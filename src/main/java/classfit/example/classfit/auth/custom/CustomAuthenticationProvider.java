package classfit.example.classfit.auth.custom;

import classfit.example.classfit.common.exception.ClassfitAuthException;
import classfit.example.classfit.common.response.ErrorCode;
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
            throw new ClassfitAuthException(ErrorCode.MEMBER_ACADEMY_INVALID);
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