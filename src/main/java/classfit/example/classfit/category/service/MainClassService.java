package classfit.example.classfit.category.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.AllMainClassResponse;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
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

    @Transactional
    public MainClassResponse createMainClass(@AuthMember Member findMember, MainClassRequest req) {

        Academy academy = findMember.getAcademy();

        boolean exists = mainClassRepository.existsByAcademyAndMainClassName(academy,
            req.mainClassName());
        if (exists) {
            throw new ClassfitException("이미 같은 이름의 메인 클래스가 있어요.", HttpStatus.CONFLICT);
        }

        MainClass mainClass = new MainClass(req.mainClassName(), academy);
        mainClassRepository.save(mainClass);

        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }

    @Transactional(readOnly = true)
    public List<AllMainClassResponse> showMainClass(Member findMember) {

        Academy academy = findMember.getAcademy();

        List<MainClass> mainClasses = mainClassRepository.findByAcademy(academy);

        return mainClasses.stream().map(mainClass -> new AllMainClassResponse(mainClass.getId(),
            mainClass.getMainClassName())).toList();
    }

    @Transactional
    public void deleteMainClass(Member findMember, Long mainClassId) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
            () -> new ClassfitException("해당 메인 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException("사용자가 속한 학원 내의 클래스가 아닙니다.", HttpStatus.FORBIDDEN);
        }

        mainClassRepository.delete(mainClass);

    }

    @Transactional
    public MainClassResponse updateMainClass(Member findMember, Long mainClassId, MainClassRequest request) {

        MainClass mainClass = mainClassRepository.findById(mainClassId).orElseThrow(
            () -> new ClassfitException("해당 메인 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!Objects.equals(findMember.getAcademy(), mainClass.getAcademy())) {
            throw new ClassfitException("사용자가 속한 학원 내의 클래스가 아닙니다.", HttpStatus.FORBIDDEN);
        }
        mainClass.updateMainClassName(request.mainClassName());
        return new MainClassResponse(mainClass.getId(), mainClass.getMainClassName());
    }
}
