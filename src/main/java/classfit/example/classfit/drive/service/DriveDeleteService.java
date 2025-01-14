package classfit.example.classfit.drive.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveDeleteService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void deleteFromTrash(Member member, DriveType driveType, List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                String trashPath = generateTrashPath(member, driveType, fileName);
                amazonS3.deleteObject(bucketName, trashPath);
            } catch (Exception e) {
                System.err.println("파일 삭제 중 오류 발생: " + fileName);
            }
        }
    }

    private String generateTrashPath(Member member, DriveType driveType, String fileName) {
        if (driveType == DriveType.PERSONAL) {
            return String.format("trash/personal/%d/%s", member.getId(), fileName);
        } else if (driveType == DriveType.SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("trash/shared/%d/%s", academyId, fileName);
        }
        throw new ClassfitException("지원하지 않는 드라이브 타입입니다.", HttpStatus.NO_CONTENT);
    }
}

