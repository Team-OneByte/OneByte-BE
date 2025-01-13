package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.Tag;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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

    public InputStreamResource downloadMultipleFiles(Member member, DriveType driveType, String folderPath, List<String> fileNames) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (String fileName : fileNames) {
                addFileToZip(member, driveType, folderPath, fileName, zipOutputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return new InputStreamResource(byteArrayInputStream);
    }

    private void addFileToZip(Member member, DriveType driveType, String folderPath, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        String originPath = DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName);
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, originPath));
        InputStream inputStream = s3Object.getObjectContent();

        String originalFileName = getOriginalFileName(originPath, fileName);
        ZipEntry zipEntry = new ZipEntry(originalFileName);
        zipOutputStream.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }

        zipOutputStream.closeEntry();
        inputStream.close();
    }

    private String getOriginalFileName(String objectKey, String fileName) {
        Map<String, String> tagMap = amazonS3.getObjectTagging(new GetObjectTaggingRequest(bucketName, objectKey))
            .getTagSet().stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));

        return tagMap.getOrDefault("originalFileName", fileName);
    }
}
