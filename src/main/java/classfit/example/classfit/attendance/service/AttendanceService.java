package classfit.example.classfit.attendance.service;

import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.attendance.service.util.DateRangeUtil;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.attendance.dto.response.AttendanceResponse;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static classfit.example.classfit.exception.ClassfitException.INVALID_ENTITY_TYPE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
    private final StudentRepository studentRepository;
    private final ClassStudentRepository classStudentRepository;

    public Page<Student> getAllStudents(int page) {
        Pageable pageable = PageRequest.of(page, 50);
        return studentRepository.findAll(pageable);
    }

    public Page<ClassStudent> getClassStudentsByMainClassAndSubClass(Long mainClassId, Long subClassId, int page) {
        Pageable pageable = PageRequest.of(page, 50);
        return classStudentRepository.findBySubClass_MainClass_IdAndSubClass_Id(mainClassId, subClassId, pageable);
    }

    public List<StudentAttendanceResponse> getStudentAttendance(List<?> students, List<LocalDate> weekRange) {
        return students.stream()
                .map(studentObject -> mapToStudentAttendanceDTO(studentObject, weekRange))
                .collect(Collectors.toList());
    }

    private StudentAttendanceResponse mapToStudentAttendanceDTO(Object studentObject, List<LocalDate> weekRange) {
        Student student = getStudentFromEntity(studentObject);

        List<AttendanceResponse> attendanceDTOs = student.getAttendances().stream()
                .filter(attendance -> weekRange.contains(attendance.getDate()))
                .map(attendance -> AttendanceResponse.of(attendance.getId(), attendance.getDate(), attendance.getStatus().name()))
                .collect(Collectors.toList());

        return new StudentAttendanceResponse(student.getId(), student.getName(), attendanceDTOs);
    }

    private Student getStudentFromEntity(Object entity) {
        if (entity instanceof Student) {
            return (Student) entity;
        } else if (entity instanceof ClassStudent) {
            return ((ClassStudent) entity).getStudent();
        }
        throw new IllegalArgumentException(INVALID_ENTITY_TYPE);
    }

    public List<LocalDate> getWeeklyAttendanceRange(int weekOffset) {
        LocalDate today = LocalDate.now();
        return DateRangeUtil.getWeekRange(today, weekOffset);
    }
}