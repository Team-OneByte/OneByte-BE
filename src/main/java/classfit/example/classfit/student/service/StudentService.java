package classfit.example.classfit.student.service;

import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.class_student.repository.ClassStudentRepository;
import classfit.example.classfit.domain.ClassStudent;
import classfit.example.classfit.domain.Student;
import classfit.example.classfit.domain.SubClass;
import classfit.example.classfit.exception.ClassfitException;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.repository.StudentRepository;
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

        for (Long subClassId : req.subClassList()) {
            SubClass subClass = subClassRepository.findById(subClassId).orElseThrow(
                () -> new ClassfitException("존재하지 않는 SubClass.", HttpStatus.NOT_FOUND));

            ClassStudent classStudent = new ClassStudent();
            classStudent.setStudent(student);
            classStudent.setSubClass(subClass);
            classStudentRepository.save(classStudent);
        }

        return StudentResponse.from(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
            .orElseThrow(
                () -> new ClassfitException("해당하는 학생 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        classStudentRepository.deleteByStudentId(studentId); // student_id에 해당하는 모든 class_student 삭제
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
}
