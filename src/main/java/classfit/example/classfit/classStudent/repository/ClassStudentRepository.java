package classfit.example.classfit.classStudent.repository;

import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.scoreReport.dto.response.FindClassStudent;
import classfit.example.classfit.student.domain.Student;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    Page<ClassStudent> findBySubClass_MainClass_IdAndSubClass_Id(Long mainClassId, Long subClassId,
            Pageable pageable);

    @Modifying
    @Query("DELETE FROM ClassStudent cs WHERE cs.student.id = :studentId")
    void deleteAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM ClassStudent s WHERE s.subClass.id = :subClassId")
    List<ClassStudent> findAllBySubClassId(@Param("subClassId") Long subClassId);

    ClassStudent findByStudent(Student student);

    List<ClassStudent> findBySubClass(SubClass subClass);

    @Query("SELECT cs.student.id " +
            "FROM ClassStudent cs " +
            "WHERE cs.subClass.id = :subClassId " +
            "AND cs.subClass.mainClass.id = :mainClassId")
    List<FindClassStudent> findStudentIdsByMainClassIdAndSubClassId(
            @Param("mainClassId") Long mainClassId,
            @Param("subClassId") Long subClassId);
}
