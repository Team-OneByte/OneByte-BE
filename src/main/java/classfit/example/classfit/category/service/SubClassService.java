package classfit.example.classfit.category.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
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

    private static void checkMemberRelationMainClass(Member findMember, MainClass findMainClass) {
        if (!Objects.equals(findMember.getAcademy().getId(), findMainClass.getAcademy().getId())) {
            throw new ClassfitException(ErrorCode.MAIN_CLASS_ACCESS_INVALID);
        }
    }

    private static void checkMemberRelationSubClass(Member findMember, SubClass findSubClass) {
        Academy academy = findSubClass.getMainClass().getAcademy();

        boolean isMemberInAcademy = academy.getMembers().stream()
                .anyMatch(member -> member.getId().equals(findMember.getId()));

        if (!isMemberInAcademy) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
    }


    @Transactional
    public SubClassResponse createSubClass(@AuthMember Member findMember, SubClassRequest req) {
        Academy findAcademy = findMember.getAcademy();

        MainClass findMainClass = mainClassRepository.findById(req.mainClassId())
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));

        boolean exists = subClassRepository.existsByMemberAndSubClassNameAndAcademyAndMainClass(
                findMember, findAcademy, req.subClassName(), findMainClass);
        if (exists) {
            throw new ClassfitException(ErrorCode.SUB_CLASS_ALREADY_EXISTS);
        }

        checkMemberRelationMainClass(findMember, findMainClass);

        SubClass subClass = new SubClass(req.subClassName(), findMainClass);

        subClassRepository.save(subClass);

        return SubClassResponse.from(subClass);
    }

    @Transactional
    public SubClassResponse updateSubClass(@AuthMember Member findMember, Long subClassId, SubClassRequest req) {
        MainClass findMainClass = mainClassRepository.findById(req.mainClassId()).orElseThrow(
            () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        SubClass findSubClass = subClassRepository.findById(subClassId).orElseThrow(
            () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));

        checkMemberRelationSubClass(findMember, findSubClass);

        findSubClass.updateSubClassName(req.subClassName());

        return new SubClassResponse(findMainClass.getId(), findSubClass.getId(),
            findSubClass.getSubClassName());

    }

    @Transactional
    public void deleteSubClass(@AuthMember Member findMember, Long subClassId) {

        SubClass findSubClass = subClassRepository.findById(subClassId).orElseThrow(
            () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));

        checkMemberRelationSubClass(findMember, findSubClass);

        subClassRepository.delete(findSubClass);
    }
}
