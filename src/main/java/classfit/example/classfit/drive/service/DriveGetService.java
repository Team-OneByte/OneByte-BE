package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveGetService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileResponse> getFilesFromS3(Member member, DriveType driveType, String folderPath) {
        List<FileResponse> files = new ArrayList<>();
        String prefix = DriveUtil.buildPrefix(driveType, member, folderPath);
        List<S3ObjectSummary> objectSummaries = getS3ObjectList(prefix);

        for (S3ObjectSummary summary : objectSummaries) {
            FileResponse fileInfo = buildFileInfo(summary);
            files.add(fileInfo);
        }
        return files;
    }

    public List<FileResponse> searchFilesByName(Member member, DriveType driveType, String fileName) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, fileName);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> normalize(fileInfo.fileName()).contains(normalize(fileName)))  // 정규화하여 필터링
            .collect(Collectors.toList());
    }

    private List<S3ObjectSummary> getS3ObjectList(String prefix) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsV2Request);
        return result.getObjectSummaries();
    }

    private FileResponse buildFileInfo(S3ObjectSummary summary) {
        String fileName = summary.getKey();
        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();

        Map<String, String> tagMap = getTagsForS3Object(fileName);

        return DriveUtil.getFileResponse(summary, fileName, fileUrl, tagMap);
    }

    private Map<String, String> getTagsForS3Object(String objectKey) {
        List<Tag> tags = amazonS3.getObjectTagging(new GetObjectTaggingRequest(bucketName, objectKey)).getTagSet();
        return tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
    }

    private ListObjectsV2Request createListObjectsRequest(DriveType driveType, Member member, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withDelimiter("/");

        String prefix = DriveUtil.buildPrefix(driveType, member, folderPath);
        request.setPrefix(prefix);
        return request;
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFC).trim().toLowerCase();
    }

    public List<FileResponse> classifyFilesByType(Member member, DriveType driveType, FileType filterFileType) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, "");
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> !isFolder(fileInfo))     // 폴더는 제외
            .filter(fileInfo -> {
                FileType fileType = DriveUtil.getFileType(fileInfo.fileName());
                return fileType.equals(filterFileType);  // 필터링 조건 추가
            })
            .collect(Collectors.toList());
    }

    private boolean isFolder(FileResponse fileInfo) {
        return fileInfo.fileName().endsWith("/");
    }
}