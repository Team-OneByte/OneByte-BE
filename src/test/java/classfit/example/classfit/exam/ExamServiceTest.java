package classfit.example.classfit.exam;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import classfit.example.classfit.exam.domain.ExamScore;
import classfit.example.classfit.exam.domain.enumType.ExamPeriod;
import classfit.example.classfit.exam.domain.enumType.Standard;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.request.FindExamRequest;
import classfit.example.classfit.exam.dto.exam.request.UpdateExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamStudentResponse;
import classfit.example.classfit.exam.dto.exam.response.ShowExamDetailResponse;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.repository.ExamScoreRepository;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private ExamScoreRepository examScoreRepository;
    private Member findMember;
    private SubClass findSubClass;
    private MainClass findMainClass;
    private CreateExamRequest examRequest;
    private FindExamRequest findExamRequest;
    private List<Student> students;
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

        findMainClass = MainClass.builder()
                .id(1L)
                .mainClassName("메인1")
                .academy(academy1)
                .build();

        findSubClass = SubClass.builder()
                .id(1L)
                .subClassName("서브1")
                .mainClass(findMainClass)
                .build();

        Student student1 = Student.builder()
                .id(1L)
                .name("테스트학생1")
                .build();

        Student student2 = Student.builder()
                .id(2L)
                .name("테스트학생2")
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

    @Test
    void 시험지_반_학생찾기_성공() {

        Exam findExam = Exam.builder()
                .id(1L)
                .subClass(findSubClass)
                .mainClass(findMainClass)
                .highestScore(100)
                .standard(Standard.SCORE)
                .examName("테스트시험")
                .build();

        academy1 = mock(Academy.class);
        findMember = mock(Member.class);

        lenient().when(findMember.getAcademy()).thenReturn(academy1);
        lenient().when(academy1.getId()).thenReturn(1L);
        lenient().when(examRepository.findById(findExam.getId())).thenReturn(Optional.of(findExam));
        lenient().when(enrollmentRepository.findStudentsByAcademyIdAndSubClass(academy1.getId(),
                        findSubClass.getId()))
                .thenReturn(students);

        List<FindExamStudentResponse> response = examService.findExamClassStudent(findMember,
                findExam.getId());

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).studentName()).isEqualTo("테스트학생1");
        assertThat(response.get(1).studentName()).isEqualTo("테스트학생2");

        verify(examRepository, times(1)).findById(findExam.getId());
        verify(enrollmentRepository, times(1)).findStudentsByAcademyIdAndSubClass(academy1.getId(),
                findSubClass.getId());
    }

    @Test
    void 시험지_반_학생찾기_실패() {
        lenient().when(examRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClassfitException.class, () -> {
            examService.findExamClassStudent(findMember, 1L);
        });

        verify(examRepository, times(1)).findById(1L);
    }

    @Test
    void 시험지_검색성공() {

        FindExamRequest findExamRequest = new FindExamRequest(1L, 1L, "테스트선생님", "테스트시험1");

        Exam exam1 = Exam.builder()
                .id(1L)
                .examName("테스트시험1")
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .createdBy(findMember.getId())
                .build();

        Exam exam2 = Exam.builder()
                .id(2L)
                .examName("테스트시험2")
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .createdBy(findMember.getId())
                .build();

        List<Exam> exams = List.of(exam1, exam2);

        lenient().when(examRepository.findExamsByConditions(
                anyLong(), anyLong(), anyLong(), anyString(), anyString()
        )).thenReturn(exams);

        List<FindExamResponse> result = examService.findExamList(findMember, findExamRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(r -> r.examName().equals("테스트시험1")));
        assertTrue(result.stream()
                .anyMatch(r -> r.mainClassName().equals(findMainClass.getMainClassName())));
        assertTrue(result.stream()
                .anyMatch(r -> r.subClassName().equals(findSubClass.getSubClassName())));

        verify(examRepository, times(1)).findExamsByConditions(
                anyLong(), anyLong(), anyLong(), anyString(), anyString()
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
        // subClass X
        lenient().when(examRepository.findExamsByConditions(
                findMember.getAcademy().getId(),
                findExamRequest.mainClassId(),
                9L,
                findExamRequest.memberName(),
                findExamRequest.examName()
        )).thenReturn(List.of());

        assertThrows(ClassfitException.class, () -> {
            examService.findExamList(findMember, new FindExamRequest(1L, 9L, "테스트선생님", "테스트시험"));
        });

        lenient().when(examRepository.findExamsByConditions(
                findMember.getAcademy().getId(),
                findExamRequest.mainClassId(),
                findExamRequest.subClassId(),
                findExamRequest.memberName(),
                "에러시험"
        )).thenReturn(List.of());

        assertThrows(ClassfitException.class, () -> {
            examService.findExamList(findMember, new FindExamRequest(1L, 1L, "테스트선생님", "에러시험"));
        });
    }

    @Test
    void 시험지_삭제성공() {
        Exam exam = Exam.builder()
                .id(1L)
                .examName("테스트시험")
                .mainClass(findMainClass)
                .build();
        lenient().when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        doNothing().when(examRepository).delete(exam);

        examService.deleteExam(findMember, exam.getId());

        verify(examRepository, times(1)).delete(exam);
    }

    @Test
    void 시험지_삭제실패() {
        Exam exam = Exam.builder()
                .id(1L)
                .examName("테스트시험")
                .mainClass(findMainClass)
                .build();

        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.deleteExam(findMember, exam.getId());
        });

        lenient().when(examRepository.findById(exam.getId())).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.deleteExam(findMember, exam.getId());
        });
    }

    @Test
    void 시험지_수정_성공() {
        Exam exam = Exam.builder()
                .id(1L)
                .examDate(LocalDate.of(2025, 1, 1))
                .examName("테스트시험")
                .highestScore(100)
                .examPeriod(ExamPeriod.DAILY)
                .standard(Standard.QUESTION)
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .examRange("범위1")
                .build();

        lenient().when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        UpdateExamRequest request = new UpdateExamRequest(
                LocalDate.of(2025, 1, 1),
                Standard.QUESTION,
                70,
                ExamPeriod.DAILY,
                "테스트시험수정",
                List.of("범위2", "범위3")
        );

        String examRangeString = String.join(",", request.examRange());

        ExamScore score1 = mock(ExamScore.class);
        when(score1.getScore()).thenReturn(80);

        ExamScore score2 = mock(ExamScore.class);
        when(score2.getScore()).thenReturn(60);

        List<ExamScore> scores = List.of(score1, score2);

        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        when(examScoreRepository.findAllByAcademyIdAndExam(anyLong(), any())).thenReturn(scores);

        examService.updateExam(findMember, 1L, request);

        assertEquals("테스트시험수정", exam.getExamName());
        assertEquals(70, exam.getHighestScore());
        assertEquals(examRangeString, exam.getExamRange());

        verify(score1).updateScore(0);
        verify(score2, never()).updateScore(anyInt());
        verify(examRepository).save(exam);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 시험지_수정_실패(int highestScore) {
        Exam exam = Exam.builder()
                .id(1L)
                .examDate(LocalDate.of(2025, 1, 1))
                .examName("테스트시험")
                .highestScore(100)
                .examPeriod(ExamPeriod.DAILY)
                .standard(Standard.QUESTION)
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .examRange("범위1")
                .build();

        UpdateExamRequest request = new UpdateExamRequest(
                LocalDate.of(2025, 1, 1),
                Standard.QUESTION,
                highestScore,
                ExamPeriod.DAILY,
                "테스트시험수정",
                List.of("범위2", "범위3")
        );
        assertThrows(ClassfitException.class, () -> {
            examService.updateExam(findMember, exam.getId(), request);
        });

        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.updateExam(findMember, exam.getId(), request);
        });

        lenient().when(examRepository.findById(exam.getId())).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.updateExam(findMember, exam.getId(), request);
        });
    }

    @Test
    void 시험지_상세조회_성공() {
        Exam exam = Exam.builder()
                .id(1L)
                .examDate(LocalDate.of(2025, 1, 1))
                .examName("테스트시험")
                .highestScore(100)
                .examPeriod(ExamPeriod.DAILY)
                .standard(Standard.QUESTION)
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .examRange("범위1")
                .build();

        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        List<ExamScore> studentScores = List.of(
                ExamScore.builder()
                        .id(1L)
                        .student(students.get(0))
                        .exam(exam)
                        .score(90)
                        .build(),

                ExamScore.builder()
                        .id(2L)
                        .student(students.get(1))
                        .exam(exam)
                        .score(70)
                        .build()
        );

        exam.getExamScores().addAll(studentScores);
        lenient().when(examScoreRepository.findByExam(exam)).thenReturn(studentScores);

        List<Enrollment> enrollments = List.of(
                Enrollment.builder()
                        .id(1L)
                        .student(students.get(0))
                        .subClass(findSubClass)
                        .build(),

                Enrollment.builder()
                        .id(2L)
                        .student(students.get(1))
                        .subClass(findSubClass)
                        .build()
        );
        when(enrollmentRepository.findByAcademyIdAndSubClass(findMember.getAcademy().getId(),
                findSubClass)).thenReturn(enrollments);

        when(examScoreRepository.findByExamAndStudentIdAndAcademyId(
                findMember.getAcademy().getId(),
                exam, students.get(0).getId()))
                .thenReturn(Optional.of(studentScores.get(0)));
        when(examScoreRepository.findByExamAndStudentIdAndAcademyId(
                findMember.getAcademy().getId(),
                exam, students.get(1).getId()))
                .thenReturn(Optional.of(studentScores.get(1)));

        ShowExamDetailResponse result = examService.showExamDetail(findMember, exam.getId());

        assertEquals(ExamPeriod.DAILY, result.examPeriod());
        assertEquals("테스트시험", result.examName());
        assertEquals(LocalDate.of(2025, 1, 1), result.examDate());
        assertEquals(70, result.lowestScore());
        assertEquals(90, result.perfectScore());
        assertEquals("80.0", result.average());
        assertEquals(100, result.highestScore());
        assertEquals(2, result.examClassStudents().size());
        assertEquals(90, result.examClassStudents().get(0).score());
        assertEquals(70, result.examClassStudents().get(1).score());
    }

    @Test
    void 시험_상세조회_실패() {
        Exam exam = Exam.builder()
                .id(1L)
                .examName("테스트시험")
                .mainClass(findMainClass)
                .build();

        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.showExamDetail(findMember, exam.getId());
        });
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examService.showExamDetail(findMember, exam.getId());
        });
    }
}

