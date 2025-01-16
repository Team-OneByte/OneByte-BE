package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveRestoreService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> restoreTrash(Member member, DriveType driveType, List<String> fileNames) {
        List<String> restoredPaths = new ArrayList<>();

        for (String fileName : fileNames) {
            String prefix = DriveUtil.generatedOriginPath(member, driveType, "", fileName);
            List<ObjectVersion> deleteMarkers = listDeleteMarkers(prefix);

            for (ObjectVersion deleteMarker : deleteMarkers) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(deleteMarker.key())
                    .versionId(deleteMarker.versionId())
                    .build();

                s3Client.deleteObject(deleteRequest);
                restoredPaths.add(deleteMarker.key());
            }
        }
        return restoredPaths;
    }


    private List<ObjectVersion> listDeleteMarkers(String prefix) {
        List<ObjectVersion> deleteMarkers = new ArrayList<>();

        ListObjectVersionsRequest request = ListObjectVersionsRequest.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();

        ListObjectVersionsResponse response;
        do {
            response = s3Client.listObjectVersions(request);

            for (DeleteMarkerEntry deleteMarkerEntry : response.deleteMarkers()) {
                deleteMarkers.add(
                    ObjectVersion.builder()
                        .key(deleteMarkerEntry.key())
                        .versionId(deleteMarkerEntry.versionId())
                        .build()
                );
            }

            request = request.toBuilder()
                .keyMarker(response.nextKeyMarker())
                .versionIdMarker(response.nextVersionIdMarker())
                .build();
        } while (response.isTruncated());
        return deleteMarkers;
    }
}
