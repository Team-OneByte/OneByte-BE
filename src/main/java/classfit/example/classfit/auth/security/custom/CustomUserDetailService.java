package classfit.example.classfit.auth.security.custom;

import classfit.example.classfit.common.exception.ClassfitAuthException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new ClassfitAuthException(ErrorCode.EMAIL_NOT_FOUND));
    }
}
