package classfit.example.classfit.invitation.dto.response;

import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.InvitationStatus;
import lombok.Builder;

@Builder
public record InvitationResponse
    (
        String staffName,

        String email,

        String academyName,

        InvitationStatus status
    ) {

    public static InvitationResponse from(Invitation invitation) {
        return InvitationResponse.builder()
            .staffName(invitation.getName())
            .email(invitation.getEmail())
            .academyName(invitation.getAcademy().getName())
            .status(invitation.getStatus())
            .build();
    }
}
