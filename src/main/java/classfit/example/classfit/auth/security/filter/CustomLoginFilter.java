package classfit.example.classfit.auth.security.filter;

import classfit.example.classfit.auth.dto.request.UserRequest;
import classfit.example.classfit.auth.security.custom.CustomAuthenticationToken;
import classfit.example.classfit.auth.security.jwt.JWTUtil;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    {
        setFilterProcessesUrl("/api/v1/signin");
    }

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);

            CustomAuthenticationToken authRequest = new CustomAuthenticationToken(userRequest.email(), userRequest.password(), null);
            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            throw new ClassfitException("입력 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) {

        CustomAuthenticationToken customAuth = (CustomAuthenticationToken) authResult;

        Collection<? extends GrantedAuthority> authorities = customAuth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();


        String role = auth.getAuthority();
        String access = jwtUtil.createJwt("access", customAuth.getEmail(), role, 1000 * 60 * 5L);             // 5분
        String refresh = jwtUtil.createJwt("refresh", customAuth.getEmail(), role, 1000 * 60 * 60 * 24 * 7L); // 7일

        addRefreshEntity(authResult.getName(), refresh, 1000 * 60 * 60 * 24 * 7L);

        res.setHeader("Authorization", "Bearer " + access);
        CookieUtil.addCookie(res, "refresh", refresh, 7 * 24 * 60 * 60);
        res.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (failed.getCause() instanceof ClassfitException classfitException) {
            response.setStatus(classfitException.getHttpStatus().value());
            String jsonResponse = String.format(
                "{ \"message\": \"%s\", \"status\": %d }",
                classfitException.getMessage(),
                classfitException.getHttpStatus().value()
            );
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonResponse = String.format(
                "{ \"message\": \"로그인에 실패하였습니다. 이메일 또는 비밀번호를 확인해주세요.\", \"status\": %d }",
                HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter().write(jsonResponse);
        }
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        String redisKey = "refresh:" + email;
        redisUtil.setDataExpire(redisKey, refresh, expiredMs);
    }
}
