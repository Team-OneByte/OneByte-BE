package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.dto.response.FileResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileResponse> getFilesFromTrash(Member member, DriveType driveType) {
        String prefix = DriveUtil.generateTrashPath(member, driveType, null);

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(prefix);
        List<S3ObjectSummary> objectSummaries = amazonS3.listObjectsV2(listObjectsV2Request).getObjectSummaries();

        return objectSummaries.stream()
            .map(this::buildFileTrashInfo)
            .toList();
    }

    private FileResponse buildFileTrashInfo(S3ObjectSummary summary) {
        String fileName = summary.getKey();
        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();

        Map<String, String> tagMap = amazonS3.getObjectTagging(new GetObjectTaggingRequest(bucketName, fileName))
            .getTagSet().stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));

        return DriveUtil.getFileResponse(summary, fileName, fileUrl, tagMap);
    }

    public List<String> moveToTrash(Member member, DriveType driveType, String folderPath, List<String> fileNames) {
        List<String> trashPaths = new ArrayList<>();

        for (String fileName : fileNames) {
            String originPath = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);
            String trashPath = DriveUtil.generateTrashPath(member, driveType, fileName);

            CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, originPath, bucketName, trashPath);
            amazonS3.copyObject(copyRequest);

            addDeleteTagsToS3Object(trashPath, member);
            amazonS3.deleteObject(bucketName, originPath);

            trashPaths.add(trashPath);
        }

        createPlaceHolder(member, driveType, folderPath);
        return trashPaths;
    }

    private void createPlaceHolder(Member member, DriveType driveType, String folderPath) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        String fullFolderPath = DriveUtil.generatedOriginPath(member, driveType, folderPath, "");
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        amazonS3.putObject(new PutObjectRequest(bucketName, fullFolderPath, emptyContent, metadata));
    }

    private void addDeleteTagsToS3Object(String objectKey, Member member) {
        GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(bucketName, objectKey);
        GetObjectTaggingResult taggingResult = amazonS3.getObjectTagging(getTaggingRequest);

        List<Tag> existingTags = taggingResult.getTagSet();
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);

        List<Tag> updatedTags = Stream.concat(
            existingTags.stream(),
            Stream.of(
                new Tag("deletedBy", member.getName()),
                new Tag("deleteAt", formattedDate)
            )
        ).collect(Collectors.toList());

        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(updatedTags)));
    }
}
