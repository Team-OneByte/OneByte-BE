package classfit.example.classfit.invitation.domain.enumType;

import lombok.Getter;

@Getter
public enum InvitationType {
    IN_PROGRESS("초대 중"),
    COMPLETED("초대 완료");

    private final String description;

    InvitationType(String description) {
        this.description = description;
    }
}