package classfit.example.classfit.auth.security.provider;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String email;

    public CustomAuthenticationToken(String email, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(email, credentials, authorities);
        this.email = email;
    }
}

