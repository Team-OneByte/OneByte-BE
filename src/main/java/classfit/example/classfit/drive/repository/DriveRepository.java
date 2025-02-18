package classfit.example.classfit.drive.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.PersonalDrive;
import classfit.example.classfit.drive.domain.SharedDrive;
import classfit.example.classfit.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DriveRepository extends JpaRepository<Drive, Long> {

    List<PersonalDrive> findByMemberAndIsDeletedFalse(Member member);

    List<SharedDrive> findByAcademyAndIsDeletedFalse(Academy academy);

    List<PersonalDrive> findByMemberAndObjectTypeAndIsDeletedFalse(Member member,
            String objectType);

    List<SharedDrive> findByAcademyAndObjectTypeAndIsDeletedFalse(Academy academy,
            String objectType);

    @Query("SELECT d FROM PersonalDrive d WHERE d.member = :member AND d.objectName LIKE %:objectName% AND d.isDeleted = false")
    List<PersonalDrive> findPersonalFilesByMember(
            @Param("member") Member member,
            @Param("objectName") String objectName
    );

    @Query("SELECT d FROM SharedDrive d WHERE d.academy = :academy AND d.objectName LIKE %:objectName% AND d.isDeleted = false")
    List<SharedDrive> findSharedFilesByAcademy(
            @Param("academy") Academy academy,
            @Param("objectName") String objectName
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM PersonalDrive d WHERE d.member = :member AND d.objectName IN :objectNames")
    int deletePersonalFilesFromTrash(
            @Param("member") Member member,
            @Param("objectNames") List<String> objectNames
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM SharedDrive d WHERE d.academy = :academy AND d.objectName IN :objectNames")
    int deleteSharedFilesFromTrash(
            @Param("academy") Academy academy,
            @Param("objectNames") List<String> objectNames
    );

    @Transactional
    @Modifying
    @Query("UPDATE PersonalDrive d SET d.isDeleted = true " +
            "WHERE d.member = :member " +
            "AND d.objectName IN :objectNames ")
    int storePersonalFiles(
            @Param("member") Member member,
            @Param("objectNames") List<String> objectNames
    );

    @Transactional
    @Modifying
    @Query("UPDATE SharedDrive d SET d.isDeleted = true " +
            "WHERE d.academy = :academy " +
            "AND d.objectName IN :objectNames ")
    int storeSharedFiles(
            @Param("academy") Academy academy,
            @Param("objectNames") List<String> objectNames
    );

    @Query("SELECT d FROM PersonalDrive d WHERE d.member = :member AND d.isDeleted = true")
    List<PersonalDrive> findDeletedPersonalFiles(@Param("member") Member member);

    @Query("SELECT d FROM SharedDrive d WHERE d.academy = :academy AND d.isDeleted = true")
    List<SharedDrive> findDeletedSharedFiles(@Param("academy") Academy academy);

    @Transactional
    @Modifying
    @Query("UPDATE Drive d SET d.isDeleted = false " +
            "WHERE d.member = :member " +
            "AND d.objectName IN :objectNames ")
    int restorePersonalFiles(
            @Param("member") Member member,
            @Param("objectNames") List<String> objectNames
    );

    @Transactional
    @Modifying
    @Query("UPDATE Drive d SET d.isDeleted = false " +
            "WHERE d.academy = :academy " +
            "AND d.objectName IN :objectNames ")
    int restoreSharedFiles(
            @Param("academy") Academy academy,
            @Param("objectNames") List<String> objectNames
    );
}
