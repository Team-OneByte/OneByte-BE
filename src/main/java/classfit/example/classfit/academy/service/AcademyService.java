package classfit.example.classfit.academy.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.enumType.InvitationType;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademyService {

    private final AcademyRepository academyRepository;
    private final MemberRepository memberRepository;
    private final InvitationRepository invitationRepository;

    @Transactional
    public AcademyResponse createAcademy(AcademyCreateRequest request) {

        if (academyRepository.existsByCode(request.code())) {
            throw new ClassfitException(ErrorCode.ACADEMY_CODE_ALREADY_EXISTS);
        }

        if (academyRepository.existsByName(request.name())) {
            throw new ClassfitException(ErrorCode.ACADEMY_ALREADY_EXISTS);
        }

        Member member = getMember(request.email());
        member.updateRole("ADMIN");

        Academy academy = request.toEntity();
        academy.addMember(member);
        academyRepository.save(academy);

        return AcademyResponse.from(academy);
    }

    @Transactional
    public void joinAcademy(AcademyJoinRequest request) {

        Academy academy = academyRepository.findByCode(request.code())
                .orElseThrow(() -> new ClassfitException(ErrorCode.EMAIL_AUTH_CODE_INVALID));

        Invitation invitation = invitationRepository.findByAcademyIdAndEmail(academy.getId(), request.email())
                .orElseThrow(() -> new ClassfitException(ErrorCode.ACADEMY_INVITATION_INVALID));

        Member member = getMember(request.email());

        invitation.updateStatus(InvitationType.COMPLETED);
        member.updateRole("MEMBER");

        academy.addMember(member);
    }

    private Member getMember(String request) {
        return memberRepository.findByEmail(request).orElseThrow(
                () -> new ClassfitException(ErrorCode.EMAIL_NOT_FOUND));
    }
}

