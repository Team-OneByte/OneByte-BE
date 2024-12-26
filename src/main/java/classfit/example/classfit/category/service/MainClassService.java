package classfit.example.classfit.category.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.AllMainClassResponse;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MainClassService {

    private final MainClassRepository mainClassRepository;
    private final MemberRepository memberRepository;

    private static void checkMemberRelationMainClass(Member findMember, MainClass findMainClass) {
        if (!Objects.equals(findMember.getId(), findMainClass.getMember().getId())) {
            throw new ClassfitException("사용자와 클래스가 일치하지 않습니다.", HttpStatus.FORBIDDEN);
        }
    }

    // 메인 클래스 추가
    @Transactional
    public MainClassResponse addMainClass(@AuthMember Member findMember, MainClassRequest req) {

        boolean exists = mainClassRepository.existsByMemberAndMainClassName(findMember,
            req.mainClassName());
        if (exists) {
            throw new ClassfitException("이미 같은 이름의 메인 클래스가 있어요.", HttpStatus.CONFLICT);
        }

        MainClass mainClass = new MainClass(req.mainClassName(), findMember);
        mainClassRepository.save(mainClass);

        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }

    // 메인 클래스 전체 조회
    @Transactional(readOnly = true)
    public List<AllMainClassResponse> showMainClass(@AuthMember Member findMember) {

        List<MainClass> mainClasses = mainClassRepository.findAll();

        return mainClasses.stream().map(mainClass -> new AllMainClassResponse(mainClass.getId(),
            mainClass.getMainClassName())).toList();
    }

    // 메인 클래스 삭제
    @Transactional
    public void deleteMainClass(@AuthMember Member findMember, Long mainClassId) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
            () -> new ClassfitException("해당 메인 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        mainClassRepository.delete(mainClass);

    }
}
