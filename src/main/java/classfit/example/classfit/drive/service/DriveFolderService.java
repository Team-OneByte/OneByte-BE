package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;

@Service
@RequiredArgsConstructor
public class DriveFolderService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String createFolder(Member member, DriveType driveType, String folderName, String folderPath) {
        String uniqueFolderName = generateUniqueFolderName(member, driveType, folderName, folderPath);
        String fullFolderPath = generateFolderKey(member, driveType, uniqueFolderName, folderPath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        amazonS3.putObject(new PutObjectRequest(bucketName, fullFolderPath, emptyContent, metadata));
        addUploadTagsToS3Object(fullFolderPath, member, uniqueFolderName);
        return fullFolderPath;
    }

    private String generateUniqueFolderName(Member member, DriveType driveType, String folderName, String folderPath) {
        String baseName = folderName;
        int count = 1;

        while (doesFolderExist(generateFolderKey(member, driveType, baseName, folderPath))) {
            baseName = folderName + count;
            count++;
        }

        return baseName;
    }

    private boolean doesFolderExist(String folderKey) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(folderKey)
            .withMaxKeys(1);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return !result.getObjectSummaries().isEmpty();
    }

    private String generateFolderKey(Member member, DriveType driveType, String folderName, String folderPath) {
        String basePath = driveType == PERSONAL
            ? String.format("personal/%d/", member.getId())
            : String.format("shared/%d/", member.getAcademy().getId());

        return basePath + (folderPath.isEmpty() ? "" : folderPath + "/") + folderName + "/";
    }

    private void addUploadTagsToS3Object(String objectKey, Member member, String uniqueFolderName) {
        String folderPathWithoutPrefix = getFolderPathWithoutPrefix(objectKey);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
        List<Tag> tags = List.of(
            new Tag("folderPath", folderPathWithoutPrefix),
            new Tag("originalFileName", uniqueFolderName),
            new Tag("uploadedBy", member.getName()),
            new Tag("uploadedAt", formattedDate)
        );
        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(tags)));
    }

    private String getFolderPathWithoutPrefix(String objectKey) {
        String folderPathWithoutPrefix = objectKey.replaceFirst("^personal/\\d+/|^shared/\\d+/", "");
        folderPathWithoutPrefix = folderPathWithoutPrefix.replaceAll("[^a-zA-Z0-9-_./]", "").trim();
        return folderPathWithoutPrefix;
    }

    public List<String> getFolders(Member member, DriveType driveType, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withDelimiter("/");

        String prefix = DriveUtil.buildPrefix(driveType, member, folderPath);
        request.setPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getCommonPrefixes().stream()
            .map(folder -> folder.substring(prefix.length()))
            .collect(Collectors.toList());
    }
}
