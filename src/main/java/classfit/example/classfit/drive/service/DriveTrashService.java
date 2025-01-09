package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

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
        return trashPaths;
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
