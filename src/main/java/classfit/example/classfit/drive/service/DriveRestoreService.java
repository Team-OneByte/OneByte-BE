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
            String trashPath = generateTrashPath(member, driveType, fileName);
            List<Tag> filteredTags = getFilteredTags(trashPath);
            String folderPath = extractTags(filteredTags);

            String restoredPath = generateSourcePath(member, driveType, folderPath, fileName);
            CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, trashPath, bucketName, restoredPath);
            amazonS3.copyObject(copyRequest);

            amazonS3.deleteObject(bucketName, trashPath);
            restoredPaths.add(restoredPath);
        }

        return restoredPaths;
    }

    private String generateTrashPath(Member member, DriveType driveType, String fileName) {
        if (driveType == DriveType.PERSONAL) {
            return String.format("trash/personal/%d/%s", member.getId(), fileName);
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("trash/shared/%d/%s", academyId, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }

    private String generateSourcePath(Member member, DriveType driveType, String folderPath, String fileName) {
        String fullFolderPath = folderPath != null && !folderPath.trim().isEmpty() ? folderPath + "/" : "";
        if (driveType == DriveType.PERSONAL) {
            return String.format("personal/%d/%s%s", member.getId(), fullFolderPath, fileName);
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s%s", academyId, fullFolderPath, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
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

    private String extractTags(List<Tag> tags) {
        return tags.stream()
            .filter(tag -> tag.getKey().equals("folderPath"))
            .map(Tag::getValue)
            .findFirst()
            .orElse("");
    }
}
