package classfit.example.classfit.classStudent.repository;

import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.scoreReport.dto.response.FindClassStudent;
import classfit.example.classfit.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    @Query("SELECT cs FROM ClassStudent cs " +
        "JOIN FETCH cs.student s " +
        "JOIN cs.subClass sc " +
        "JOIN sc.mainClass mc " +
        "WHERE mc.id = :mainClassId AND sc.id = :subClassId AND mc.academy.id = :academyId")
    Page<ClassStudent> findAllByMainClassAndSubClass(
        @Param("mainClassId") Long mainClassId,
        @Param("subClassId") Long subClassId,
        @Param("academyId") Long academyId,
        Pageable pageable);

    @Query("SELECT cs FROM ClassStudent cs " +
        "WHERE cs.subClass.mainClass.academy.id = :academyId")
    List<ClassStudent> findByAcademyId(Long academyId);

    @Modifying
    @Query("DELETE FROM ClassStudent cs WHERE cs.student.id = :studentId")
    void deleteAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM ClassStudent s WHERE s.subClass.id = :subClassId")
    List<ClassStudent> findAllBySubClassId(@Param("subClassId") Long subClassId);

    List<ClassStudent> findByStudent(Student student);

    List<ClassStudent> findBySubClass(SubClass subClass);

    @Query("SELECT new classfit.example.classfit.scoreReport.dto.response.FindClassStudent(cs.student.id, cs.student.name) " +
        "FROM ClassStudent cs " +
        "WHERE cs.subClass.id = :subClassId " +
        "AND cs.subClass.mainClass.id = :mainClassId")
    List<FindClassStudent> findStudentIdsByMainClassIdAndSubClassId(
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);
}
