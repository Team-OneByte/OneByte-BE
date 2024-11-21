package classfit.example.classfit.category.repository;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubClassRepository extends JpaRepository<SubClass, Long> {
    boolean existsByMemberAndSubClassNameAndMainClass(Member member, String subClassName,
                                                      MainClass mainClass);

}
