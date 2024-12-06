package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.attendance.dto.response.StatisticsDateResponse;
import classfit.example.classfit.attendance.dto.response.StatisticsMemberResponse;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.student.domain.Student;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceStatisticsService {

    private final AttendanceRepository attendanceRepository;
    private final ClassStudentRepository classStudentRepository;

    public List<StatisticsDateResponse> getAttendanceStatisticsByDate(LocalDate startDate, LocalDate endDate, Long subClassId) {
        List<Attendance> attendances = attendanceRepository.findByDateBetweenAndSubClassId(startDate, endDate, subClassId);

        return attendances.stream()
            .collect(Collectors.groupingBy(Attendance::getDate)) // 날짜별로 그룹화
            .entrySet().stream()
            .map(entry -> createStatisticsDateResponse(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(StatisticsDateResponse::date)
                .thenComparingInt(StatisticsDateResponse::week))
            .collect(Collectors.toList());
    }

    private StatisticsDateResponse createStatisticsDateResponse(LocalDate date, List<Attendance> dayAttendances) {
        Attendance firstAttendance = dayAttendances.get(0); // week 정보 가져오기
        return new StatisticsDateResponse(
            date,
            firstAttendance.getWeek(),
            countByStatus(dayAttendances, AttendanceStatus.PRESENT),
            countByStatus(dayAttendances, AttendanceStatus.ABSENT),
            countByStatus(dayAttendances, AttendanceStatus.LATE),
            0 // extraCount
        );
    }

    public List<StatisticsMemberResponse> getAttendanceStatisticsByMember(LocalDate startDate, LocalDate endDate) {
        List<ClassStudent> allClassStudents = classStudentRepository.findAll();

        return allClassStudents.stream()
            .map(classStudent -> createStatisticsMemberResponse(classStudent, startDate, endDate))
            .sorted(Comparator.comparing(StatisticsMemberResponse::name))
            .collect(Collectors.toList());
    }

    private StatisticsMemberResponse createStatisticsMemberResponse(ClassStudent classStudent, LocalDate startDate, LocalDate endDate) {
        Student student = classStudent.getStudent();
        List<Attendance> studentAttendances = attendanceRepository.findByStudentIdAndDateBetween(student.getId(), startDate, endDate);

        return new StatisticsMemberResponse(
            student.getName(),
            countByStatus(studentAttendances, AttendanceStatus.PRESENT),
            countByStatus(studentAttendances, AttendanceStatus.ABSENT),
            countByStatus(studentAttendances, AttendanceStatus.LATE),
            0 // extraCount
        );
    }

    private int countByStatus(List<Attendance> attendances, AttendanceStatus status) {
        return (int) attendances.stream().filter(a -> a.getStatus() == status).count();
    }
}