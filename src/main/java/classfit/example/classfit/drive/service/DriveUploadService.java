package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DriveUploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadToS3(file);
    }

    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            fileUrls.add(uploadToS3(file));
        }
        return fileUrls;
    }

    private String uploadToS3(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return uploadAndGetUrl(inputStream, file);
        } catch (IOException e) {
            throw new ClassfitException("파일 업로드 오류: " + file.getOriginalFilename(), HttpStatus.BAD_REQUEST);
        }
    }

    private String uploadAndGetUrl(InputStream inputStream, MultipartFile file) {
        String objectKey = generateObjectKey(file);
        ObjectMetadata metadata = buildObjectMetadata(file);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);
        return amazonS3.getUrl(bucketName, objectKey).toString();
    }

    private ObjectMetadata buildObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }
    private String generateObjectKey(MultipartFile file) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        return "uploads/" + uniqueFileName;
    }
}