package classfit.example.classfit.mail.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailResponse(
        String email,
        String emailToken
) {
    public static EmailResponse of(final String email) {
        return EmailResponse.builder()
                .email(email)
                .build();
    }

    public static EmailResponse from(final String email, final String emailToken) {
        return EmailResponse.builder()
                .email(email)
                .emailToken(emailToken)
                .build();
    }
}
