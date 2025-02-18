package classfit.example.classfit.exam;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.enumType.ExamPeriod;
import classfit.example.classfit.exam.domain.enumType.Standard;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.request.FindExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamResponse;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.service.ExamService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.EnrollmentRepository;
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
    @Mock
    private EnrollmentRepository enrollmentRepository;
    private Member findMember;
    private SubClass findSubClass;
    private MainClass findMainClass;
    private CreateExamRequest examRequest;
    private FindExamRequest findExamRequest;
    private List<Student> students;

    @BeforeEach
    void setUp() {
        Academy academy1 = Academy.builder()
                .id(1L)
                .name("테스트학원1")
                .build();

        findMember = Member.builder()
                .id(1L)
                .academy(academy1)
                .build();

        findMainClass = MainClass.builder()
                .id(1L)
                .academy(academy1)
                .build();

        findSubClass = SubClass.builder()
                .id(1L)
                .mainClass(findMainClass)
                .build();

        Student student1 = Student.builder()
                .id(1L).
                build();

        Student student2 = Student.builder()
                .id(2L)
                .build();

        Enrollment enrollment1 = mock(Enrollment.class);
        lenient().when(enrollment1.getStudent()).thenReturn(student1);

        Enrollment enrollment2 = mock(Enrollment.class);
        lenient().when(enrollment2.getStudent()).thenReturn(student2);
        students = List.of(student1, student2);

        lenient().when(academyRepository.findById(1L)).thenReturn(Optional.of(academy1));

    }

    @Test
    void 시험생성_성공() {
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

    @Test
    void 시험생성_실패() {
        // subclass 존재x
        lenient().when(subClassRepository.findById(1L)).thenReturn(Optional.empty());
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
        assertThrows(ClassfitException.class, () -> {
            examService.createExam(findMember, examRequest);
        });

        // member 존재x
        lenient().when(subClassRepository.findById(1L)).thenReturn(Optional.of(findSubClass));
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
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
        assertThrows(ClassfitException.class, () -> {
            examService.createExam(findMember, examRequest);
        });

        // 저장실패
        lenient().when(subClassRepository.findById(1L)).thenReturn(Optional.of(findSubClass));
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(findMember));
        lenient().when(mainClassRepository.findById(1L)).thenReturn(Optional.of(findMainClass));

        when(examRepository.save(any(Exam.class))).thenThrow(new RuntimeException("저장 실패"));
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
        assertThrows(RuntimeException.class, () -> {
            examService.createExam(findMember, examRequest);
        });
    }

    //    @Test
//    void 시험지_반_학생찾기_성공() {
//        Exam findExam = Exam.builder().id(1L).build();
//        lenient().when(subClassRepository.findById(1L)).thenReturn(Optional.of(findSubClass));
//        lenient().when(examRepository.findById(1L)).thenReturn(Optional.of(findExam));
//        lenient().when(enrollmentRepository.findStudentsByAcademyIdAndSubClass(1L, findSubClass))
//                .thenReturn(List.of(students.get(0), students.get(1)));
//
//        List<FindExamStudentResponse> result = examService.findExamClassStudent(findMember, 1L);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertTrue(result.contains(FindExamStudentResponse.of(students.get(0))));
//        assertTrue(result.contains(FindExamStudentResponse.of(students.get(1))));
//    }

    @Test
    void 시험지_검색성공() {
        findExamRequest = new FindExamRequest(1L, 1L, "테스트선생님", "테스트시험");
        Exam exam1 = Exam.builder()
                .id(1L)
                .examName("테스트시험1")
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .build();

        Exam exam2 = Exam.builder()
                .id(2L)
                .examName("테스트시험2")
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .build();

        List<Exam> exams = List.of(exam1, exam2);

        lenient().when(examRepository.findExamsByConditions(
                findMember.getAcademy().getId(),
                findExamRequest.mainClassId(),
                findExamRequest.subClassId(),
                findExamRequest.memberName(),
                findExamRequest.examName()
        )).thenReturn(exams);

        List<FindExamResponse> result = examService.findExamList(findMember, findExamRequest);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(response -> response.examName().equals("테스트시험1")));
        assertTrue(result.stream().anyMatch(response -> response.examName().equals("테스트시험2")));

        verify(examRepository, times(1)).findExamsByConditions(
                findMember.getAcademy().getId(),
                findExamRequest.mainClassId(),
                findExamRequest.subClassId(),
                findExamRequest.memberName(),
                findExamRequest.examName()
        );
    }

    @Test
    void 시험지_검색실패() {
        // member 존재 x
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        findExamRequest = new FindExamRequest(1L, 1L, "테스트선생님", "테스트시험");
        assertThrows(ClassfitException.class, () -> {
            examService.findExamList(findMember, findExamRequest);
        });
        // exams.isEmpty()
        lenient().when(examRepository.findExamsByConditions(
                findMember.getAcademy().getId(),
                findExamRequest.mainClassId(),
                findExamRequest.subClassId(),
                findExamRequest.memberName(),
                findExamRequest.examName()
        )).thenReturn(List.of());

        assertThrows(ClassfitException.class, () -> {
            examService.findExamList(findMember, findExamRequest);
        });
    }

}

