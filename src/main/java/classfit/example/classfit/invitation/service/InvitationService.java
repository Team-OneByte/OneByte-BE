package classfit.example.classfit.invitation.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.dto.request.InvitationRequest;
import classfit.example.classfit.invitation.dto.response.InvitationResponse;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.mail.service.EmailService;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;

    @Transactional
    public String findAcademyCode(Member member) {
        Academy academy = member.getAcademy();

        if (academy == null) {
            throw new ClassfitException(ErrorCode.INVALID_INVITATION);
        }

        return academy.getCode();
    }

    @Transactional
    public void inviteStaffByEmail(Member member, InvitationRequest request) {

        boolean exists = invitationRepository.existsByAcademyIdAndEmail(
            member.getAcademy().getId(),
            request.email()
        );
        System.out.println("Academy ID: " + member.getAcademy().getId());
        System.out.println("Request Email: " + request.email());
        System.out.println(exists + " exists");

        if (!exists) {
            Invitation invitation = request.toEntity(member.getAcademy());
            invitationRepository.save(invitation);
        }

        emailService.sendEmail(request.email(), EmailPurpose.INVITATION);
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> staffInfoAll(Member member) {
        List<Invitation> invitations = invitationRepository.findByAcademy(member.getAcademy());
        return invitations.stream()
            .map(InvitationResponse::from)
            .collect(Collectors.toList());
    }
}
