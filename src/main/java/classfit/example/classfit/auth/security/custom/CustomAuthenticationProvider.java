package classfit.example.classfit.auth.security.custom;

import classfit.example.classfit.common.exception.ClassfitAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Authentication result = super.authenticate(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) result.getPrincipal();

        if (userDetails.member().getAcademy() == null) {
            throw new ClassfitAuthException("해당 회원은 학원이 등록되지 않았습니다.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new CustomAuthenticationToken(
            authentication.getName(),
            authentication.getCredentials().toString(),
            result.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}