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
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Gender;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public StudentResponse registerStudent(StudentRequest request) {
        Student student = request.toEntity(true);
        studentRepository.save(student);

        request.subClassList().forEach(subClassId -> {
            SubClass subClass = subClassRepository.findById(subClassId).orElseThrow(
                () -> new ClassfitException("존재하지 않는 SubClass ID입니다.", HttpStatus.NOT_FOUND));
            ClassStudent classStudent = new ClassStudent();
            classStudent.addStudent(student);
            classStudent.addSubClass(subClass);
            classStudentRepository.save(classStudent);

            createAttendanceForThreeWeeks(student);
        });

        return StudentResponse.from(student);
    }

    private void createAttendanceForThreeWeeks(Student student) {
        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate.with(DayOfWeek.MONDAY);
        List<ClassStudent> classStudents = classStudentRepository.findByStudent(student);

        classStudents.forEach(classStudent -> {
            for (int i = 0; i < 3; i++) {
                LocalDate weekDate = weekStart.plusWeeks(i);
                for (int j = 0; j < 7; j++) {
                    LocalDate attendanceDate = weekDate.plusDays(j);
                    Attendance attendance = Attendance.builder()
                        .date(attendanceDate)
                        .week(j)
                        .status(AttendanceStatus.PRESENT)
                        .student(student)
                        .classStudent(classStudent)
                        .build();
                    attendanceRepository.save(attendance);
                }
            }
        });
    }

    @Transactional
    public void deleteStudent(List<Long> studentIds) {
        studentIds.stream()
            .map(studentId -> studentRepository.findById(studentId).orElseThrow(()
                -> new ClassfitException("해당하는 학생 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)))
            .forEach(student -> {
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
            () -> new ClassfitException("해당하는 학생 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        List<String> subClassList = studentRepository.findSubClassesByStudentId(studentId);
        return StudentInfoResponse.of(student, subClassList);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findStudentsByName(String studentName) {

        List<Student> students = studentRepository.findAllByName(studentName)
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new ClassfitException("해당 학생은 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return students.stream()
            .map(StudentResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateStudent(Long studentId, StudentUpdateRequest req) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException("해당하는 학생 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

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
            throw new ClassfitException("학생의 정보 수정에 실패했습니다.", HttpStatus.NOT_MODIFIED);
        }
    }

    private void updateSubClasses(Student student, List<Long> subClassList) {
        if (subClassList == null || subClassList.isEmpty()) {
            return;
        }

        classStudentRepository.deleteAllByStudentId(student.getId());

        subClassList.forEach(subClassId -> {
            SubClass subClass = subClassRepository.findById(subClassId).orElseThrow(
                () -> new ClassfitException("존재하지 않는 SubClass 입니다.", HttpStatus.NOT_FOUND));

            ClassStudent classStudent = new ClassStudent();
            classStudent.addStudent(student);
            classStudent.addSubClass(subClass);
            classStudentRepository.save(classStudent);
        });
    }
}