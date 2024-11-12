package classfit.example.classfit.category.repository;

import classfit.example.classfit.domain.MainClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainClassRespository extends JpaRepository<MainClass,Long> {
    List<MainClass> findAllByOrderByMainClassNameAsc();
}
