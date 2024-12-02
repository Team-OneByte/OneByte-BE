package classfit.example.classfit.auth.jwt;

import classfit.example.classfit.common.exception.ClassfitException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 요청에 저장된 예외 확인
        Throwable exception = (Throwable) request.getAttribute("exception");

        if (exception instanceof ClassfitException classfitException) {
            log.error("ClassfitException 확인: {}", classfitException.getMessage());
            response.setStatus(classfitException.getHttpStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"" + classfitException.getMessage() + "\", \"status\": " + classfitException.getHttpStatus().value() + "}");
        } else {
            log.error("기타 예외 처리: {}", authException.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Unauthorized\", \"status\": 401}");
        }
    }
}