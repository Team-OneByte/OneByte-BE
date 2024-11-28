package classfit.example.classfit.academy.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.dto.request.AcademyRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademyService {

    private final AcademyRepository academyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public AcademyResponse register(AcademyRequest request) {

        if (academyRepository.existsByName(request.name())) {
            throw new ClassfitException("이미 등록된 학원명이 존재합니다. 다시 시도해 주세요.", HttpStatus.NOT_IMPLEMENTED);
        }

        Member member = memberRepository.findByEmail(request.email()).orElseThrow(
            () -> new ClassfitException("등록된 회원 정보가 없습니다. 처음부터 다시 시도해 주세요", HttpStatus.NOT_FOUND));

        Academy academy = request.toEntity();
        academy.addAcademy(member);
        academyRepository.save(academy);

        return AcademyResponse.from(academy);
    }

    public String createCode() {
        return CodeUtil.createdCode();
    }
}

