package classfit.example.classfit.auth.filter;

import classfit.example.classfit.auth.custom.CustomAuthenticationToken;
import classfit.example.classfit.common.util.CookieUtil;
import classfit.example.classfit.common.util.JWTUtil;
import classfit.example.classfit.common.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), new TypeReference<>() {
            });

            String email = requestMap.get("email");
            String password = requestMap.get("password");

            CustomAuthenticationToken authRequest = new CustomAuthenticationToken(email, password, null);

            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) {
        CustomAuthenticationToken customAuth = (CustomAuthenticationToken) authResult;
        String role = customAuth.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJwt(ACCESS_TOKEN_CATEGORY, customAuth.getEmail(), role, 1000 * 60 * 5L);
        String refreshToken = jwtUtil.createJwt(REFRESH_TOKEN_CATEGORY, customAuth.getEmail(), role, 1000 * 60 * 60 * 24 * 7L);
        redisUtil.setDataExpire(REFRESH_TOKEN_CATEGORY + ":" + customAuth.getEmail(), refreshToken, 60 * 60 * 24 * 7L);

        res.setHeader(CREDENTIAL, SECURITY_SCHEMA_TYPE + accessToken);
        ResponseCookie responseCookie = CookieUtil.setCookie(REFRESH_TOKEN_CATEGORY, refreshToken, 7 * 24 * 60 * 60);
        res.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        res.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(req, res, failed);
    }
}
