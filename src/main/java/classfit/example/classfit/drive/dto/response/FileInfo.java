package classfit.example.classfit.drive.dto.response;

import java.time.LocalDateTime;

public record FileInfo(
        String fileName,
        String fileUrl,
        String uploadedBy,
        LocalDateTime uploadedAt
) {
}
