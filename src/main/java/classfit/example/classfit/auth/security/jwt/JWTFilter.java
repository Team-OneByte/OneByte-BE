package classfit.example.classfit.auth.security.jwt;

import classfit.example.classfit.auth.security.custom.CustomUserDetailService;
import classfit.example.classfit.auth.security.custom.CustomUserDetails;
import classfit.example.classfit.common.exception.ClassfitException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final CustomUserDetailService customUserDetailService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String access = jwtUtil.resolveToken(request);

        if (access == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(access)) {
                throw new ClassfitException("엑세스 토큰이 만료되었습니다.", HttpStatus.REQUEST_TIMEOUT);
            }

            if (!"access".equals(jwtUtil.getCategory(access))) {
                throw new ClassfitException("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }

            String email = jwtUtil.getEmail(access);
            CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(email);

            Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ClassfitException ex) {
            request.setAttribute("exception", ex);
            throw new AuthenticationException(ex.getMessage()) {
            };
        }

    }
}
