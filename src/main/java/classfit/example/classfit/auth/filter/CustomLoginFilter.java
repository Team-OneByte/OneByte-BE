package classfit.example.classfit.auth.filter;

import classfit.example.classfit.auth.dto.request.UserRequest;
import classfit.example.classfit.auth.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        setFilterProcessesUrl("/api/v1/login");
    }

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userRequest.email(), userRequest.password(), null);
            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            throw new ClassfitException("로그인 요청의 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String access = jwtUtil.createJwt("access", authResult.getName(), role, 1000 * 60 * 3L);                 // 3초
        String refresh = jwtUtil.createJwt("refresh", authResult.getName(), role, 1000 * 60 * 60 * 24 * 7L); //7일

        addRefreshEntity(authResult.getName(), refresh, 1000 * 60 * 60 * 24 * 7L);

        res.setHeader("Authorization", "Bearer " + access);
        res.addCookie(createCookie("refresh", refresh));
        res.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
            "{ \"message\": \"로그인에 실패하였습니다. 이메일 또는 비밀번호를 확인해주세요.\", \"status\": %d }",
            HttpServletResponse.SC_UNAUTHORIZED
        );

        response.getWriter().write(jsonResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        String redisKey = "Refresh Token : " + email;
        redisUtil.setDataExpire(redisKey, refresh, expiredMs);
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return obtainEmail(request);
    }

    private String obtainEmail(HttpServletRequest request) {
        return request.getParameter("email");
    }
}
