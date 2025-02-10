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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DriveGetService {

    private final DriveRepository driveRepository;

    public List<DriveFileResponse> getObjectList(Member member, DriveType driveType, String folderPath) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findByMemberAndObjectPath(member, folderPath)
                : driveRepository.findByAcademyAndObjectPath(member.getAcademy(), folderPath);

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }


    public List<DriveFileResponse> searchFilesByName(Member member, DriveType driveType, String fileName, String folderPath) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findPersonalFilesByMember(member, folderPath, fileName)
                : driveRepository.findSharedFilesByAcademy(member.getAcademy(), folderPath, fileName);

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }

    public List<DriveFileResponse> classifyFilesByType(Member member, DriveType driveType, String objectType, String folderPath) {
        List<Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findByMemberAndObjectPathAndObjectType(member, folderPath, objectType)
                : driveRepository.findByAcademyAndObjectPathAndObjectType(member.getAcademy(), folderPath, objectType);

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }
}