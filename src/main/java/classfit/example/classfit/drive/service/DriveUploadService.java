package classfit.example.classfit.drive.service;

import static classfit.example.classfit.drive.domain.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.DriveType.SHARED;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DriveUploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> uploadFiles(@AuthMember Member member, DriveType driveType, List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                return uploadFileToS3(member, file, driveType);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생: " + file.getOriginalFilename(), e);
            }
        })
        .collect(Collectors.toList());
    }

    private String uploadFileToS3(Member member, MultipartFile file, DriveType driveType) throws IOException {
        String objectKey = generateObjectKey(member, file, driveType);

        try (InputStream inputStream = file.getInputStream()) {
            uploadToS3(objectKey, inputStream, file);
        }
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

    private String generateObjectKey(Member member, MultipartFile file, DriveType driveType) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
;
        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s", member.getId(), uniqueFileName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s", academyId, uniqueFileName);
        }
        throw new IllegalArgumentException("유효하지 않은 드라이브 타입");
    }
}