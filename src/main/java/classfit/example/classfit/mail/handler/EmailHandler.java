package classfit.example.classfit.mail.handler;

import classfit.example.classfit.mail.dto.request.EmailPurpose;

import java.util.Map;

public interface EmailHandler {
    boolean supports(EmailPurpose purpose);

    void validate(String email);

    String getTitle();

    long getExpirationTime();

    Map<String, Object> getTemplateVariables(String authCode);
}
