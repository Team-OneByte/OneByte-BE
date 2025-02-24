package classfit.example.classfit.course;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.dto.request.SubClassRequest;
import classfit.example.classfit.course.dto.response.SubClassResponse;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.course.service.SubClassService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SubCourseServiceTest {

    @InjectMocks
    SubClassService subClassService;

    @Mock
    private SubClassRepository subClassRepository;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MainClassRepository mainClassRepository;

    private Member findMember;
    private Academy academy1;
    private MainClass findMainClass;

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

        findMainClass = MainClass.builder()
                .id(1L)
                .academy(academy1)
                .mainClassName("테스트메인클래스")
                .build();
    }

    @Test
    void 서브클래스_생성_성공() {
        when(mainClassRepository.findById(findMainClass.getId()))
                .thenReturn(Optional.of(findMainClass));

        SubClassRequest request = new SubClassRequest(findMainClass.getId(), "테스트서브클래스");

        when(subClassRepository.existsByMemberAndSubClassNameAndAcademyAndMainClass(
                findMember, academy1, request.subClassName(), findMainClass))
                .thenReturn(false);

        SubClass subClass = SubClass.builder()
                .id(1L)
                .subClassName("테스트서브클래스")
                .mainClass(findMainClass)
                .build();

        when(subClassRepository.save(any(SubClass.class))).thenReturn(subClass);

        SubClassResponse response = subClassService.createSubClass(findMember, request);

        assertThat(response).isNotNull();
        assertThat(response.subClassName()).isEqualTo("테스트서브클래스");
        verify(subClassRepository, times(1)).save(any(SubClass.class));
    }

    @Test
    void 서브클래스_생성_실패() {
        SubClassRequest request = new SubClassRequest(findMainClass.getId(), "테스트서브클래스");
        when(mainClassRepository.findById(1L)).thenReturn(Optional.of(findMainClass));
        when(subClassRepository.existsByMemberAndSubClassNameAndAcademyAndMainClass(any(), any(),
                any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> subClassService.createSubClass(findMember, request))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.SUB_CLASS_ALREADY_EXISTS.getMessage());

        verify(subClassRepository, never()).save(any(SubClass.class));
    }

    @Test
    void 서브클래스_생성_메인클래스_없음_실패() {
        SubClassRequest request = new SubClassRequest(findMainClass.getId(), "테스트서브클래스");
        when(mainClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subClassService.createSubClass(findMember, request))
                .isInstanceOf(ClassfitException.class)
                .hasMessage(ErrorCode.MAIN_CLASS_NOT_FOUND.getMessage());

        verify(subClassRepository, never()).save(any(SubClass.class));
    }

}
