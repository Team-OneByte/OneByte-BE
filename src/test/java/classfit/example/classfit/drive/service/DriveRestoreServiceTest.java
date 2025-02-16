package classfit.example.classfit.drive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("드라이브 휴지통 복원 테스트")
public class DriveRestoreServiceTest {

    @InjectMocks
    private DriveRestoreService driveRestoreService;

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private Member member;

    @Mock
    private Academy academy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(member.getAcademy()).thenReturn(academy);
    }

    @Test
    @DisplayName("개인 드라이브 휴지통에 있는 파일을 복원한다")
    void 개인_드라이브_휴지통_복원() {
        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("file1", "file2");

        when(driveRepository.restorePersonalFiles(member, objectNames)).thenReturn(2);

        // When
        Integer restoredCount = driveRestoreService.restoreTrash(member, driveType, objectNames);

        // Then
        assertEquals(2, restoredCount);
        verify(driveRepository, times(1)).restorePersonalFiles(member, objectNames);
        verify(driveRepository, never()).restoreSharedFiles(any(), any());
    }

    @Test
    @DisplayName("공용 파일을 복구한다")
    void 공용_드라이브_휴지통_복원() {
        // Given
        DriveType driveType = DriveType.SHARED;
        List<String> objectNames = List.of("file1", "file2");

        when(driveRepository.restoreSharedFiles(academy, objectNames)).thenReturn(2);

        // When
        Integer restoredCount = driveRestoreService.restoreTrash(member, driveType, objectNames);

        // Then
        assertEquals(2, restoredCount);
        verify(driveRepository, times(1)).restoreSharedFiles(academy, objectNames);
        verify(driveRepository, never()).restorePersonalFiles(any(), any());
    }
}
