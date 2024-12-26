package classfit.example.classfit.category.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubClassService {

    private final SubClassRepository subClassRepository;
    private final MainClassRepository mainClassRepository;
    private final MemberRepository memberRepository;

    private static void checkMemberRelationMainClass(Member findMember, MainClass findMainClass) {
        if (!Objects.equals(findMember.getId(), findMainClass.getMember().getId())) {
            throw new ClassfitException("사용자와 클래스가 일치하지 않습니다.", HttpStatus.FORBIDDEN);
        }
    }

    private static void checkMemberRelationSubClass(Member findMember, SubClass findSubClass) {
        if (!Objects.equals(findMember.getId(), findSubClass.getMember().getId())) {
            throw new ClassfitException("사용자와 클래스가 일치하지 않습니다.", HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
// 서브클래스 추가
    public SubClassResponse addSubClass(@AuthMember Member findMember, SubClassRequest req) {

        MainClass findMainClass = mainClassRepository.findById(req.mainClassId())
            .orElseThrow(
                () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        boolean exists = subClassRepository.existsByMemberAndSubClassNameAndMainClass(
            findMember, req.subClassName(), findMainClass);
        if (exists) {
            throw new ClassfitException("해당 메인 클래스 내에 이미 같은 이름의 서브 클래스가 있어요.", HttpStatus.CONFLICT);
        }

        checkMemberRelationMainClass(findMember, findMainClass);

        SubClass subClass = new SubClass(req.subClassName(), findMember, findMainClass);

        subClassRepository.save(subClass);

        return new SubClassResponse(req.mainClassId(), subClass.getId(),
            subClass.getSubClassName());
    }

    @Transactional
    // 서브 클래스 수정
    public SubClassResponse updateSubClass(@AuthMember Member findMember, Long subClassId, SubClassRequest req) {
        MainClass findMainClass = mainClassRepository.findById(req.mainClassId()).orElseThrow(
            () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass findSubClass = subClassRepository.findById(subClassId).orElseThrow(
            () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        checkMemberRelationSubClass(findMember, findSubClass);

        findSubClass.updateSubClassName(req.subClassName());

        return new SubClassResponse(findMainClass.getId(), findSubClass.getId(),
            findSubClass.getSubClassName());

    }

    @Transactional
    // 서브 클래스 삭제
    public void deleteSubClass(@AuthMember Member findMember, Long subClassId) {

        SubClass findSubClass = subClassRepository.findById(subClassId).orElseThrow(
            () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        checkMemberRelationSubClass(findMember, findSubClass);

        subClassRepository.delete(findSubClass);
    }
}
