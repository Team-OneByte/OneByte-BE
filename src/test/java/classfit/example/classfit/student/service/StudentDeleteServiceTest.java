package classfit.example.classfit.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.scoreReport.repository.ScoreReportRepository;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import classfit.example.classfit.student.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학생 삭제 테스트")
class StudentDeleteServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ScoreReportRepository scoreReportRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ScoreReportService scoreReportService;

    @InjectMocks
    private StudentService studentService;

    private Member member;
    private Student student;
    private List<ScoreReport> scoreReports;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder().id(1L).build();
        student = Student.builder().id(1L).build();

        scoreReports = List.of(ScoreReport.builder()
                        .id(1L)
                        .student(student)
                        .build(),
                ScoreReport.builder()
                        .id(2L)
                        .student(student)
                        .build());
    }

    @Test
    @DisplayName("학생을 삭제하면 관련된 데이터도 삭제된다.")
    void 학생_삭제_성공_케이스() {
        // given
        List<Long> studentIds = List.of(student.getId());

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(scoreReportService.findByStudentId(student.getId())).thenReturn(scoreReports);

        // when
        studentService.deleteStudent(member, studentIds);

        // then
        verify(scoreReportService, times(2)).deleteReport(eq(member), anyLong());
        verify(scoreReportRepository, times(1)).deleteByStudentId(student.getId());
        verify(attendanceRepository, times(1)).deleteByStudentId(student.getId());
        verify(enrollmentRepository, times(1)).deleteAllByStudentId(student.getId());
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    @DisplayName("존재하지 않는 학생을 삭제할 경우, 예외 발생한다.")
    void 학생_삭제_실패_케이스() {
        // given
        List<Long> studentIds = List.of(999L);

        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> studentService.deleteStudent(member, studentIds));

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).delete(any(Student.class));
    }
}
