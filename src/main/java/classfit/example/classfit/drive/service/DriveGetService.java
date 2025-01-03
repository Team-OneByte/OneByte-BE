package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.dto.response.FileInfo;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveGetService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileInfo> getFilesFromS3(Member member, DriveType driveType) {
        List<FileInfo> files = new ArrayList<>();
        String prefix = getPrefixByDriveType(member, driveType);
        List<S3ObjectSummary> objectSummaries = getS3ObjectList(prefix);

        for (S3ObjectSummary summary : objectSummaries) {
            FileInfo fileInfo = buildFileInfo(summary);
            files.add(fileInfo);
        }
        return files;
    }

    private String getPrefixByDriveType(Member member, DriveType driveType) {
        switch (driveType) {
            case PERSONAL:
            return "personal/" + member.getId() + "/";
            case SHARED:
                Long academyId = member.getAcademy().getId();
                return "shared/" + academyId + "/";
            default:
                throw new IllegalArgumentException("유효하지 않은 드라이브 타입");
        }
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
        String uploadedBy = tagMap.get("uploadedBy");
        LocalDateTime uploadedAt = parseUploadedAt(tagMap.get("uploadedAt"));
        return new FileInfo(fileName, fileUrl, uploadedBy, uploadedAt);
    }

    private Map<String, String> getTagsForS3Object(String objectKey) {
        List<Tag> tags = amazonS3.getObjectTagging(new GetObjectTaggingRequest(bucketName, objectKey)).getTagSet();
        return tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
    }

    private LocalDateTime parseUploadedAt(String uploadedAtStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(uploadedAtStr, formatter);
    }
}