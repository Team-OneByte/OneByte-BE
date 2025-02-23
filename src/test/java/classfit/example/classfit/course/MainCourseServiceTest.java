package classfit.example.classfit.course;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.dto.request.MainClassRequest;
import classfit.example.classfit.course.dto.response.MainClassResponse;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.service.MainClassService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
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

    @Mock
    private MemberRepository memberRepository;

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

}
