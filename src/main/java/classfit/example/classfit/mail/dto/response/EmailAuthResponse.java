package classfit.example.classfit.mail.dto.response;

import lombok.Builder;

@Builder
public record EmailAuthResponse
    (
        String email
    ) {

    public static EmailAuthResponse from(final String email) {
        return EmailAuthResponse.builder()
            .email(email)
            .build();
    }
}
