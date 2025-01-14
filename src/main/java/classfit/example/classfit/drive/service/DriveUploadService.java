package classfit.example.classfit.drive.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveUploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> uploadFiles(@AuthMember Member member, DriveType driveType, List<MultipartFile> files, String folderPath) {
        return files.stream().map(file -> {
                try {
                    return uploadFileToS3(member, file, driveType, folderPath);
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            })
            .collect(Collectors.toList());
    }

    private String uploadFileToS3(Member member, MultipartFile file, DriveType driveType, String folderPath) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
        String objectKey = DriveUtil.generatedOriginPath(member, driveType, folderPath, uniqueFileName);

        try (InputStream inputStream = file.getInputStream()) {
            uploadToS3(objectKey, inputStream, file);
        }
        addTagsToS3Object(objectKey, member, folderPath, driveType, originalFileName);
        return amazonS3.getUrl(bucketName, objectKey).toString();
    }

    private void uploadToS3(String objectKey, InputStream inputStream, MultipartFile file) {
        ObjectMetadata metadata = buildObjectMetadata(file);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);
    }

    private ObjectMetadata buildObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    private void addTagsToS3Object(String objectKey, Member member, String folderPath, DriveType driveType, String originalFileName) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
        String finalFolderPath = folderPath != null && !folderPath.trim().isEmpty() ? folderPath : "";
        finalFolderPath = finalFolderPath + "/";

        List<Tag> tags = List.of(
            new Tag("folderPath", finalFolderPath),
            new Tag("driveType", driveType.toString().toLowerCase()),
            new Tag("originalFileName", originalFileName),
            new Tag("uploadedBy", member.getName()),
            new Tag("uploadedAt", formattedDate)
        );
        amazonS3.setObjectTagging(new SetObjectTaggingRequest(bucketName, objectKey, new ObjectTagging(tags)));
    }
}