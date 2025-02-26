package classfit.example.classfit.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import classfit.example.classfit.student.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학생 등록 및 조회 테스트")
class StudentCreateServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private SubClassRepository subClassRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentRequest studentRequest;

    private Student student;

    private List<Enrollment> enrollments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SubClass subClass1 = createSubClass(1L, "수업 A", 1L, "학원 A");
        SubClass subClass2 = createSubClass(2L, "수업 B", 2L, "학원 B");

        enrollments = List.of(
                Enrollment.builder()
                        .subClass(subClass1)
                        .student(student)
                        .build(),
                Enrollment.builder()
                        .subClass(subClass2)
                        .student(student)
                        .build()
        );

        when(subClassRepository.findById(1L)).thenReturn(Optional.of(subClass1));
        when(subClassRepository.findById(2L)).thenReturn(Optional.of(subClass2));

        studentRequest = createStudentRequest(List.of(1L, 2L));
        student = studentRequest.toEntity(true);
    }

    @Test
    @DisplayName("입력한 학생의 정보으로 학생 등록한다.")
    void 학생_등록_성공_케이스() {
        // given
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(enrollmentRepository.findByStudent(any(Student.class))).thenReturn(enrollments);

        // when
        StudentResponse response = studentService.registerStudent(studentRequest);

        // then
        assertNotNull(response);
        assertEquals(studentRequest.name(), response.name());

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(enrollmentRepository, times(2)).save(any(Enrollment.class));
        verify(attendanceRepository, atLeast(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("등록된 수업이 없을 경우, 학생 등록에 실패한다.")
    void 학생_등록_실패_케이스() {
        // given
        StudentRequest requestWithoutClass = createStudentRequest(List.of());
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // when
        StudentResponse response = studentService.registerStudent(requestWithoutClass);

        // then
        assertNotNull(response);
        assertEquals(requestWithoutClass.name(), response.name());

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("등록된 학생 정보에 대해서 조회한다.")
    void 학생_조회_성공_케이스() {
        // given
        List<String> subClassList = List.of("수업 A", "수업 B");

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(studentRepository.findSubClassesByStudentId(student.getId())).thenReturn(subClassList);

        // when
        StudentInfoResponse response = studentService.getStudentInfo(student.getId());

        // then
        assertNotNull(response);
        assertEquals(student.getName(), response.name());

        verify(studentRepository, times(1)).findById(student.getId());
        verify(studentRepository, times(1)).findSubClassesByStudentId(student.getId());
    }

    @Test
    @DisplayName("학생이 등록되지 않았을 경우, 조회에 실패한다.")
    void 학생_조회_실패_케이스() {
        // given
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> studentService.getStudentInfo(student.getId()));

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());

        verify(studentRepository, times(1)).findById(student.getId());
        verify(studentRepository, never()).findSubClassesByStudentId(anyLong()); // 호출되지 않아야 함
    }

    @Test
    @DisplayName("특정 필드의 값의 학생 정보를 수정한다.")
    void 학생_정보_수정_성공_케이스() {
        // given
        StudentUpdateRequest updateRequest = createStudentUpdateRequest();

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        // when
        studentService.updateStudent(student.getId(), updateRequest);

        // then
        assertEquals("김철수", student.getName());
    }

    @Test
    @DisplayName("존재하지 않은 학생일 경우, 정보 수정에 실패한다.")
    void 학생_정보_수정_실패_케이스() {
        // given
        StudentUpdateRequest updateRequest = createStudentUpdateRequest();

        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> studentService.updateStudent(student.getId(), updateRequest));

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());

        verify(studentRepository, never()).save(any(Student.class));
    }

    private MainClass createMainClass(Long id, String name) {
        return MainClass.builder().id(id).mainClassName(name).build();
    }

    private SubClass createSubClass(Long id, String name, Long mainClassId, String mainClassName) {
        return SubClass.builder()
                .id(id)
                .subClassName(name)
                .mainClass(createMainClass(mainClassId, mainClassName))
                .build();
    }

    private StudentRequest createStudentRequest(List<Long> subClassIds) {
        return new StudentRequest(
                "홍길동", "MALE",
                LocalDate.of(2000, 1, 1),
                "01012345678", "01098765432",
                "3학년", subClassIds,
                "서울시 강남구", "비고 내용", "상담 기록"
        );
    }

    private StudentUpdateRequest createStudentUpdateRequest() {
        return new StudentUpdateRequest(
                "김철수", "MALE",
                LocalDate.of(2000, 1, 1),
                "01012345678", "01098765432",
                "3학년", List.of(1L, 2L),
                "서울시 강남구", true, "비고 내용", "상담 기록"
        );
    }
}