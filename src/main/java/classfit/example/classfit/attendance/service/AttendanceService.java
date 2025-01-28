package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.DateRangeUtil;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
    private final StudentRepository studentRepository;
    private final ClassStudentRepository classStudentRepository;

    public Page<Student> getAllStudents(int page, Member loggedInMember) {
        Long academyId = loggedInMember.getAcademy().getId();
        Pageable pageable = PageRequest.of(page, 50);
        return studentRepository.findAllByAcademyId(academyId, pageable);
    }

    public Page<ClassStudent> getClassStudentsByMainClassAndSubClass(Long mainClassId, Long subClassId, int page, Member loggedInMember) {
        Long academyId = loggedInMember.getAcademy().getId();
        Pageable pageable = PageRequest.of(page, 50);
        return classStudentRepository.findAllByMainClassAndSubClass(
            mainClassId, subClassId, academyId, pageable);
    }

    public List<StudentAttendanceResponse> getStudentAttendance(List<?> students, List<LocalDate> weekRange) {
        return students.stream()
            .map(studentObject -> mapToStudentAttendanceDTO(studentObject, weekRange))
            .sorted(Comparator.comparing(student -> student.name()))
            .collect(Collectors.toList());
    }

    private StudentAttendanceResponse mapToStudentAttendanceDTO(Object studentObject, List<LocalDate> weekRange) {
        Student student = getStudentFromEntity(studentObject);

        List<AttendanceResponse> attendanceDTOs = student.getClassStudents().stream()
            .flatMap(classStudent -> classStudent.getAttendances().stream())
            .filter(attendance -> weekRange.contains(attendance.getDate()))
            .sorted(Comparator.comparing(Attendance::getDate))
            .map(attendance -> AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getWeek(), attendance.getStatus().name()))
            .collect(Collectors.toList());

        return new StudentAttendanceResponse(student.getId(), student.getName(), attendanceDTOs);
    }

    private Student getStudentFromEntity(Object entity) {
        if (entity instanceof Student) {
            return (Student) entity;
        } else if (entity instanceof ClassStudent) {
            return ((ClassStudent) entity).getStudent();
        }
        throw new ClassfitException(ErrorCode.ENTITY_TYPE_INVALID);
    }

    public List<LocalDate> getWeeklyAttendanceRange(int weekOffset) {
        LocalDate today = LocalDate.now();
        return DateRangeUtil.getWeekRange(today, weekOffset);
    }
}