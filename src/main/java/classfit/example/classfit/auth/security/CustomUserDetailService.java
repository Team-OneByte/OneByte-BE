package classfit.example.classfit.auth.security;

import classfit.example.classfit.auth.dto.request.CustomUserDetails;
import classfit.example.classfit.common.exception.ClassfitException;
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
            .map(member -> {
                if (member.getAcademy() == null) {
                    throw new ClassfitException("해당 회원은 학원이 등록되지 않았습니다. 학원을 등록해 주세요.", HttpStatus.BAD_REQUEST);
                }
                return new CustomUserDetails(member);
            })
            .orElseThrow(() -> new ClassfitException("해당 계정은 존재하지 않습니다. 이메일: " + email, HttpStatus.NOT_FOUND));
    }
}
