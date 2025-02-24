package classfit.example.classfit.course;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.dto.request.MainClassRequest;
import classfit.example.classfit.course.dto.response.AllMainClassResponse;
import classfit.example.classfit.course.dto.response.MainClassResponse;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.service.MainClassService;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MainCourseServiceTest {

    @InjectMocks
    MainClassService mainClassService;

    @Mock
    private MainClassRepository mainClassRepository;

    private Member findMember;
    private Academy academy1;

    @BeforeEach
    void setUp() {
        academy1 = Academy.builder()
                .id(1L)
                .name("테스트학원1")
                .build();

        findMember = Member.builder()
                .id(1L)
                .name("테스트선생님")
                .academy(academy1)
                .build();
    }

    @Test
    void 메인클래스_생성_성공() {
        MainClassRequest request = new MainClassRequest("테스트메인클래스");
        when(mainClassRepository.existsByAcademyAndMainClassName(academy1, request.mainClassName()))
                .thenReturn(false);

        when(mainClassRepository.save(any(MainClass.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MainClassResponse response = mainClassService.createMainClass(findMember, request);

        assertThat(response).isNotNull();
        assertThat(response.mainClassName()).isEqualTo("테스트메인클래스");

        verify(mainClassRepository, times(1)).save(any(MainClass.class));

    }

    @Test
    void 메인클래스_생성_실패() {

        MainClassRequest request = new MainClassRequest("테스트메인클래스");

        when(mainClassRepository.existsByAcademyAndMainClassName(academy1, request.mainClassName()))
                .thenReturn(true);

        assertThatThrownBy(() -> mainClassService.createMainClass(findMember, request))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.MAIN_CLASS_ALREADY_EXISTS.getMessage());

        verify(mainClassRepository, never()).save(any(MainClass.class));
    }

    @Test
    void 메인클래스_조회_성공() {

        MainClass mainClass1 = new MainClass("테스트메인클래스1", academy1);
        MainClass mainClass2 = new MainClass("테스트메인클래스2", academy1);
        List<MainClass> mainClassList = List.of(mainClass1, mainClass2);

        when(mainClassRepository.findByAcademy(academy1)).thenReturn(mainClassList);

        List<AllMainClassResponse> responseList = mainClassService.showMainClass(findMember);

        assertThat(responseList).hasSize(2);
        assertThat(responseList.get(0).mainClassName()).isEqualTo("테스트메인클래스1");
        assertThat(responseList.get(1).mainClassName()).isEqualTo("테스트메인클래스2");

        verify(mainClassRepository, times(1)).findByAcademy(academy1);
    }

    @Test
    void 메인클래스_수정_성공() {
        Long mainClassId = 1L;
        String updatedClassName = "수정메인클래스";
        MainClassRequest request = new MainClassRequest(updatedClassName);

        MainClass mainClass = new MainClass("기존메인클래스", academy1);
        lenient().when(mainClassRepository.findById(mainClassId))
                .thenReturn(Optional.of(mainClass));
        lenient().when(mainClassRepository.save(mainClass)).thenReturn(mainClass);

        MainClassResponse response = mainClassService.updateMainClass(findMember, 1L, request);

        assertThat(response).isNotNull();
        assertThat(response.mainClassName()).isEqualTo(updatedClassName);
        verify(mainClassRepository, times(1)).findById(mainClassId);
    }

    @Test
    void 메인클래스_수정_실패() {
        Long mainClassId = 1L;
        MainClassRequest request = new MainClassRequest("수정메인클래스");

        Academy anotherAcademy = Academy.builder().id(2L).name("academy2").build();
        Member anotherMember = Member.builder().id(2L).academy(anotherAcademy).build();
        MainClass mainClass = new MainClass("기존메인클래스", academy1);

        when(mainClassRepository.findById(mainClassId)).thenReturn(Optional.of(mainClass));

        assertThatThrownBy(
                () -> mainClassService.updateMainClass(anotherMember, mainClassId, request))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.ACADEMY_ACCESS_INVALID.getMessage());

    }

    @Test
    void 메인클래스_수정_메인클래스_없음_실패() {
        Long mainClassId = 1L;
        MainClassRequest request = new MainClassRequest("수정메인클래스");

        when(mainClassRepository.findById(mainClassId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mainClassService.updateMainClass(findMember, mainClassId, request))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.MAIN_CLASS_NOT_FOUND.getMessage());

        verify(mainClassRepository, times(1)).findById(mainClassId);
    }

    @Test
    void 메인클래스_삭제_성공() {
        Long mainClassId = 1L;
        MainClass mainClass = new MainClass("테스트메인클래스", academy1);

        when(mainClassRepository.findById(mainClassId)).thenReturn(Optional.of(mainClass));

        mainClassService.deleteMainClass(findMember, mainClassId);

        verify(mainClassRepository, times(1)).delete(mainClass);
    }

    @Test
    void 메인클래스_삭제_메인클래스_없음_실패() {
        Long mainClassId = 1L;

        when(mainClassRepository.findById(mainClassId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mainClassService.deleteMainClass(findMember, mainClassId))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.MAIN_CLASS_NOT_FOUND.getMessage());

        verify(mainClassRepository, never()).delete(any(MainClass.class));
    }

    @Test
    void 메인클래스_삭제_학원권한_실패() {
        Long mainClassId = 1L;

        Academy anotherAcademy = Academy.builder().id(2L).name("다른학원").build();
        Member anotherMember = Member.builder().id(2L).academy(anotherAcademy).build();
        MainClass mainClass = new MainClass("기존메인클래스", academy1);

        when(mainClassRepository.findById(mainClassId)).thenReturn(Optional.of(mainClass));

        assertThatThrownBy(() -> mainClassService.deleteMainClass(anotherMember, mainClassId))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.ACADEMY_ACCESS_INVALID.getMessage());

        verify(mainClassRepository, never()).delete(any(MainClass.class));
    }
}
