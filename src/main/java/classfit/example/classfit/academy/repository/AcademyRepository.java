package classfit.example.classfit.academy.repository;

import classfit.example.classfit.academy.domain.Academy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademyRepository extends JpaRepository<Academy, Long> {

    boolean existsByName(String name);

}
