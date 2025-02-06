package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final AmazonS3 amazonS3;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<FileResponse> getFilesFromTrash(Member member, DriveType driveType) {
        Set<String> excludedPaths = new HashSet<>();
        List<FileResponse> deletedFiles = new ArrayList<>();
        String prefix = DriveUtil.buildPrefix(driveType, member, "");

        ListObjectVersionsRequest request = ListObjectVersionsRequest.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();

        ListObjectVersionsResponse response;
        do {
            response = s3Client.listObjectVersions(request);

            ListObjectVersionsResponse finalResponse = response;
            response.deleteMarkers().forEach(deleteMarker -> {
                String key = deleteMarker.key();

                for (String excludedPath : excludedPaths) {
                    if (key.startsWith(excludedPath)) {
                        return;
                    }
                }

                if (key.endsWith("/")) {
                    excludedPaths.add(key);
                }

                String deleteMarkerVersionId = deleteMarker.versionId();
                Optional<ObjectVersion> previousVersionOpt = finalResponse.versions().stream()
                    .filter(version -> version.key().equals(key))
                    .filter(version -> !version.versionId().equals(deleteMarkerVersionId))
                    .findFirst();

                Map<String, String> tagMap = new HashMap<>();
                long fileSize = 0;
                if (previousVersionOpt.isPresent()) {
                    ObjectVersion previousVersion = previousVersionOpt.get();
                    fileSize = previousVersion.size();

                    GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .versionId(previousVersion.versionId())
                        .build();

                    GetObjectTaggingResponse taggingResponse = s3Client.getObjectTagging(taggingRequest);
                    tagMap = taggingResponse.tagSet().stream()
                        .collect(Collectors.toMap(Tag::key, Tag::value));
                }

                S3ObjectSummary summary = new S3ObjectSummary();
                summary.setKey(key);
                summary.setSize(fileSize);

                String fileUrl = s3Client.utilities().getUrl(builder ->
                    builder.bucket(bucketName).key(key)).toExternalForm();

                FileResponse fileResponse = DriveUtil.getFileResponse(summary, key, fileUrl, tagMap);
                deletedFiles.add(fileResponse);
            });

            request = request.toBuilder()
                .keyMarker(response.nextKeyMarker())
                .versionIdMarker(response.nextVersionIdMarker())
                .build();

        } while (response.isTruncated());

        return deletedFiles;
    }


    public List<String> storeTrash(Member member, DriveType driveType, String folderPath, List<String> fileNames) {
        List<String> trashPaths = new ArrayList<>();

        for (String fileName : fileNames) {
            if (fileName.endsWith("/")) {
                List<String> objectsToDelete = fileInFolder(member, driveType, fileName);
                deleteObjects(objectsToDelete);
                trashPaths.addAll(objectsToDelete);
            } else {
                String originPath = cleanPath(DriveUtil.generatedOriginPath(member, driveType, folderPath, fileName));
                amazonS3.deleteObject(bucketName, originPath);
                trashPaths.add(originPath);
            }
        }

        return trashPaths;
    }

    private List<String> fileInFolder(Member member, DriveType driveType, String folderPath) {
        List<String> files = new ArrayList<>();

        String prefix = cleanPath(DriveUtil.generatedOriginPath(member, driveType, folderPath, ""));
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(prefix);

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(request);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String key = objectSummary.getKey();
                files.add(key);
            }
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return files;
    }

    private void deleteObjects(List<String> objectKeys) {
        if (objectKeys.isEmpty()) {
            return;
        }

        List<DeleteObjectsRequest.KeyVersion> keysToDelete = objectKeys.stream()
            .map(DeleteObjectsRequest.KeyVersion::new)
            .collect(Collectors.toList());

        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
            .withKeys(keysToDelete);

        amazonS3.deleteObjects(deleteRequest);
    }

    private String cleanPath(String path) {
        if (path == null) {
            return "";
        }
        return path.replaceAll("/+", "/");
    }
}
