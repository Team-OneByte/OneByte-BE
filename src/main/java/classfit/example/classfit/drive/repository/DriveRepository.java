package classfit.example.classfit.drive.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.PersonalDrive;
import classfit.example.classfit.drive.domain.SharedDrive;
import classfit.example.classfit.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriveRepository extends JpaRepository<Drive, Long> {
    List<PersonalDrive> findByMemberAndObjectPath(Member member, String folderPath);

    List<SharedDrive> findByAcademyAndObjectPath(Academy academy, String folderPath);

    List<Drive> findByMemberAndObjectPathAndObjectType(Member member, String folderPath, String objectType);

    List<Drive> findByAcademyAndObjectPathAndObjectType(Academy academy, String folderPath, String objectType);

    @Query("SELECT d FROM PersonalDrive d WHERE d.member = :member AND d.objectPath = :folderPath AND d.objectName LIKE %:fileName%")
    List<PersonalDrive> findPersonalFilesByMember(
            @Param("member") Member member,
            @Param("folderPath") String folderPath,
            @Param("fileName") String fileName
    );

    @Query("SELECT d FROM SharedDrive d WHERE d.academy = :academy AND d.objectPath = :folderPath AND d.objectName LIKE %:fileName%")
    List<SharedDrive> findSharedFilesByAcademy(
            @Param("academy") Academy academy,
            @Param("folderPath") String folderPath,
            @Param("fileName") String fileName
    );
}
