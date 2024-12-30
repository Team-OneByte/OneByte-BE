package classfit.example.classfit.invitation.dto.request;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.InvitationStatus;
import jakarta.validation.constraints.Email;

public record InvitationRequest
    (
        String name,

        @Email
        String email
    ) {

    public Invitation toEntity(Academy academy) {

        return Invitation.builder()
            .name(name())
            .email(email())
            .status(InvitationStatus.IN_PROGRESS)
            .academy(academy)
            .build();
    }
}
