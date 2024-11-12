package classfit.example.classfit.student.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentControllerImpl {

    private final StudentService studentService;

    @PostMapping("/")
    public ApiResponse<StudentResponse> registerStudent(@RequestBody @Valid StudentRequest req
    ) {
        StudentResponse studentResponse = studentService.registerStudent(req);
        return ApiResponse.success(studentResponse, 201, "CREATED STUDENT");
    }

    @GetMapping("/")
    public ApiResponse<List<StudentResponse>> getStudentList() {
        List<StudentResponse> studentList = studentService.getStudentList();
        return ApiResponse.success(studentList, 200, "FIND STUDENTS");
    }

    @DeleteMapping("/{studentId}")
    public ApiResponse<StudentResponse> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ApiResponse.success(null, 200, "DELETED STUDENT");
    }
}
