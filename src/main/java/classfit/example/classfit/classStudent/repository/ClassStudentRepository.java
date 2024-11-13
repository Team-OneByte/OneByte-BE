package classfit.example.classfit.classStudent.repository;

import classfit.example.classfit.domain.ClassStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    Page<ClassStudent> findBySubClass_MainClass_IdAndSubClass_Id(Long mainClassId, Long subClassId, Pageable pageable);
}
