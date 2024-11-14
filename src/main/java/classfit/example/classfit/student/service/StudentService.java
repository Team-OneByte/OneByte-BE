package classfit.example.classfit.student.service;

import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.domain.SubClass;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubClassRepository subClassRepository;
    private final ClassStudentRepository classStudentRepository;

    @Transactional
    public StudentResponse registerStudent(StudentRequest req) {

        Student student = req.toEntity(true);
        studentRepository.save(student);

        req.subClassList().forEach(subClassId -> {
            SubClass subClass = subClassRepository.findById(subClassId).orElseThrow(
                () -> new ClassfitException("존재하지 않는 SubClass ID입니다.", HttpStatus.NOT_FOUND));
            ClassStudent classStudent = new ClassStudent();
            classStudent.setStudent(student);
            classStudent.setSubClass(subClass);
            classStudentRepository.save(classStudent);
        });

        return StudentResponse.from(student);
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
    public List<StudentResponse> studentInfoAll() {
        List<Student> studentAll = studentRepository.findAll();

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

        updateStudentFields(student, req);
        updateStudentSubClasses(student, req.subClassList());
    }

    private void updateStudentFields(Student student, StudentUpdateRequest req) {
        try {
            Field[] fields = StudentUpdateRequest.class.getDeclaredFields();

            for (Field field : fields) {

                if (field.getName().equals("subClassList")) {
                    continue;
                }

                field.setAccessible(true);
                Object newValue = field.get(req);

                if ("isStudent".equals(field.getName()) && newValue == null) {
                    newValue = true;
                }

                if (newValue == null) {
                    continue;
                }

                Field studentField = Student.class.getDeclaredField(field.getName());
                studentField.setAccessible(true);

                if (!newValue.equals(studentField.get(student))) {
                    studentField.set(student, newValue);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ClassfitException("학생의 정보 수정에 실패했습니다.", HttpStatus.NOT_MODIFIED);
        }
    }

    private void updateStudentSubClasses(Student student, List<Long> subClassList) {
        if (subClassList != null && !subClassList.isEmpty()) {
            classStudentRepository.deleteAllByStudentId(student.getId());

            subClassList.forEach(subClassId -> {
                SubClass subClass = subClassRepository.findById(subClassId).orElseThrow(
                    () -> new ClassfitException("존재하지 않는 SubClass 입니다.", HttpStatus.NOT_FOUND));

                ClassStudent classStudent = new ClassStudent();
                classStudent.setStudent(student);
                classStudent.setSubClass(subClass);
                classStudentRepository.save(classStudent);
            });
        }
    }
}
