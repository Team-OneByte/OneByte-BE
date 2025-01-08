package classfit.example.classfit.drive.service;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.DriveType.SHARED;

import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
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

    public String createFolder(Member member, DriveType driveType, String folderName, String folderPath) {
        String uniqueFolderName = generateUniqueFolderName(member, driveType, folderName, folderPath);
        String fullFolderPath = generateFolderKey(member, driveType, uniqueFolderName, folderPath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        amazonS3.putObject(new PutObjectRequest(bucketName, fullFolderPath, emptyContent, metadata));
        return fullFolderPath;
    }

    private String generateUniqueFolderName(Member member, DriveType driveType, String folderName, String folderPath) {
        String baseKey = generateFolderKey(member, driveType, folderName, folderPath);
        int counter = 1;

        while (doesFolderExist(baseKey)) {
            folderName = String.format("%s(%d)", folderName, counter);
            baseKey = generateFolderKey(member, driveType, folderName, folderPath);
            counter++;
        }
        return folderName;
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
}
