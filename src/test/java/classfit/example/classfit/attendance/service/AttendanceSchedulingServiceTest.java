package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.member.domain.Gender;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AttendanceSchedulingServiceTest {

    @InjectMocks
    private AttendanceSchedulingService attendanceSchedulingService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Transactional
    List<Student> generateTestStudents(int count) {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            students.add(
                Student.builder().name("학생" + i).gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                    .birth(LocalDate.of(2010, 1, 1).plusDays(i))
                    .studentNumber("010" + String.format("%08d", i))
                    .parentNumber("010" + String.format("%08d", i + 1000)).grade("1학년")
                    .address("서울특별시 강남구").isStudent(true).build());
        }
        return students;
    }

    @Test
    @Transactional
    void createWeeklyAttendanceTest() {
        // Given
        LocalDate lastDate = LocalDate.of(2024, 11, 10);
        LocalDate nextWeekMonday = lastDate.plusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        List<Student> students = generateTestStudents(5);

        // When
        when(attendanceRepository.findLastGeneratedDate()).thenReturn(Optional.of(lastDate));
        when(studentRepository.findAll()).thenReturn(students);
        when(attendanceRepository.existsByStudentAndDate(any(Student.class),
            any(LocalDate.class))).thenAnswer(invocation -> {
            LocalDate date = invocation.getArgument(1);
            // 마지막 생성된 주차 이후 날짜는 존재하지 않는 것으로 설정
            return !date.isAfter(nextWeekMonday.minusDays(1));
        });

        attendanceSchedulingService.createWeeklyAttendance();

        // Then
        for (int i = 0; i < 7; i++) {
            LocalDate expectedDate = nextWeekMonday.plusDays(i); // 예상 생성 날짜 (월요일부터 일요일까지)
            System.out.println(
                "검증 중: 날짜 = " + expectedDate + ", 요일 = " + expectedDate.getDayOfWeek());

            verify(attendanceRepository, times(students.size())).save(argThat(attendance -> {
                boolean matches = attendance.getDate().equals(expectedDate) && students.contains(
                    attendance.getStudent());
                if (!matches) {
                    System.out.println("매칭 실패: 날짜 = " + attendance.getDate() + ", 학생 = "
                        + attendance.getStudent());
                }
                return matches;
            }));
        }
        verify(attendanceRepository, times(7 * students.size())).save(any(Attendance.class));
    }
}