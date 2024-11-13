package classfit.example.classfit.student.repository;

import classfit.example.classfit.domain.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByName(String studentName);
}
