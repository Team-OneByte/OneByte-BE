package classfit.example.classfit.drive.dto.response;

import classfit.example.classfit.drive.domain.Drive;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DriveFileResponse(
        String objectType,
        String objectName,
        String objectSize,
        String objectUrl,
        String objectPath,
        String uploadedBy,
        LocalDate uploadedAt
) {

    public static DriveFileResponse of(Drive drive) {
        return DriveFileResponse.builder()
                .objectType(drive.getObjectType())
                .objectName(drive.getObjectName())
                .objectSize(drive.getObjectSize())
                .objectUrl(drive.getObjectUrl())
                .objectPath(drive.getObjectPath())
                .uploadedBy(drive.getUploadedBy())
                .uploadedAt(drive.getUploadedAt())
                .build();
    }
}
