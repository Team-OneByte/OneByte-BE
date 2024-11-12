package classfit.example.classfit.category.service;

import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.repository.MainClassRespository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.domain.MainClass;
import classfit.example.classfit.domain.Member;
import classfit.example.classfit.domain.SubClass;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.member.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubClassService {

    private final SubClassRepository subClassRepository;
    private final MainClassRespository mainClassRespository;
    private final MemberRepository memberRepository;

    @Transactional
    // 서브클래스 추가
    public SubClassResponse addSubClass(Long memberId, SubClassRequest req) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        MainClass findMainClass = mainClassRespository.findById(req.mainClassId()).orElseThrow(
                () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        checkMemberRelationMainClass(findMember, findMainClass);
        SubClass subClass = new SubClass(req.subClassName(), findMember);

        subClassRepository.save(subClass);

        return new SubClassResponse(req.mainClassId(), subClass.getId(),
                subClass.getSubClassName());
    }

    @Transactional
    // 서브 클래스 수정
    public SubClassResponse updateSubClass(Long memberId, Long subClassId, SubClassRequest req) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        MainClass findMainClass = mainClassRespository.findById(req.mainClassId()).orElseThrow(
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
    public void deleteSubClass(Long memberId, Long subClassId) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        SubClass findSubClass = subClassRepository.findById(subClassId).orElseThrow(
                () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        checkMemberRelationSubClass(findMember, findSubClass);

        subClassRepository.delete(findSubClass);
    }

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
}
