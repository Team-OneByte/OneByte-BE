package classfit.example.classfit.inviteMember.repository;

import classfit.example.classfit.inviteMember.domain.InviteMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteMemberRepository extends JpaRepository<InviteMember, Long> {

}
