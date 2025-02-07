package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.domain.enumType.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static classfit.example.classfit.drive.domain.enumType.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.enumType.DriveType.SHARED;

public class DriveUtil {

    public static String generatedOriginPath(Member member, DriveType driveType, String folderPath, String fileName) {
        String fullFolderPath = folderPath != null && !folderPath.trim().isEmpty() ? folderPath + "/" : "";

        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s%s", member.getId(), fullFolderPath, fileName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s%s", academyId, fullFolderPath, fileName);
        }
        throw new ClassfitException(ErrorCode.DRIVE_TYPE_INVALID);
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

    public static FileResponse getFileResponse(S3ObjectSummary summary, String fileName,
                                               String fileUrl,
                                               Map<String, String> tagMap) {
        FileType fileType = getFileType(fileName);
        String originalFileName = tagMap.getOrDefault("originalFileName", "");
        String fileNameWithoutPrefix = getFileNameWithoutPrefix(fileName);
        LocalDateTime uploadedAt = parseUploadedAt(tagMap.get("uploadedAt"));
        String fileSize = formatFileSize(summary.getSize());

        return new FileResponse(
            fileType,
            originalFileName,
            fileNameWithoutPrefix,
            fileSize,
            fileUrl,
            tagMap.getOrDefault("folderPath", ""),
            tagMap.getOrDefault("uploadedBy", ""),
            uploadedAt
        );
    }

    public static FileType getFileType(String fileName) {
        if (fileName.endsWith("/")) {
            return FileType.FOLDER;
        }
        String extension = getFileExtension(fileName);
        return FileType.getFileTypeByExtension(extension);
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    private static LocalDateTime parseUploadedAt(String uploadedAtStr) {
        return (uploadedAtStr == null || uploadedAtStr.isBlank())
            ? LocalDateTime.now()
            : LocalDateTime.parse(uploadedAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String formatFileSize(long sizeInBytes) {
        double sizeInKB = sizeInBytes / 1024.0;
        if (sizeInKB >= 1024) {
            double sizeInMB = sizeInKB / 1024.0;
            return String.format("%.1f MB", sizeInMB);
        }
        return String.format("%.1f KB", sizeInKB);
    }

    private static String getFileNameWithoutPrefix(String objectKey) {
        return objectKey.replaceFirst("^personal/\\d+/|^shared/\\d+/", "");
    }
}
