package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.dto.response.FileInfo;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveGetService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileInfo> getFilesFromS3() {
        List<S3ObjectSummary> objectSummaries = getS3ObjectList();
        List<FileInfo> files = new ArrayList<>();

        for (S3ObjectSummary summary : objectSummaries) {
            String fileName = summary.getKey();
            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            files.add(new FileInfo(fileName, fileUrl));
        }
        return files;
    }

    private List<S3ObjectSummary> getS3ObjectList() {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsV2Request);
        return result.getObjectSummaries();
    }
}