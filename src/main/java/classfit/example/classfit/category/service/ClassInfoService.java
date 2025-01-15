package classfit.example.classfit.category.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.dto.response.ClassInfoResponse;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassInfoService {
    private final MainClassRepository mainClassRepository;

    @Transactional
    public List<ClassInfoResponse> getClasses(Member member) {
        Academy academy = member.getAcademy();

        List<MainClass> mainClasses = mainClassRepository.findAllByAcademyOrderByMainClassNameAsc(academy);
        return mainClasses.stream()
            .map(this::mapToClassInfoResponse)
            .collect(Collectors.toList());
    }

    private ClassInfoResponse mapToClassInfoResponse(MainClass mainClass) {
        List<SubClassResponse> subClassResponses = mainClass.getSubClasses().stream()
            .sorted((subClass1, subClass2) -> subClass1.getSubClassName().compareTo(subClass2.getSubClassName()))
            .map(subClass -> mapToSubClassResponse(mainClass.getId(), subClass))
            .collect(Collectors.toList());
        return new ClassInfoResponse(mainClass.getId(), mainClass.getMainClassName(), subClassResponses);
    }

    private SubClassResponse mapToSubClassResponse(Long mainClassId, SubClass subClass) {
        return SubClassResponse.of(mainClassId, subClass.getId(), subClass.getSubClassName());
    }
}
