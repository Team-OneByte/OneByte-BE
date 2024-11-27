package classfit.example.classfit.mail.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailAuthResponse
    (
        String email,
        String emailToken
    ) {

    public static EmailAuthResponse of(final String email) {
        return EmailAuthResponse.builder()
            .email(email)
            .build();
    }

    public static EmailAuthResponse from(final String email, final String emailToken) {
        return EmailAuthResponse.builder()
            .email(email)
            .emailToken(emailToken)
            .build();
    }
}
