package classfit.example.classfit.drive.service;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;

import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        addTagsToS3Object(fullFolderPath, member);
        return fullFolderPath;
    }

    private String generateUniqueFolderName(Member member, DriveType driveType, String folderName, String folderPath) {
        String baseName = folderName;
        int count = 1;

        while (doesFolderExist(generateFolderKey(member, driveType, baseName, folderPath))) {
            baseName = folderName + " (" + count + ")";
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

    private void addTagsToS3Object(String objectKey, Member member) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
        List<Tag> tags = List.of(
            new Tag("uploadedBy", member.getName()),
            new Tag("uploadedAt", formattedDate)
        );
        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(tags)));
    }

    public List<String> getFolders(Member member, DriveType driveType, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withDelimiter("/");

        String prefix = buildPrefix(driveType, member, folderPath);
        request.setPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getCommonPrefixes().stream()
            .map(folder -> folder.substring(prefix.length()))
            .collect(Collectors.toList());
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
        return folderPath.isEmpty() ? basePrefix + "/" : basePrefix + "/" + folderPath + "/";
    }
}
