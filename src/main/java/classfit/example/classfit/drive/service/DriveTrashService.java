package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.domain.Drive;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriveTrashService {

    private final DriveRepository driveRepository;

    @Transactional
    public Integer storeTrash(Member member, DriveType driveType, List<String> objectNames) {
        return (driveType == DriveType.PERSONAL)
        ? driveRepository.storePersonalFiles(member, objectNames)
                : driveRepository.storeSharedFiles(member.getAcademy(), objectNames);
    }

    @Transactional(readOnly = true)
    public List<DriveFileResponse> getTrashList(Member member, DriveType driveType) {
        List<? extends Drive> drives = (driveType == DriveType.PERSONAL)
                ? driveRepository.findDeletedPersonalFiles(member)
                : driveRepository.findDeletedSharedFiles(member.getAcademy());

        return drives.stream()
                .map(DriveFileResponse::of)
                .collect(Collectors.toList());
    }
}
