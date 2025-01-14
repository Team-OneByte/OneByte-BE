package classfit.example.classfit.member.repository;

import classfit.example.classfit.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<List<Member>> findByAcademyId(Long academyId);
}
