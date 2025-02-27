package classfit.example.classfit.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학생 전체 및 특정 조회 테스트")
class StudentQueryServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Member member;
    private Academy academy;
    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        academy = Academy.builder()
                .id(1L)
                .name("테스트 학원")
                .build();
        member = Member.builder()
                .id(1L)
                .academy(academy)
                .build();

        studentList = createStudent();
    }

    @Test
    @DisplayName("학생 전체 목록을 정상적으로 조회한다.")
    void 학생_전체조회_성공_케이스() {
        // given
        when(studentRepository.findStudentsByAcademyId(academy.getId())).thenReturn(studentList);

        // when
        List<StudentResponse> result = studentService.studentInfoAll(member);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("홍길동", result.get(0).name());
        assertEquals("김철수", result.get(1).name());

        verify(studentRepository, times(1)).findStudentsByAcademyId(academy.getId());
    }

    @Test
    @DisplayName("등록되지 않은 학생에 대해 조회한다.")
    void 학생_전체조회_실패_케이스() {
        // given
        when(studentRepository.findStudentsByAcademyId(academy.getId())).thenReturn(List.of());

        // when
        List<StudentResponse> result = studentService.studentInfoAll(member);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(studentRepository, times(1)).findStudentsByAcademyId(academy.getId());
    }

    @Test
    @DisplayName("특정 이름으로 학생을 정상적으로 조회한다.")
    void 특정_학생_검색_성공_케이스() {
        // given
        String searchName = "홍길동";
        when(studentRepository.findAllByName(searchName)).thenReturn(Optional.of(createStudent()));

        // when
        List<StudentResponse> result = studentService.findStudentsByName(searchName);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("홍길동", result.get(0).name());
        assertNotEquals("홍길동", result.get(1).name());

        verify(studentRepository, times(1)).findAllByName(searchName);
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 검색 시 예외를 반환한다.")
    void 특정_학생_검색_실패_케이스() {
        // given
        String searchName = "존재하지 않는 이름";

        when(studentRepository.findAllByName(searchName)).thenReturn(Optional.empty());

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> studentService.findStudentsByName(searchName));

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
        verify(studentRepository, times(1)).findAllByName(searchName);
    }

    static List<Student> createStudent() {
        return List.of(
                Student.builder()
                        .id(1L)
                        .name("홍길동")
                        .grade("A+")
                        .build(),
                Student.builder()
                        .id(2L)
                        .name("김철수")
                        .grade("A+")
                        .build()
        );
    }
}
