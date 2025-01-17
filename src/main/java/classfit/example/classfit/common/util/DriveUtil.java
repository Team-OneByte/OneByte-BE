package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

    public static FileResponse getFileResponse(
        AmazonS3 amazonS3,
        String bucketName,
        S3ObjectSummary summary,
        String fileName,
        String fileUrl,
        Map<String, String> tagMap
    ) {
        FileType fileType = getFileType(fileName);
        String originalFileName = tagMap.getOrDefault("originalFileName", "");
        String fileNameWithoutPrefix = getFileNameWithoutPrefix(fileName);
        LocalDateTime uploadedAt = parseUploadedAt(tagMap.get("uploadedAt"));

        long fileSize = summary.getSize();
        if (fileType == FileType.FOLDER) {
            fileSize = calculateFolderSize(amazonS3, bucketName, fileName);
        }
        String formattedFileSize = formatFileSize(fileSize);
        return new FileResponse(
            fileType,
            originalFileName,
            fileNameWithoutPrefix,
            formattedFileSize,
            fileUrl,
            tagMap.getOrDefault("folderPath", ""),
            tagMap.getOrDefault("uploadedBy", ""),
            uploadedAt
        );
    }
    private static long calculateFolderSize(AmazonS3 amazonS3, String bucketName, String folderPath) {
        long totalSize = 0;

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(folderPath)
            .withDelimiter("/");

        ListObjectsV2Result listObjectsResponse = amazonS3.listObjectsV2(listObjectsRequest);
        for (S3ObjectSummary summary : listObjectsResponse.getObjectSummaries()) {
            totalSize += summary.getSize();
        }
        for (String commonPrefix : listObjectsResponse.getCommonPrefixes()) {
            totalSize += calculateFolderSize(amazonS3, bucketName, commonPrefix);
        }
        return totalSize;
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
