package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveRestoreService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String restoreFromTrash(Member member, DriveType driveType, String fileName) {
        String trashPath = generateTrashPath(member, driveType, fileName);

        GetObjectTaggingRequest taggingRequest = new GetObjectTaggingRequest(bucketName, trashPath);
        GetObjectTaggingResult taggingResult = amazonS3.getObjectTagging(taggingRequest);

        Map<String, String> tagMap = taggingResult.getTagSet().stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));

        String folderPath = tagMap.getOrDefault("folderPath", "");
        String restoredPath = generateOriginalPath(member, driveType, folderPath, fileName);

        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, trashPath, bucketName, restoredPath);
        amazonS3.copyObject(copyRequest);

        amazonS3.deleteObject(bucketName, trashPath);
        return restoredPath;
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

    private String generateOriginalPath(Member member, DriveType driveType, String folderPath, String fileName) {
        String fullFolderPath = folderPath != null && !folderPath.trim().isEmpty() ? folderPath : "";
        if (driveType == DriveType.PERSONAL) {
            return String.format("personal/%d/%s%s", member.getId(), fullFolderPath, fileName);
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s%s", academyId, fullFolderPath, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }
}
