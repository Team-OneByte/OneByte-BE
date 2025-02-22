package classfit.example.classfit.exam;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.ExamScore;
import classfit.example.classfit.exam.dto.examscore.request.CreateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.request.UpdateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.response.CreateExamScoreResponse;
import classfit.example.classfit.exam.dto.examscore.response.UpdateExamScoreResponse;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.repository.ExamScoreRepository;
import classfit.example.classfit.exam.service.ExamScoreService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExamScoreServiceTest {

    @InjectMocks
    ExamScoreService examScoreService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ExamRepository examRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ExamScoreRepository examScoreRepository;

    private Member findMember;
    private SubClass findSubClass;
    private MainClass findMainClass;
    private List<Student> students;
    private Exam exam;

    @BeforeEach
    void setUp() {
        Academy academy1 = Academy.builder()
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
                .build();

        findSubClass = SubClass.builder()
                .id(1L)
                .mainClass(findMainClass)
                .build();

        Student student1 = Student.builder()
                .id(1L)
                .build();

        Student student2 = Student.builder()
                .id(2L)
                .build();

        exam = Exam.builder()
                .id(1L)
                .examName("테스트시험1")
                .mainClass(findMainClass)
                .subClass(findSubClass)
                .createdBy(findMember.getId())
                .build();

        Enrollment enrollment1 = mock(Enrollment.class);
        lenient().when(enrollment1.getStudent()).thenReturn(student1);

        Enrollment enrollment2 = mock(Enrollment.class);
        lenient().when(enrollment2.getStudent()).thenReturn(student2);
        students = List.of(student1, student2);
    }

    @Test
    void 학생점수_등록_성공() {
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(findMember));
        lenient().when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        List<CreateExamScoreRequest> requests = List.of(
                new CreateExamScoreRequest(1L, 1L, 80, null, null, true),
                new CreateExamScoreRequest(2L, 1L, 30, null, null, true)
        );
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

        lenient().when(examScoreRepository.save(any(ExamScore.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<CreateExamScoreResponse> responses = examScoreService.createExamScore(findMember,
                requests);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).studentId()).isEqualTo(1L);
        assertThat(responses.get(1).studentId()).isEqualTo(2L);
        verify(examScoreRepository, times(2)).save(any(ExamScore.class));
    }

    @Test
    void 학생점수_등록_실패() {

        List<CreateExamScoreRequest> requests = List.of(
                new CreateExamScoreRequest(1L, 1L, 80, null, null, true),
                new CreateExamScoreRequest(2L, 1L, 30, null, null, true)
        );
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examScoreService.createExamScore(findMember, requests);
        });
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examScoreService.createExamScore(findMember, requests);
        });
    }

    @Test
    void 학생점수_수정_성공() {

        lenient().when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.of(findMember));

        List<UpdateExamScoreRequest> requests = List.of(
                new UpdateExamScoreRequest(1L, 80, null, null, true),
                new UpdateExamScoreRequest(2L, 20, null, null, true)
        );
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

        lenient().when(examScoreRepository.save(any(ExamScore.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateExamScoreResponse responses = examScoreService.updateExamScore(findMember,
                exam.getId(),
                requests);

        assertThat(responses).isNotNull();
        assertThat(responses.examStudents()).hasSize(2);

        assertThat(responses.examStudents().get(0).score()).isEqualTo(80);
        assertThat(responses.examStudents().get(1).score()).isEqualTo(20);

        verify(examScoreRepository, times(2)).save(any(ExamScore.class));
    }

    @Test
    void 학생성적_수정_실패() {

        List<UpdateExamScoreRequest> requests = List.of(
                new UpdateExamScoreRequest(1L, 80, null, null, true),
                new UpdateExamScoreRequest(2L, 20, null, null, true)
        );
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examScoreService.updateExamScore(findMember, 1L, requests);
        });
        lenient().when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassfitException.class, () -> {
            examScoreService.updateExamScore(findMember, 1L, requests);
        });
    }
}
