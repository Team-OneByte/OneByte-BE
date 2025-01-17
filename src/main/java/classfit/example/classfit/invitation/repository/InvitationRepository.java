package classfit.example.classfit.invitation.repository;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByAcademy(Academy academy);

    Optional<Invitation> findByAcademyIdAndEmail(Long id, String email);

    boolean existsByAcademyIdAndEmail(Long id, String email);
}
