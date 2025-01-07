package classfit.example.classfit.drive.service;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.DriveType.SHARED;

import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveFolderService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String createFolder(Member member, DriveType driveType, String folderName) {
        String folderKey = generateFolderKey(member, driveType, folderName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        amazonS3.putObject(new PutObjectRequest(bucketName, folderKey, emptyContent, metadata));
        return folderKey;
    }

    private String generateFolderKey(Member member, DriveType driveType, String folderName) {
        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s/", member.getId(), folderName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s/", academyId, folderName);
        }
        throw new IllegalArgumentException("유효하지 않은 드라이브 타입");
    }
}
