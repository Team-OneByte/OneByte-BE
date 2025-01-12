package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.Tag;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.DriveType.SHARED;

public class DriveUtil {

    public static String generatedOriginPath(Member member, DriveType driveType, String folderPath, String fileName) {
        String fullFolderPath = folderPath != null && !folderPath.trim().isEmpty() ? folderPath + "/" : "";

        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s%s", member.getId(), fullFolderPath, fileName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s%s", academyId, fullFolderPath, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }

    public static String generateTrashPath(Member member, DriveType driveType, String fileName) {

        String basePath;

        if (driveType == DriveType.PERSONAL) {
            basePath = String.format("trash/personal/%d/", member.getId());
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            basePath = String.format("trash/shared/%d/", academyId);
        } else {
            throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
        }

        return (fileName != null && !fileName.trim().isEmpty()) ? basePath + fileName : basePath;
    }

    public static String extractTags(List<Tag> tags, String tagKey) {
        return tags.stream()
            .filter(tag -> tag.getKey().equals(tagKey))
            .map(Tag::getValue)
            .findFirst()
            .orElse("");
    }

    public static LocalDateTime parseUploadedAt(String uploadedAtStr) {
        return (uploadedAtStr == null || uploadedAtStr.isBlank())
            ? LocalDateTime.now()
            : LocalDateTime.parse(uploadedAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static FileType getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            return FileType.getFileTypeByExtension(extension);
        }
        return FileType.UNKNOWN;
    }

    public static String formatFileSize(long sizeInBytes) {
        double sizeInKB = sizeInBytes / 1024.0;
        if (sizeInKB >= 1024) {
            // 1MB 이상이면 MB 단위로 표시
            double sizeInMB = sizeInKB / 1024.0;
            return String.format("%.1f MB", sizeInMB); // 소수점 한 자리까지 MB로 표시
        }
        // 1MB 미만이면 KB 단위로 표시
        return String.format("%.1f KB", sizeInKB); // 소수점 한 자리까지 KB로 표시
    }

    public static String buildPrefix(DriveType driveType, Member member, String folderPath) {
        String basePrefix;

        if (driveType == DriveType.PERSONAL) {
            basePrefix = "personal/" + member.getId() + "/";
        } else if (driveType == DriveType.SHARED) {
            basePrefix = "shared/" + member.getAcademy().getId() + "/";
        } else {
            throw new IllegalArgumentException("지원하지 않는 드라이브 타입입니다.");
        }
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return basePrefix;
        }

        return basePrefix + folderPath + "/";
    }
}
