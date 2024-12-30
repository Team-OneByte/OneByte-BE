package classfit.example.classfit.invitation.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;

    public String findAcademyCode(Member member) {
        Academy academy = member.getAcademy();

        if (academy == null) {
            throw new ClassfitException("해당 사용자는 학원에 등록되지 않았습니다.", HttpStatus.NOT_FOUND);
        }

        return academy.getCode();
    }

}
