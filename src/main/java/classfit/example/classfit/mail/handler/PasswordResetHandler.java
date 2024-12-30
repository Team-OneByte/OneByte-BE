package classfit.example.classfit.mail.handler;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PasswordResetHandler implements EmailHandler {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(EmailPurpose purpose) {
        return purpose == EmailPurpose.PASSWORD_RESET;
    }

    @Override
    public void validate(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new ClassfitException("존재하지 않는 계정입니다.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public String getTitle() {
        return "[ClassFit] 비밀번호 재설정 인증코드";
    }

    @Override
    public long getExpirationTime() {
        return 60 * 5L; // 5분
    }

    @Override
    public Map<String, Object> getTemplateVariables(String authCode) {
        return Map.of("code", authCode);
    }
}
