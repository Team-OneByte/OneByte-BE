package classfit.example.classfit.drive.service;

import static classfit.example.classfit.drive.domain.enumType.DriveType.*;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriveFolderService {

    private final AmazonS3 amazonS3;
    private final DriveRepository driveRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public String createFolder(Member member, DriveType driveType, String folderPath, String folderName) {
        if (folderName == null || folderName.trim().isEmpty()) {
            throw new ClassfitException(ErrorCode.DRIVE_FOLDER_NAME_NOT_EMPTY);
        }
        String folderKey = DriveUtil.generateFolderPath(member, driveType, folderName, folderPath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        amazonS3.putObject(new PutObjectRequest(bucketName, folderKey, emptyContent, metadata));
        driveRepository.save(createEntity(member, driveType, folderName, folderPath, folderKey));
        return folderKey;
    }

    @Transactional(readOnly = true)
    public List<String> getFolders(Member member, DriveType driveType, String folderPath) {
        List<? extends Drive> drives = (driveType == PERSONAL)
                ? driveRepository.findByMemberAndObjectPath(member, folderPath)
                : driveRepository.findByAcademyAndObjectPath(member.getAcademy(), folderPath);

        return drives.stream()
                .map(Drive::getObjectName)
                .collect(Collectors.toList());
    }

    private Drive createEntity(Member member, DriveType driveType, String folderName, String folderPath, String folderKey) {
        String originUrl = getS3FileUrl(folderKey);
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, folderKey);

        return driveType.toEntity(folderName + "/", folderPath, originUrl, metadata, member, LocalDate.now());
    }

    private String getS3FileUrl(String objectKey) {
        if (!amazonS3.doesObjectExist(bucketName, objectKey)) {
            throw new ClassfitException(ErrorCode.FILE_NOT_FOUND);
        }
        return amazonS3.getUrl(bucketName, objectKey).toString();
    }
}
