package classfit.example.classfit.drive.service;

import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriveRestoreService {

    private final DriveRepository driveRepository;

    @Transactional
    public Integer restoreTrash(Member member, DriveType driveType, List<String> objectNames) {
        return (driveType == DriveType.PERSONAL)
                ? driveRepository.restorePersonalFiles(member, objectNames)
                : driveRepository.restoreSharedFiles(member.getAcademy(), objectNames);
    }
}
