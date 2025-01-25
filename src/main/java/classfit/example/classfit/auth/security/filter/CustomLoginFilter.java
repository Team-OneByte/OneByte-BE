package classfit.example.classfit.auth.security.filter;

import classfit.example.classfit.auth.dto.request.UserRequest;
import classfit.example.classfit.auth.security.custom.CustomAuthenticationToken;
import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.common.exception.ClassfitAuthException;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.CookieUtil;
import classfit.example.classfit.common.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String CREDENTIAL = "Authorization";
    private static final String SECURITY_SCHEMA_TYPE = "Bearer ";
    private static final String ACCESS_TOKEN_CATEGORY = "access";
    private static final String REFRESH_TOKEN_CATEGORY = "refresh";

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserRequest userRequest = parseRequest(request);
        userRequest.validate().ifPresent(errorMessage -> {
            throw new ClassfitAuthException(errorMessage, HttpStatus.BAD_REQUEST);
        });

        CustomAuthenticationToken authRequest = new CustomAuthenticationToken(
            userRequest.email(), userRequest.password(), null
        );
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) {
        CustomAuthenticationToken customAuth = (CustomAuthenticationToken) authResult;
        String role = customAuth.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJwt(ACCESS_TOKEN_CATEGORY, customAuth.getEmail(), role, 1000 * 60 * 60 * 24 * 5L);
        String refreshToken = jwtUtil.createJwt(REFRESH_TOKEN_CATEGORY, customAuth.getEmail(), role, 1000 * 60 * 60 * 24 * 7L);

        redisUtil.setDataExpire(REFRESH_TOKEN_CATEGORY + ":" + customAuth.getEmail(), refreshToken, 60 * 60 * 24 * 7L);
        setResponse(res, accessToken, refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException {
        if (failed instanceof ClassfitAuthException) {
            CustomApiResponse.errorResponse(res, failed.getMessage(), ((ClassfitAuthException) failed).getHttpStatusCode());
            return;
        }
        CustomApiResponse.errorResponse(res, "아이디 또는 비밀번호가 잘못되었습니다.", HttpServletResponse.SC_UNAUTHORIZED);
    }

    private UserRequest parseRequest(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(request.getInputStream(), UserRequest.class);
        } catch (IOException e) {
            throw new ClassfitException("입력 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void setResponse(HttpServletResponse res, String accessToken, String refreshToken) {
        res.setHeader(CREDENTIAL, SECURITY_SCHEMA_TYPE + accessToken);
        CookieUtil.setCookie(res, REFRESH_TOKEN_CATEGORY, refreshToken, 7 * 24 * 60 * 60);
        res.setStatus(HttpStatus.OK.value());
    }
}
