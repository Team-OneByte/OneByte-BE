package classfit.example.classfit.auth.exception;

import classfit.example.classfit.common.exception.ClassfitAuthException;
import classfit.example.classfit.common.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final static String DEFAULT_FAILURE_MESSAGE = "로그인에 실패하였습니다.";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof ClassfitAuthException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = DEFAULT_FAILURE_MESSAGE;
        }

        CustomApiResponse.errorResponse(response, errorMessage, HttpStatus.UNAUTHORIZED.value());
    }
}
