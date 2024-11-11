package classfit.example.classfit.student.repository;

import classfit.example.classfit.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}