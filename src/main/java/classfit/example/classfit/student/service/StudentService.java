package classfit.example.classfit.student.service;

import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.class_student.repository.ClassStudentRepository;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.domain.SubClass;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.UpdateStudentRequest;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
            SubClass subClass = subClassRepository.findById(subClassId)
                .orElseThrow(
                    () -> new ClassfitException("존재하지 않는 SubClass ID입니다.", HttpStatus.NOT_FOUND));
            ClassStudent classStudent = new ClassStudent();
            classStudent.setStudent(student);
            classStudent.setSubClass(subClass);
            classStudentRepository.save(classStudent);
        });

        return StudentResponse.from(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException("해당하는 학생 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        classStudentRepository.deleteAllByStudentId(studentId);
        studentRepository.delete(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentList() {
        List<Student> getStudents = studentRepository.findAll();

        List<StudentResponse> responseList = new ArrayList<>();

        for (Student student : getStudents) {
            StudentResponse studentResponse = StudentResponse.from(student);
            responseList.add(studentResponse);
        }

        return responseList;
    }

    @Transactional
    public void updateStudent(Long studentId, UpdateStudentRequest req) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException("해당하는 학생 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        updateStudentFields(student, req);
        updateStudentSubClasses(student, req.subClassList());
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentInfo(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ClassfitException("해당하는 학생 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return StudentResponse.from(student);
    }

    private void updateStudentFields(Student student, UpdateStudentRequest req) {
        try {
            Field[] fields = UpdateStudentRequest.class.getDeclaredFields();

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
