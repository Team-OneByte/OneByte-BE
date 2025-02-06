package classfit.example.classfit.drive.dto.response;

import classfit.example.classfit.drive.domain.enumType.FileType;

import java.time.LocalDateTime;

public record FileResponse(
        FileType fileType,
        String originalFileName,
        String fileName,
        String fileSize,
        String fileUrl,
        String folderPath,
        String uploadedBy,
        LocalDateTime uploadedAt
) {
}
