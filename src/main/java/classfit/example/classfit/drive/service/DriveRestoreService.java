package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveRestoreService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> restoreFromTrash(Member member, DriveType driveType, List<String> fileNames) {
        List<String> restoredPaths = new ArrayList<>();

        for (String fileName : fileNames) {
            String trashPath = DriveUtil.generateTrashPath(member, driveType, fileName);
            List<Tag> filteredTags = getFilteredTags(trashPath);
            String folderPath = DriveUtil.extractTags(filteredTags, "folderPath");

            String restoredPath = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);
            CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, trashPath, bucketName, restoredPath);
            amazonS3.copyObject(copyRequest);

            amazonS3.deleteObject(bucketName, trashPath);
            restoredPaths.add(restoredPath);
        }

        return restoredPaths;
    }

    private List<Tag> getFilteredTags(String trashPath) {
        GetObjectTaggingRequest taggingRequest = new GetObjectTaggingRequest(bucketName, trashPath);
        GetObjectTaggingResult taggingResult = amazonS3.getObjectTagging(taggingRequest);

        List<Tag> filteredTags = taggingResult.getTagSet().stream()
            .filter(tag -> !tag.getKey().equals("deletedBy") && !tag.getKey().equals("deleteAt"))
            .collect(Collectors.toList());

        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, trashPath, new ObjectTagging(filteredTags)));
        return filteredTags;
    }
}
