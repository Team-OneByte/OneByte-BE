package classfit.example.classfit.category.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubClassRepository extends JpaRepository<SubClass, Long> {
    @Query("SELECT COUNT(s) > 0 FROM SubClass s " +
            "WHERE s.mainClass = :mainClass " +
            "AND s.subClassName = :subClassName " +
            "AND s.mainClass.academy = :academy " +
            "AND :member MEMBER OF s.mainClass.academy.members")
    boolean existsByMemberAndSubClassNameAndAcademyAndMainClass(
            Member member, Academy academy, String subClassName, MainClass mainClass);
}

