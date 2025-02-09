package classfit.example.classfit.drive.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.PersonalDrive;
import classfit.example.classfit.drive.domain.SharedDrive;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriveRepository extends JpaRepository<Drive, Long> {
    List<PersonalDrive> findByMemberAndObjectPath(Member member, String folderPath);
    List<SharedDrive> findByAcademyAndObjectPath(Academy academy, String folderPath);
}
