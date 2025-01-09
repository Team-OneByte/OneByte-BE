package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String moveToTrash(Member member, DriveType driveType, String path) {
        return isFolderPath(path)
            ? moveFolderToTrash(member, driveType, path)
            : moveFileToTrash(member, driveType, path);
    }

    private boolean isFolderPath(String path) {
        return path.endsWith("/");
    }

    private String moveFileToTrash(Member member, DriveType driveType, String fileName) {
        String filePath = generateFullPath(member, driveType, fileName);
        return moveS3ToTrash(filePath, generateTrashPath(driveType, filePath), member);
    }

    private String moveFolderToTrash(Member member, DriveType driveType, String folderPath) {
        List<S3ObjectSummary> objectSummaries = listS3ByPrefix(generateFullPath(member, driveType, folderPath));

        if (objectSummaries.isEmpty()) {
            return moveS3ToTrash(folderPath, generateTrashPath(driveType, folderPath), member);
        }

        objectSummaries.forEach(summary -> {
            String filePath = summary.getKey();
            moveS3ToTrash(filePath, generateTrashPath(driveType, filePath), member);
        });

        return folderPath;
    }

    private String moveS3ToTrash(String sourcePath, String trashPath, Member member) {
        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, sourcePath, bucketName, trashPath);
        amazonS3.copyObject(copyRequest);

        addDeleteTagsToS3Object(trashPath, member);

        amazonS3.deleteObject(bucketName, sourcePath);
        return trashPath;
    }

    private List<S3ObjectSummary> listS3ByPrefix(String prefix) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(prefix);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return result.getObjectSummaries();
    }

    private String generateFullPath(Member member, DriveType driveType, String fileName) {
        if (driveType == DriveType.PERSONAL) {
            return String.format("personal/%d/%s", member.getId(), fileName);
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s", academyId, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }

    private String generateTrashPath(DriveType driveType, String filePath) {
        if (driveType == DriveType.PERSONAL) {
            return String.format("trash/%s", filePath);
        } else if (driveType == DriveType.SHARED) {
            return String.format("trash/%s", filePath);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }

    private void addDeleteTagsToS3Object(String objectKey, Member member) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);

        List<Tag> tags = List.of(
            new Tag("deletedBy", member.getName()),
            new Tag("deleteAt", formattedDate)
        );

        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(tags)));
    }
}
