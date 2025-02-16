package classfit.example.classfit.exam;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.enumType.ExamPeriod;
import classfit.example.classfit.exam.domain.enumType.Standard;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.service.ExamService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.domain.enumType.MemberType;
import classfit.example.classfit.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {

    @InjectMocks
    ExamService examService;
    @Mock
    private SubClassRepository subClassRepository;

    @Mock
    private MainClassRepository mainClassRepository;

    @Mock
    private ExamRepository examRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AcademyRepository academyRepository;

    private Member findMember;
    private SubClass findSubClass;
    private MainClass findMainClass;
    private CreateExamRequest examRequest;

    @BeforeEach
    void setUp() {
        Academy academy1 = Academy.builder()
                .id(1L)
                .name("testAcademy1")
                .build();

        findMember = Member.builder()
                .id(1L)
                .email("test1@naver.com")
                .password("koo1234")
                .name("테스트선생님")
                .role("ADMIN")
                .phoneNumber("01012345678")
                .status(MemberType.ACTIVE)
                .birthDate(LocalDate.parse("2000-01-01"))
                .subject("수학")
                .academy(academy1)
                .build();

        findMainClass = MainClass.builder()
                .id(1L)
                .mainClassName("테스트메인클래스1")
                .academy(academy1)
                .build();

        findSubClass = SubClass.builder()
                .id(1L)
                .subClassName("테스트서브클래스1")
                .mainClass(findMainClass)
                .build();
        lenient().when(academyRepository.findById(1L)).thenReturn(Optional.of(academy1));

    }

    @Test
    void 시험생성() {
        lenient().when(subClassRepository.findById(1L)).thenReturn(Optional.of(findSubClass));
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(findMember));
        lenient().when(mainClassRepository.findById(1L)).thenReturn(Optional.of(findMainClass));
        when(examRepository.save(any(Exam.class))).thenAnswer(invocation -> {
            Exam exam = invocation.getArgument(0);
            ReflectionTestUtils.setField(exam, "id", 1L);
            return exam;
        });

        examRequest = new CreateExamRequest(
                1L,
                1L,
                LocalDate.now(),
                Standard.QUESTION,
                100,
                ExamPeriod.DAILY,
                "테스트시험",
                List.of("1단원", "2단원")
        );
        CreateExamResponse response = examService.createExam(findMember, examRequest);

        assertNotNull(response);
        assertNotNull(response.examId());

        verify(subClassRepository, times(1)).findById(1L);
        verify(mainClassRepository, times(1)).findById(1L);
        verify(examRepository, times(1)).save(any());
    }

}

