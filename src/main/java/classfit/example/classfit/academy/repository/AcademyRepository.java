package classfit.example.classfit.academy.repository;

import classfit.example.classfit.academy.domain.Academy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademyRepository extends JpaRepository<Academy, Long> {

    boolean existsByName(String name);

    Optional<Academy> findByCode(String code);

    boolean existsByIdAndInviteMembersEmail(Long academyId, String email);
}
