package classfit.example.classfit.auth.service;

import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.CookieUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.member.domain.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String CREDENTIAL = "Authorization";
    private static final String SECURITY_SCHEMA_TYPE = "Bearer ";
    private static final String ACCESS_TOKEN_CATEGORY = "access";
    private static final String REFRESH_TOKEN_CATEGORY = "refresh";

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies())
                        .orElseThrow(() -> new ClassfitException(ErrorCode.COOKIE_NOT_FOUND)))
                .filter(cookie -> REFRESH_TOKEN_CATEGORY.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new ClassfitException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (jwtUtil.isExpired(refreshToken)) {
            throw new ClassfitException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!REFRESH_TOKEN_CATEGORY.equals(jwtUtil.getCategory(refreshToken))) {
            throw new ClassfitException(ErrorCode.TOKEN_INVALID);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String redisKey = REFRESH_TOKEN_CATEGORY + ":" + email;
        String existedToken = redisUtil.getData(redisKey);

        if (existedToken == null) {
            throw new ClassfitException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String newAccess = jwtUtil.createJwt(ACCESS_TOKEN_CATEGORY, email, role, 1000 * 60 * 5L);
        String newRefresh = jwtUtil.createJwt(REFRESH_TOKEN_CATEGORY, email, role, 1000 * 60 * 60 * 24 * 7L);

        redisUtil.deleteData(redisKey);
        redisUtil.setDataExpire(redisKey, newRefresh, 1000 * 60 * 60 * 24 * 7L);

        ResponseCookie responseCookie = CookieUtil.setCookie(REFRESH_TOKEN_CATEGORY, newRefresh, 60 * 60 * 24 * 7L);
        response.setHeader(CREDENTIAL, SECURITY_SCHEMA_TYPE + newAccess);
        response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void logout(Member member) {
        String redisRefreshTokenKey = REFRESH_TOKEN_CATEGORY + ":" + member.getEmail();
        String refreshToken = redisUtil.getData(redisRefreshTokenKey);

        if (jwtUtil.isExpired(refreshToken) || refreshToken == null) {
            throw new ClassfitException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        redisUtil.deleteData(redisRefreshTokenKey);
    }
}
