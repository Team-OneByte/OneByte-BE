package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileInfo;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveGetService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileInfo> getFilesFromS3(Member member, DriveType driveType, String folderPath) {
        List<FileInfo> files = new ArrayList<>();
        String prefix = getPrefixByDriveType(member, driveType, folderPath);
        List<S3ObjectSummary> objectSummaries = getS3ObjectList(prefix);

        for (S3ObjectSummary summary : objectSummaries) {
            FileInfo fileInfo = buildFileInfo(summary);
            files.add(fileInfo);
        }
        return files;
    }

    public List<FileInfo> searchFilesByName(Member member, DriveType driveType, String fileName) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, fileName);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> normalize(fileInfo.fileName()).contains(normalize(fileName)))  // 정규화하여 필터링
            .collect(Collectors.toList());
    }

    private String getPrefixByDriveType(Member member, DriveType driveType, String folderPath) {
        String basePrefix;
        switch (driveType) {
            case PERSONAL:
                basePrefix = "personal/" + member.getId() + "/";
                break;
            case SHARED:
                Long academyId = member.getAcademy().getId();
                basePrefix = "shared/" + academyId + "/";
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 드라이브 타입");
        }
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return basePrefix;
        }

        return basePrefix + folderPath + "/";
    }

    private List<S3ObjectSummary> getS3ObjectList(String prefix) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsV2Request);
        return result.getObjectSummaries();
    }

    private FileInfo buildFileInfo(S3ObjectSummary summary) {
        String fileName = summary.getKey();
        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();

        Map<String, String> tagMap = getTagsForS3Object(fileName);
        String folderPath = tagMap.get("folderPath");
        String uploadedBy = tagMap.get("uploadedBy");
        LocalDateTime uploadedAt = parseUploadedAt(tagMap.get("uploadedAt"));
        return new FileInfo(fileName, fileUrl, folderPath, uploadedBy, uploadedAt);
    }

    private Map<String, String> getTagsForS3Object(String objectKey) {
        List<Tag> tags = amazonS3.getObjectTagging(new GetObjectTaggingRequest(bucketName, objectKey)).getTagSet();
        return tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
    }

    private LocalDateTime parseUploadedAt(String uploadedAtStr) {
        if (uploadedAtStr == null || uploadedAtStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(uploadedAtStr, formatter);
    }

    private ListObjectsV2Request createListObjectsRequest(DriveType driveType, Member member, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withDelimiter("/");

        String prefix = buildPrefix(driveType, member, folderPath);
        request.setPrefix(prefix);

        return request;
    }

    private String buildPrefix(DriveType driveType, Member member, String folderPath) {
        String basePrefix;

        if (driveType == DriveType.PERSONAL) {
            basePrefix = "personal/" + member.getId();
        } else if (driveType == DriveType.SHARED) {
            basePrefix = "shared/" + member.getAcademy().getId();
        } else {
            throw new IllegalArgumentException("지원하지 않는 드라이브 타입입니다.");
        }
        return basePrefix + "/";
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFC).trim().toLowerCase();
    }

    public List<FileInfo> classifyFilesByType(Member member, DriveType driveType, FileType filterFileType) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, "");
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> !isFolder(fileInfo))  // 폴더는 제외
            .filter(fileInfo -> {
                FileType fileType = getFileType(fileInfo.fileName());
                return fileType.equals(filterFileType);  // 필터링 조건 추가
            })
            .collect(Collectors.toList());
    }

    private boolean isFolder(FileInfo fileInfo) {
        return fileInfo.fileName().endsWith("/");
    }

    private FileType getFileType(String fileName) {
        String extension = getFileExtension(fileName);
        return FileType.getFileTypeByExtension(extension);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}