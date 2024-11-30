package classfit.example.classfit.auth.service;

import classfit.example.classfit.auth.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.RedisUtil;
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
        String redisKey = "Refresh Token : " + email;
        String existedToken = redisUtil.getData(redisKey);

        if (existedToken == null) {
            throw new ClassfitException("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String newAccess = jwtUtil.createJwt("access", email, role, 1000 * 60 * 3L);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, 1000 * 60 * 60 * 24 * 7L);

        redisUtil.deleteData(redisKey);
        addRefreshEntity(email, newRefresh, 1000 * 60 * 60 * 24 * 7L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
        return response;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);

        return cookie;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        String redisKey = "Refresh Token : " + email;
        redisUtil.setDataExpire(redisKey, refresh, expiredMs);
    }
}
