package classfit.example.classfit.category.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.category.domain.MainClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainClassRepository extends JpaRepository<MainClass, Long> {
    @Query("SELECT mc FROM MainClass mc WHERE mc.member.academy = :academy ORDER BY mc.mainClassName ASC")
    List<MainClass> findAllByMemberAcademy(@Param("academy") Academy academy);

    List<MainClass> findByMemberAcademy(Academy academy);

    boolean existsByMember_AcademyAndMainClassName(Academy academy, String mainClassName);
}
