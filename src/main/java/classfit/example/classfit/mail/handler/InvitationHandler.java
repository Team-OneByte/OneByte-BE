package classfit.example.classfit.mail.handler;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.SecurityUtil;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvitationHandler implements EmailHandler {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(EmailPurpose purpose) {
        return purpose == EmailPurpose.INVITATION;
    }

    @Override
    public void validate(String email) {

    }

    @Override
    public String getTitle() {
        return "[ClassFit] 초대 코드 확인";
    }

    @Override
    public long getExpirationTime() {
        return 10L;
    }

    @Override
    public Map<String, Object> getTemplateVariables(String authCode) {
        Academy academy = getAcademy();
        return Map.of("code", academy.getCode());
    }

    private Academy getAcademy() {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getAcademy();
    }
}
