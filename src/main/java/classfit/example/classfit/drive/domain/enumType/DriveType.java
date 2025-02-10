package classfit.example.classfit.drive.domain.enumType;

import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.PersonalDrive;
import classfit.example.classfit.drive.domain.SharedDrive;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.time.LocalDate;


public enum DriveType {
    PERSONAL {
        @Override
        public Drive toEntity(String fileName, String folderPath, String originUrl, ObjectMetadata metadata, Member member, LocalDate dateTime) {
            return PersonalDrive.toEntity(fileName, folderPath, originUrl, metadata, member, dateTime);
        }
    },
    SHARED {
        @Override
        public Drive toEntity(String fileName, String folderPath, String originUrl, ObjectMetadata metadata, Member member, LocalDate dateTime) {
            return SharedDrive.toEntity(fileName, folderPath, originUrl, metadata, member, dateTime);
        }
    };

    public abstract Drive toEntity(String fileName, String folderPath, String originUrl, ObjectMetadata metadata, Member member, LocalDate dateTime);
}