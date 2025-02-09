package classfit.example.classfit.drive.dto.response;

import classfit.example.classfit.drive.domain.enumType.ObjectType;

import java.time.LocalDateTime;

public record FileResponse(
        ObjectType objectType,
        String originalFileName,
        String fileName,
        String fileSize,
        String fileUrl,
        String folderPath,
        String uploadedBy,
        LocalDateTime uploadedAt
) {
}
