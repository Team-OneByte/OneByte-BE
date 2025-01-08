package classfit.example.classfit.drive.dto.response;

import java.time.LocalDateTime;

public record FileInfo(
        String fileName,
        String fileUrl,
        String folderPath,
        String uploadedBy,
        LocalDateTime uploadedAt
) {
}
