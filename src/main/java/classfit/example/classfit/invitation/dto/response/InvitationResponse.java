package classfit.example.classfit.invitation.dto.response;

import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.enumType.InvitationType;
import lombok.Builder;

@Builder
public record InvitationResponse(
        String staffName,
        String email,
        String academyName,
        InvitationType status
) {
    public static InvitationResponse from(final Invitation invitation) {
        return InvitationResponse.builder()
                .staffName(invitation.getName())
                .email(invitation.getEmail())
                .academyName(invitation.getAcademy().getName())
                .status(invitation.getStatus())
                .build();
    }
}
