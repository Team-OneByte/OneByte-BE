package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DrivePreSignedResponse;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import jakarta.transaction.Transactional;
import java.net.URL;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriveUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    private final DriveRepository driveRepository;

    public List<DrivePreSignedResponse> getPreSignedUrl(Member member, DriveType driveType,
            String folderPath, List<String> fileName) {
        return fileName.stream()
                .map(file -> {
                    String uniqueFileName = UUID.randomUUID() + "_" + file;
                    String objectKey = DriveUtil.generatedOriginPath(member, driveType, folderPath,
                            uniqueFileName);

                    GeneratePresignedUrlRequest preSignedUrl = generatePreSignedUrl(bucketName,
                            objectKey);
                    URL url = amazonS3.generatePresignedUrl(preSignedUrl);

                    return DrivePreSignedResponse.of(uniqueFileName, url.toString());
                })
                .toList();
    }

    @Transactional
    public void uploadConfirm(Member member, DriveType driveType, String folderPath, List<String> fileNames) {
        List<Drive> files = fileNames.stream()
                .map(fileName -> createDriveEntity(member, driveType, folderPath, fileName))
                .toList();

        driveRepository.saveAll(files);
    }

    private GeneratePresignedUrlRequest generatePreSignedUrl(String bucket, String fileName) {
        GeneratePresignedUrlRequest preSignedURL = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(preSignedUrlExpiration());

        preSignedURL.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return preSignedURL;
    }

    private Date preSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private Drive createDriveEntity(Member member, DriveType driveType, String folderPath, String fileName) {
        String objectKey = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);
        String originUrl = getS3FileUrl(objectKey);
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, objectKey);

        return driveType.toEntity(fileName, folderPath, originUrl, metadata, member);
    }

    private String getS3FileUrl(String objectKey) {
        if (!amazonS3.doesObjectExist(bucketName, objectKey)) {
            throw new ClassfitException(ErrorCode.FILE_NOT_FOUND);
        }
        return amazonS3.getUrl(bucketName, objectKey).toString();
    }
}