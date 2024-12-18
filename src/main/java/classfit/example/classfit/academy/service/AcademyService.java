package classfit.example.classfit.academy.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.EmailUtil;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        if (academyRepository.existsByName(request.name())) {
            throw new ClassfitException("이미 등록된 학원명이 존재합니다. 다시 시도해 주세요.", HttpStatus.NOT_IMPLEMENTED);
        }

        Member member = memberRepository.findByEmail(request.email()).orElseThrow(
            () -> new ClassfitException("등록된 회원 정보가 없습니다. 회원 가입을 완료해 주세요.", HttpStatus.NOT_FOUND));

        member.updateRole("ADMIN");

        Academy academy = request.toEntity();
        academy.addMember(member);
        academyRepository.save(academy);

        return AcademyResponse.from(academy);
    }

    @Transactional
    public void joinAcademy(Member member, AcademyJoinRequest request) {

        Academy academy = academyRepository.findByCode(request.code())
            .orElseThrow(() -> new ClassfitException("유효하지 않는 코드입니다", HttpStatus.NOT_FOUND));

        if (!invitationRepository.existsByAcademyIdAndEmail(academy.getId(), request.email())) {
            throw new ClassfitException("학원으로부터 초대되어지지 않은 계정입니다.", HttpStatus.NOT_FOUND);
        }

        member.updateRole("MEMBER");
        academy.addMember(member);
    }

    public String createCode() {
        return EmailUtil.createdCode();
    }
}

