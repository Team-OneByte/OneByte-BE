package classfit.example.classfit.classStudent.repository;

import classfit.example.classfit.domain.ClassStudent;
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
}
