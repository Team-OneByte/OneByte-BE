package classfit.example.classfit.invitation.dto.request;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.enumType.InvitationType;
import jakarta.validation.constraints.Email;

public record InvitationRequest(
        String name,

        @Email(message = "이메일 형식과 올바르지 않습니다.")
        String email
) {
    public Invitation toEntity(final Academy academy) {
        return Invitation.builder()
                .name(name())
                .email(email())
                .status(InvitationType.IN_PROGRESS)
                .academy(academy)
                .build();
    }
}
