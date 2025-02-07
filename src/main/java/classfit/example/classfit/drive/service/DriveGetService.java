package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.domain.enumType.FileType;
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

        String folderPathWithSlash = folderPath.isEmpty() ? folderPath : folderPath + "/";
        for (S3ObjectSummary summary : objectSummaries) {
            FileResponse fileInfo = buildFileInfo(summary);

            boolean isNotFolderItself = !fileInfo.fileName().equals(folderPathWithSlash);
            boolean isInTargetFolder = fileInfo.folderPath().equals(folderPathWithSlash);
            if (isNotFolderItself && isInTargetFolder) {
                files.add(fileInfo);
            }
        }
        return files;
    }

    public List<FileResponse> searchFilesByName(Member member, DriveType driveType, String fileName, String folderPath) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, folderPath);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        String folderPathWithSlash = folderPath.isEmpty() ? folderPath : folderPath + "/";
        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> {
                boolean isNotFolderItself = !fileInfo.fileName().equals(folderPathWithSlash);
                boolean matchesFileName = fileName.isEmpty() || normalize(fileInfo.fileName()).contains(normalize(fileName));
                boolean isInTargetFolder = fileInfo.folderPath().equals(folderPathWithSlash);
                return isNotFolderItself && matchesFileName && isInTargetFolder;
            })
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
            .withBucketName(bucketName);

        String prefix = DriveUtil.buildPrefix(driveType, member, folderPath);
        request.setPrefix(prefix);
        return request;
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFC).trim().toLowerCase();
    }

    public List<FileResponse> classifyFilesByType(Member member, DriveType driveType, FileType filterFileType, String folderPath) {
        ListObjectsV2Request request = createListObjectsRequest(driveType, member, folderPath);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);

        String folderPathWithSlash = folderPath.isEmpty() ? folderPath : folderPath + "/";
        return result.getObjectSummaries().stream()
            .map(this::buildFileInfo)
            .filter(fileInfo -> {
                boolean isNotFolderItself = !fileInfo.fileName().equals(folderPathWithSlash);
                boolean matchesFileType = DriveUtil.getFileType(fileInfo.fileName()).equals(filterFileType);
                boolean isInTargetFolder = fileInfo.folderPath().equals(folderPathWithSlash);
                return isNotFolderItself && matchesFileType && isInTargetFolder;
            })
            .collect(Collectors.toList());
    }
}