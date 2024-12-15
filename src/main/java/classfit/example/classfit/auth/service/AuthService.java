package classfit.example.classfit.auth.service;

import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.member.domain.Member;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    public HttpServletResponse reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new ClassfitException("Refresh Token이 만료되었습니다.", HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtil.getEmail(refresh);
        String category = jwtUtil.getCategory(refresh);
        String role = jwtUtil.getRole(refresh);

        if (!category.equals("refresh")) {
            throw new ClassfitException("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        String redisKey = "refresh:" + email;
        String existedToken = redisUtil.getData(redisKey);

        if (existedToken == null) {
            throw new ClassfitException("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String newAccess = jwtUtil.createJwt("access", email, role, 1000 * 60 * 5L);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, 1000 * 60 * 60 * 24 * 7L);

        redisUtil.deleteData(redisKey);
        addRefreshEntity(email, newRefresh, 1000 * 60 * 60 * 24 * 7L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
        return response;
    }

    public void logout(Member member) {
        String redisRefreshTokenKey = "refresh:" + member.getEmail();

        String refreshToken = redisUtil.getData(redisRefreshTokenKey);

        if (jwtUtil.isExpired(refreshToken) || refreshToken == null) {
            throw new ClassfitException("Refresh 토큰이 유효하지 않거나 만료되었습니다..", HttpStatus.NOT_FOUND);
        }

        redisUtil.deleteData(redisRefreshTokenKey);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);

        return cookie;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        String redisKey = "refresh:" + email;
        redisUtil.setDataExpire(redisKey, refresh, expiredMs);
    }
}
