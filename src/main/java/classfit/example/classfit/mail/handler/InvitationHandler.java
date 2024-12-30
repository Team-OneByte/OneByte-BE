package classfit.example.classfit.mail.handler;

import classfit.example.classfit.mail.dto.request.EmailPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvitationHandler implements EmailHandler {


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
        return -1;
    }

    @Override
    public Map<String, Object> getTemplateVariables(String authCode) {
        return Map.of("code", authCode, "extraInfo", "초대 관련 메시지");
    }
}
