package classfit.example.classfit.member.repository;

import classfit.example.classfit.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<List<Member>> findByAcademyId(Long academyId);

    @Query("SELECT m.academy.id FROM Member m WHERE m.name = :memberName")
    Long findAcademyIdByMemberName(@Param("memberName") String memberName);
}
