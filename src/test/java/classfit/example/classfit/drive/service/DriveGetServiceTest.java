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

@DisplayName("드라이브 조회 테스트")
class DriveGetServiceTest {

    @InjectMocks
    private DriveGetService driveGetService;

    @Mock
    private DriveRepository driveRepository;

    private Member member;

    private Academy academy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        academy = Academy.builder().id(1L).build();
        member = Member.builder()
                .id(1L)
                .academy(academy)
                .build();
    }

    @Test
    @DisplayName("개인 드라이브에 저장된 파일을 조회한다.")
    void 개인_파일_목록_조회_케이스() {
        // given
        List<PersonalDrive> mockDrives = List.of(
                PersonalDrive.builder().id(1L).objectName("file1").objectType("image").build(),
                PersonalDrive.builder().id(2L).objectName("file2").objectType("image").build()
        );

        // when
        when(driveRepository.findByMemberAndIsDeletedFalse(member)).thenReturn(mockDrives);
        List<DriveFileResponse> result = driveGetService.getObjectList(member, DriveType.PERSONAL);

        // then
        assertEquals(2, result.size());
        assertEquals("file1", result.getFirst().objectName());
        verify(driveRepository, times(1)).findByMemberAndIsDeletedFalse(member);
    }

    @Test
    @DisplayName("공용 드라이브에 저장된 파일을 조회한다.")
    void 공용_파일_목록_조회_케이스() {
        // given
        List<SharedDrive> mockDrives = List.of(
                SharedDrive.builder().id(1L).objectName("file1").objectType("image").build(),
                SharedDrive.builder().id(2L).objectName("file2").objectType("image").build()
        );

        // when
        when(driveRepository.findByAcademyAndIsDeletedFalse(academy)).thenReturn(
                mockDrives);
        List<DriveFileResponse> result = driveGetService.getObjectList(member, DriveType.SHARED);

        // then
        assertEquals(2, result.size());
        assertEquals("file1", result.getFirst().objectName());
        verify(driveRepository, times(1)).findByAcademyAndIsDeletedFalse(academy);
    }

    @Test
    @DisplayName("개인 드라이브 파일명으로 파일을 검색한다.")
    void 개인_드라이브_파일명_조회_케이스() {
        // given
        String objectName = "file1";
        List<PersonalDrive> mockDrives = List.of(
                PersonalDrive.builder().id(1L).objectName("file1").objectType("image").build()
        );
        when(driveRepository.findPersonalFilesByMember(member, objectName)).thenReturn(mockDrives);

        // when
        List<DriveFileResponse> result = driveGetService.searchFilesByName(member,
                DriveType.PERSONAL, objectName);

        // then
        assertEquals(1, result.size());
        assertEquals(objectName, result.getFirst().objectName());
        verify(driveRepository, times(1)).findPersonalFilesByMember(member, objectName);
    }

    @Test
    @DisplayName("공용 드라이브 파일명으로 파일을 검색한다.")
    void 공용_드라이브_파일명_조회_케이스() {
        // given
        String objectName = "file1";
        List<SharedDrive> mockDrives = List.of(
                SharedDrive.builder().id(1L).objectName("file1").objectType("image").build()
        );
        when(driveRepository.findSharedFilesByAcademy(member.getAcademy(), objectName)).thenReturn(
                mockDrives);

        // when
        List<DriveFileResponse> result = driveGetService.searchFilesByName(
                member,
                DriveType.SHARED,
                objectName
        );

        // then
        assertEquals(1, result.size());
        assertEquals(objectName, result.getFirst().objectName());
        verify(driveRepository, times(1)).findSharedFilesByAcademy(academy, objectName);
    }


    @Test
    @DisplayName("개인 드라이브에 해당하는 파일 확장자로 조회한다.")
    void 개인_드라이브_파일_확장자_조회_케이스() {
        // given
        String objectType = "image";
        List<PersonalDrive> mockDrives = List.of(
                PersonalDrive.builder().id(1L).objectName("imageFile").objectType("image").build(),
                PersonalDrive.builder().id(2L).objectName("file2").objectType("image").build()
        );
        when(driveRepository.findByMemberAndObjectTypeAndIsDeletedFalse(member,
                objectType)).thenReturn(mockDrives);

        // when
        List<DriveFileResponse> result = driveGetService.classifyFilesByType(member,
                DriveType.PERSONAL, objectType);

        // then
        assertEquals(2, result.size());
        assertEquals("imageFile", result.getFirst().objectName());
        verify(driveRepository, times(1))
                .findByMemberAndObjectTypeAndIsDeletedFalse(member, objectType);
    }

    @Test
    @DisplayName("공용 드라이브에 해당하는 파일 확장자로 조회한다.")
    void 공용_드라이브_파일_확장자_조회_케이스() {
        // given
        String objectType = "image";
        List<SharedDrive> mockDrives = List.of(
                SharedDrive.builder().id(1L).objectName("imageFile").objectType("image").build(),
                SharedDrive.builder().id(2L).objectName("file2").objectType("image").build()
        );
        when(driveRepository.findByAcademyAndObjectTypeAndIsDeletedFalse(academy, objectType))
                .thenReturn(mockDrives);

        // when
        List<DriveFileResponse> result = driveGetService.classifyFilesByType(
                member,
                DriveType.SHARED,
                objectType
        );

        // then
        assertEquals(2, result.size());
        assertEquals("imageFile", result.getFirst().objectName());
        verify(driveRepository, times(1))
                .findByAcademyAndObjectTypeAndIsDeletedFalse(academy, objectType);
    }
}