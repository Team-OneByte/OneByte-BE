package classfit.example.classfit.category.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.AllMainClassResponse;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
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

    // 메인 클래스 추가
    @Transactional
    public MainClassResponse addMainClass(@AuthMember Member findMember, MainClassRequest req) {

        Academy academy = findMember.getAcademy();

        boolean exists = mainClassRepository.existsByAcademyAndMainClassName(academy,
            req.mainClassName());
        if (exists) {
            throw new ClassfitException(ErrorCode.MAIN_CLASS_ALREADY_EXISTS);
        }

        MainClass mainClass = new MainClass(req.mainClassName(), academy);
        mainClassRepository.save(mainClass);

        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }

    // 메인 클래스 전체 조회
    @Transactional(readOnly = true)
    public List<AllMainClassResponse> showMainClass(Member findMember) {

        Academy academy = findMember.getAcademy();

        List<MainClass> mainClasses = mainClassRepository.findByAcademy(academy);

        return mainClasses.stream().map(mainClass -> new AllMainClassResponse(mainClass.getId(),
            mainClass.getMainClassName())).toList();
    }

    // 메인 클래스 삭제
    @Transactional
    public void deleteMainClass(Member findMember, Long mainClassId) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
            () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }

        mainClassRepository.delete(mainClass);

    }

    @Transactional
    public MainClassResponse updateMainClass(Member findMember, Long mainClassId, MainClassRequest request) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
            () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
        mainClass.updateMainClassName(request.mainClassName());
        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }
}
