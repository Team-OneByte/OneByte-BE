package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.DriveType.SHARED;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> moveToTrash(Member member, DriveType driveType, String folderPath, List<String> fileNames) {
        List<String> trashPaths = new ArrayList<>();

        for (String fileName : fileNames) {
            String sourcePath = generateSourcePath(member, driveType, folderPath, fileName);
            String trashPath = generateTrashPath(member, driveType, fileName);

            CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, sourcePath, bucketName, trashPath);
            amazonS3.copyObject(copyRequest);

            addDeleteTagsToS3Object(trashPath, folderPath, member);
            amazonS3.deleteObject(bucketName, sourcePath);

            trashPaths.add(trashPath);
        }
        return trashPaths;
    }

    private String generateSourcePath(Member member, DriveType driveType, String folderPath, String fileName) {
        String fullFolderPath = generateFolderPath(folderPath);

        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s%s", member.getId(), fullFolderPath, fileName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s%s", academyId, fullFolderPath, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
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

    private String generateFolderPath(String folderPath) {
        return folderPath != null && !folderPath.trim().isEmpty() ? folderPath + "/" : "";
    }

    private void addDeleteTagsToS3Object(String objectKey, String folderPath, Member member) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);

        List<Tag> tags = List.of(
            new Tag("folderPath", generateFolderPath(folderPath)),
            new Tag("deletedBy", member.getName()),
            new Tag("deleteAt", formattedDate)
        );
        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(tags)));
    }
}
