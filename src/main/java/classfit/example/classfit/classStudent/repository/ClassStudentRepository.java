package classfit.example.classfit.classStudent.repository;

import classfit.example.classfit.domain.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    List<ClassStudent> findBySubClass_MainClass_IdAndSubClass_Id(Long mainClassId, Long subClassId);
}
