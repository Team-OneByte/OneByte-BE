package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.domain.enumType.ObjectType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static classfit.example.classfit.drive.domain.enumType.DriveType.PERSONAL;
import static classfit.example.classfit.drive.domain.enumType.DriveType.SHARED;

public class DriveUtil {

    public static String generatedOriginPath(Member member, DriveType driveType, String fileName) {

        if (driveType == PERSONAL) {
            return String.format("personal/%d/%s", member.getId(), fileName);
        } else if (driveType == SHARED) {
            Long academyId = member.getAcademy().getId();
            return String.format("shared/%d/%s", academyId, fileName);
        }
        throw new ClassfitException(ErrorCode.DRIVE_TYPE_INVALID);
    }

    public static String generateFolderPath(Member member, DriveType driveType, String folderName, String folderPath) {
        String basePath = driveType == PERSONAL
                ? String.format("personal/%d/", member.getId())
                : String.format("shared/%d/", member.getAcademy().getId());

        return basePath + folderPath + folderName + "/";
    }

    public static String formatFileSize(long sizeInBytes) {
        double sizeInKB = sizeInBytes / 1024.0;
        if (sizeInKB >= 1024) {
            double sizeInMB = sizeInKB / 1024.0;
            return String.format("%.1f MB", sizeInMB);
        }
        return String.format("%.1f KB", sizeInKB);
    }

    public static String formatObjectName(String objectName) {
        if (objectName == null || !objectName.contains("_")) {
            return objectName; // _가 없으면 원본 문자열 그대로 반환
        }
        return objectName.substring(objectName.indexOf("_") + 1);
    }
}
