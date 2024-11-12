package classfit.example.classfit.category.repository;

import classfit.example.classfit.domain.SubClass;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubClassRepository extends JpaRepository<SubClass, Long> {

    Optional<SubClass> findBySubClassName(String subClassName);
}
