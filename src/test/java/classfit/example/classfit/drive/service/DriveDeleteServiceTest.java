package classfit.example.classfit.drive.service;

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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

@DisplayName("드라이브 삭제 테스트")
class DriveDeleteServiceTest {

    @InjectMocks
    private DriveDeleteService driveDeleteService;

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private S3Client s3Client;

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
    @DisplayName("개인 파일을 휴지통에서 삭제하고, S3에서 삭제한다")
    void 개인_드라이브_휴지통_비우기_케이스() {
        // Given
        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("file1", "file2");

        when(driveRepository.deletePersonalFilesFromTrash(member, objectNames)).thenReturn(2);

        // When
        driveDeleteService.deleteFromTrash(member, driveType, objectNames);

        // Then
        verify(driveRepository, times(1)).deletePersonalFilesFromTrash(member, objectNames);
        verify(driveRepository, never()).deleteSharedFilesFromTrash(any(), any());
        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("공용 파일을 휴지통에서 삭제하고 S3에서도 삭제한다")
    void 공용_드라이브_휴지통_비우기_케이스() {
        // Given
        DriveType driveType = DriveType.SHARED;
        List<String> objectNames = List.of("file1", "file2");

        when(driveRepository.deleteSharedFilesFromTrash(academy, objectNames)).thenReturn(2);

        // When
        driveDeleteService.deleteFromTrash(member, driveType, objectNames);

        // Then
        verify(driveRepository, times(1)).deleteSharedFilesFromTrash(academy, objectNames);
        verify(driveRepository, never()).deletePersonalFilesFromTrash(any(), any());
        verify(s3Client, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("파일 삭제가 실패했을 때, S3 삭제를 하지 않는다")
    void 휴지통_비우기_실패_케이스() {
        // Given
        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("file1", "file2");

        when(driveRepository.deletePersonalFilesFromTrash(member, objectNames)).thenReturn(0);

        // When
        driveDeleteService.deleteFromTrash(member, driveType, objectNames);

        // Then
        verify(driveRepository, times(1)).deletePersonalFilesFromTrash(member, objectNames);
        verify(s3Client, never()).deleteObjects(any(DeleteObjectsRequest.class));
    }
}
