package classfit.example.classfit.student.repository;

import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.scoreReport.dto.response.FindClassStudent;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT cs FROM Enrollment cs " +
            "JOIN FETCH cs.student s " +
            "JOIN cs.subClass sc " +
            "JOIN sc.mainClass mc " +
            "WHERE mc.id = :mainClassId AND sc.id = :subClassId AND mc.academy.id = :academyId")
    Page<Enrollment> findAllByMainClassAndSubClass(
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId,
            @Param("academyId") Long academyId,
            Pageable pageable);

    @Query("SELECT cs FROM Enrollment cs " +
            "WHERE cs.subClass.mainClass.academy.id = :academyId")
    List<Enrollment> findByAcademyId(Long academyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Enrollment cs WHERE cs.student.id = :studentId")
    void deleteAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Enrollment s WHERE s.subClass.id = :subClassId")
    List<Enrollment> findAllBySubClassId(@Param("subClassId") Long subClassId);

    List<Enrollment> findByStudent(Student student);

    @Query("SELECT cs FROM Enrollment cs " +
            "JOIN cs.subClass sc " +
            "JOIN sc.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND sc = :subClass")
    List<Enrollment> findByAcademyIdAndSubClass(@Param("academyId") Long academyId,
            @Param("subClass") SubClass subClass);


    @Query("SELECT new classfit.example.classfit.scoreReport.dto.response.FindClassStudent(cs.student.id, cs.student.name) "
            +
            "FROM Enrollment cs " +
            "WHERE cs.subClass.id = :subClassId " +
            "AND cs.subClass.mainClass.id = :mainClassId")
    List<FindClassStudent> findStudentIdsByMainClassIdAndSubClassId(
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);

    @Query("SELECT DISTINCT e.student FROM Enrollment e " +
            "JOIN e.subClass sc " +
            "JOIN sc.mainClass mc " +
            "JOIN mc.academy a " +
            "WHERE a.id = :academyId " +
            "AND sc.id = :subClassId")
    List<Student> findStudentsByAcademyIdAndSubClass(@Param("academyId") Long academyId,
            @Param("subClassId") Long subClassId);

}
