package classfit.example.classfit.invitation.domain;

import lombok.Getter;

@Getter
public enum InvitationStatus {
    IN_PROGRESS("초대 중"),
    COMPLETED("초대 완료");

    private final String description;

    InvitationStatus(String description) {
        this.description = description;
    }
}