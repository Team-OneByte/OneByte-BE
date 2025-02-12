package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriveGetService {

    private final DriveRepository driveRepository;

    @Transactional(readOnly = true)
    public List<DriveFileResponse> getObjectList(Member member, DriveType driveType) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findByMemberAndIsDeletedFalse(member)
                : driveRepository.findByAcademyAndIsDeletedFalse(member.getAcademy());

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriveFileResponse> searchFilesByName(Member member, DriveType driveType, String objectName) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findPersonalFilesByMember(member, objectName)
                : driveRepository.findSharedFilesByAcademy(member.getAcademy(), objectName);

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriveFileResponse> classifyFilesByType(Member member, DriveType driveType, String objectType) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findByMemberAndObjectTypeAndIsDeletedFalse(member, objectType)
                : driveRepository.findByAcademyAndObjectTypeAndIsDeletedFalse(member.getAcademy(), objectType);

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }
}