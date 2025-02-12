package classfit.example.classfit.course.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.course.domain.MainClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainClassRepository extends JpaRepository<MainClass, Long> {
    List<MainClass> findAllByAcademyOrderByMainClassNameAsc(Academy academy);

    List<MainClass> findByAcademy(Academy academy);

    boolean existsByAcademyAndMainClassName(Academy academy, String mainClassName);
}

