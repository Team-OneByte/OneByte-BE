package classfit.example.classfit.auth.filter;

import classfit.example.classfit.auth.custom.CustomUserDetailService;
import classfit.example.classfit.auth.custom.CustomUserDetails;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_CATEGORY = "access";
    private final CustomUserDetailService customUserDetailService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.resolveToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(accessToken)) {
                throw new ClassfitException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }

            if (!ACCESS_TOKEN_CATEGORY.equals(jwtUtil.getCategory(accessToken))) {
                throw new ClassfitException(ErrorCode.TOKEN_INVALID);
            }

            String email = jwtUtil.getEmail(accessToken);
            CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(email);

            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (ClassfitException ex) {
            CustomApiResponse.errorResponse(response, ex.getMessage(), ex.getHttpStatusCode());
        }
    }
}
