package classfit.example.classfit.drive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.drive.domain.PersonalDrive;
import classfit.example.classfit.drive.domain.SharedDrive;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("드라이브 휴지통 이동 및 조회 테스트")
class DriveTrashServiceTest {

    @InjectMocks
    private DriveTrashService driveTrashService;

    @Mock
    private DriveRepository driveRepository;

    private Member member;
    private Academy academy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        academy = Academy.builder().id(1L).build();
        member = Member.builder().id(1L).academy(academy).build();
    }

    @Test
    @DisplayName("개인 드라이브에 존재하는 파일을 휴지통으로 이동한다.")
    void 개인_드라이브_휴지통_이동_케이스() {
        // Given
        List<String> objectNames = List.of("file1.txt", "file2.txt");

        when(driveRepository.storePersonalFiles(member, objectNames)).thenReturn(2);

        // When
        int result = driveTrashService.storeTrash(member, DriveType.PERSONAL, objectNames);

        // Then
        assertEquals(2, result);
        verify(driveRepository, times(1)).storePersonalFiles(member, objectNames);
    }

    @Test
    @DisplayName("공용 드라이브에 존재하는 파일을 휴지통으로 이동한다.")
    void 공용_드라이브_휴지통_이동_케이스() {
        // Given
        List<String> objectNames = List.of("shared1.doc", "shared2.doc");

        when(driveRepository.storeSharedFiles(academy, objectNames)).thenReturn(2);

        // When
        int result = driveTrashService.storeTrash(member, DriveType.SHARED, objectNames);

        // Then
        assertEquals(2, result);
        verify(driveRepository, times(1)).storeSharedFiles(academy, objectNames);
    }

    @Test
    @DisplayName("개인 드라이브의 휴지통 목록을 조회한다.")
    void 개인_드라이브_휴지통_조회_케이스() {
        // Given
        List<PersonalDrive> deletedFiles = List.of(
                PersonalDrive.builder().objectName("file1.txt").build(),
                PersonalDrive.builder().objectName("file2.txt").build()
        );

        when(driveRepository.findDeletedPersonalFiles(member)).thenReturn(deletedFiles);

        // When
        List<DriveFileResponse> result = driveTrashService.getTrashList(member, DriveType.PERSONAL);

        // Then
        assertEquals(2, result.size());
        verify(driveRepository, times(1)).findDeletedPersonalFiles(member);
    }

    @Test
    @DisplayName("공용 드라이브의 휴지통 목록을 조회한다.")
    void 공용_드라이브_휴지통_조회_케이스() {
        // Given
        List<SharedDrive> deletedFiles = List.of(
                SharedDrive.builder().objectName("shared1.doc").build(),
                SharedDrive.builder().objectName("shared2.doc").build()
        );

        when(driveRepository.findDeletedSharedFiles(academy)).thenReturn(deletedFiles);

        // When
        List<DriveFileResponse> result = driveTrashService.getTrashList(member, DriveType.SHARED);

        // Then
        assertEquals(2, result.size());
        verify(driveRepository, times(1)).findDeletedSharedFiles(academy);
    }
}
