package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveDeleteService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void deleteFromTrash(Member member, DriveType driveType, String folderPath, List<String> fileNames) {

        fileNames.forEach(fileName -> {
            String key = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);

            try {
                ListObjectVersionsRequest listVersionsRequest = ListObjectVersionsRequest.builder()
                    .bucket(bucketName)
                    .prefix(key)
                    .build();

                ListObjectVersionsResponse response = s3Client.listObjectVersions(listVersionsRequest);

                response.versions().forEach(version -> {
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(version.key())
                        .versionId(version.versionId())
                        .build();

                    s3Client.deleteObject(deleteRequest);
                });

                response.deleteMarkers().forEach(deleteMarker -> {
                    DeleteObjectRequest deleteMarkerRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(deleteMarker.key())
                        .versionId(deleteMarker.versionId())
                        .build();

                    s3Client.deleteObject(deleteMarkerRequest);
                });

            } catch (S3Exception e) {
                System.err.println("Error deleting file: " + key + ", Error: " + e.awsErrorDetails().errorMessage());
            }
        });
    }


}

