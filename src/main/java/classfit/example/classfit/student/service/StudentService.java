package classfit.example.classfit.student.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.domain.AttendanceStatus;
import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.scoreReport.domain.ScoreReportRepository;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import classfit.example.classfit.student.domain.Gender;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubClassRepository subClassRepository;
    private final ClassStudentRepository classStudentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScoreReportRepository scoreReportRepository;
    private final ScoreReportService scoreReportService;

    @Transactional
    public StudentResponse registerStudent(StudentRequest request) {
        Student student = request.toEntity(true);
        studentRepository.save(student);

        request.subClassList().forEach(subClassId -> {
            SubClass subClass = getSubClass(subClassId);
            ClassStudent classStudent = ClassStudent.create(student, subClass);
            classStudentRepository.save(classStudent);

            createAttendanceForSevenWeeks(student);
        });

        return StudentResponse.from(student);
    }

    private void createAttendanceForSevenWeeks(Student student) {
        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate.with(DayOfWeek.MONDAY).minusWeeks(4);

        List<ClassStudent> classStudents = classStudentRepository.findByStudent(student);

        classStudents.forEach(classStudent -> {
            for (int i = 0; i < 7; i++) {
                LocalDate weekDate = weekStart.plusWeeks(i);
                AttendanceStatus status = (i < 4) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT; // 4주 전은 ABSENT, 나머지는 PRESENT

                for (int j = 0; j < 7; j++) {
                    LocalDate attendanceDate = weekDate.plusDays(j);
                    Attendance attendance = Attendance.builder()
                        .date(attendanceDate)
                        .week(j)
                        .status(status)
                        .classStudent(classStudent)
                        .build();
                    attendanceRepository.save(attendance);
                }
            }
        });
    }

    @Transactional
    public void deleteStudent(Member member, List<Long> studentIds) {
        studentIds.stream()
            .map(studentId -> studentRepository.findById(studentId).orElseThrow(()
                -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND)))
            .forEach(student -> {
                List<ScoreReport> scoreReports = scoreReportService.findByStudentId(student.getId());
                for (ScoreReport scoreReport : scoreReports) {
                    scoreReportService.deleteReport(member, scoreReport.getId());
                }

                scoreReportRepository.deleteByStudentId(student.getId());
                attendanceRepository.deleteByStudentId(student.getId());
                classStudentRepository.deleteAllByStudentId(student.getId());
                studentRepository.delete(student);
            });
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> studentInfoAll(Member member) {
        Academy academy = member.getAcademy();
        List<Student> studentAll = studentRepository.findStudentsByAcademyId(academy.getId());

        return studentAll.stream()
            .map(StudentResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentInfoResponse getStudentInfo(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));

        List<String> subClassList = studentRepository.findSubClassesByStudentId(studentId);
        return StudentInfoResponse.of(student, subClassList);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findStudentsByName(String studentName) {

        List<Student> students = studentRepository.findAllByName(studentName)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));

        return students.stream()
            .map(StudentResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateStudent(Long studentId, StudentUpdateRequest req) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));

        updateFields(student, req);
        updateSubClasses(student, req.subClassList());
    }

    private void updateFields(Student student, StudentUpdateRequest req) {
        Arrays.stream(StudentUpdateRequest.class.getDeclaredFields())
            .filter(field -> !"subClassList".equals(field.getName()))
            .forEach(field -> setFieldIfChanged(student, req, field));
    }

    private void setFieldIfChanged(Student student, StudentUpdateRequest req, Field field) {
        try {
            field.setAccessible(true);
            Object newValue = field.get(req);

            if (newValue == null) {
                return;
            }

            if ("gender".equals(field.getName()) && newValue instanceof String) {
                newValue = Gender.valueOf(((String) newValue).toUpperCase());
            }

            Field studentField = Student.class.getDeclaredField(field.getName());
            studentField.setAccessible(true);

            if (!newValue.equals(studentField.get(student))) {
                studentField.set(student, newValue);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ClassfitException(ErrorCode.STUDENT_MODIFIED_FAILED);
        }
    }

    private void updateSubClasses(Student student, List<Long> subClassList) {
        if (subClassList == null || subClassList.isEmpty()) {
            return;
        }

        classStudentRepository.deleteAllByStudentId(student.getId());

        subClassList.forEach(subClassId -> {
            SubClass subClass = getSubClass(subClassId);
            ClassStudent classStudent = ClassStudent.create(student, subClass);
            classStudentRepository.save(classStudent);
        });
    }

    private SubClass getSubClass(Long subClassId) {
        return subClassRepository.findById(subClassId).orElseThrow(
            () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));
    }
}