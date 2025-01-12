package classfit.example.classfit.drive.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveDownloadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public InputStreamResource downloadFile(String fileName) {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));
        InputStream inputStream = s3Object.getObjectContent();
        return new InputStreamResource(inputStream);
    }
}
