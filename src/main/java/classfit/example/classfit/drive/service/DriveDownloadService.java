package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveDownloadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public InputStreamResource downloadFile(Member member, DriveType driveType, String folderPath, String fileName) {
        String originPath = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, originPath));
        InputStream inputStream = s3Object.getObjectContent();
        return new InputStreamResource(inputStream);
    }
}
