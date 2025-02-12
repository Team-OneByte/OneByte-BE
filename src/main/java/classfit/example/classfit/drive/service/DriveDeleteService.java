package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveDeleteService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final DriveRepository driveRepository;

    public void deleteFromTrash(Member member, DriveType driveType, List<String> objectNames) {
        int deletedRows = (driveType == DriveType.PERSONAL)
                ? driveRepository.deletePersonalFilesFromTrash(member, objectNames)
                : driveRepository.deleteSharedFilesFromTrash(member.getAcademy(), objectNames);

        if (deletedRows > 0) {
            deleteFilesFromS3(member, driveType, objectNames);
        }
    }

    private void deleteFilesFromS3(Member member, DriveType driveType, List<String> fileNames) {
        List<ObjectIdentifier> objectsToDelete = fileNames.stream()
                .map(fileName -> ObjectIdentifier.builder()
                        .key(DriveUtil.generatedOriginPath(member, driveType, fileName))
                        .build())
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objectsToDelete).build())
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);
    }
}

