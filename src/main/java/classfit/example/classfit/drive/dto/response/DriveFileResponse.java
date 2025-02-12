package classfit.example.classfit.drive.dto.response;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.Drive;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DriveFileResponse(
        String objectType,
        String objectName,
        String originObjectName,
        String objectSize,
        String objectUrl,
        String uploadedBy,
        LocalDate uploadedAt
) {

    public static DriveFileResponse of(Drive drive) {
        return DriveFileResponse.builder()
                .objectType(drive.getObjectType())
                .objectName(drive.getObjectName())
                .originObjectName(DriveUtil.formatObjectName(drive.getObjectName()))
                .objectSize(drive.getObjectSize())
                .objectUrl(drive.getObjectUrl())
                .uploadedBy(drive.getUploadedBy())
                .uploadedAt(drive.getUploadedAt())
                .build();
    }
}
