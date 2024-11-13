package classfit.example.classfit.student.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "학생 API", description = "학생 관련 API")
public class StudentControllerImpl {

    private final StudentService studentService;

    @PostMapping("/")
    @Operation(summary = "학생 정보 등록", description = "학생 정보 등록하는 API 입니다.")
    public ApiResponse<StudentResponse> registerStudent(@RequestBody @Valid StudentRequest req) {

        StudentResponse studentResponse = studentService.registerStudent(req);
        return ApiResponse.success(studentResponse, 201, "CREATED STUDENT");
    }

    @GetMapping("/")
    @Operation(summary = "학생 정보 조회", description = "전체 학생 정보 조회하는 API 입니다. ")
    public ApiResponse<List<StudentResponse>> studentInfoAll() {

        List<StudentResponse> studentList = studentService.studentInfoAll();
        return ApiResponse.success(studentList, 200, "FIND STUDENTS");
    }

    @DeleteMapping("/")
    @Operation(summary = "학생 정보 삭제", description = "학생 정보 삭제하는 API 입니다. ")
    public ApiResponse<List<Long>> deleteStudent(@RequestParam List<Long> studentIds) {

        studentService.deleteStudent(studentIds);
        return ApiResponse.success(studentIds.stream().toList(), 200, "DELETED STUDENT");
    }

    @PatchMapping("/{studentId}")
    @Operation(summary = "학생 정보 수정", description = "학생 정보를 수정하는 API 입니다. ")
    public ApiResponse<Long> updateStudent(
        @PathVariable Long studentId, @RequestBody @Valid StudentUpdateRequest req) {

        studentService.updateStudent(studentId, req);
        return ApiResponse.success(studentId, 200, "UPDATED STUDENT");
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "개인 학생 정보 조회", description = "특정 학생 정보를 조회하는 API 입니다. ")
    public ApiResponse<StudentInfoResponse> studentInfo(@PathVariable Long studentId) {

        StudentInfoResponse studentInfo = studentService.getStudentInfo(studentId);
        return ApiResponse.success(studentInfo, 200, studentInfo.name() + "의 정보");
    }

    @GetMapping("/search")
    @Operation(summary = "학생 이름 검색", description = "특정한 학생 이름으로 목록 조회하는 API 입니다.")
    public ApiResponse<StudentResponse> findStudentByName(
        @RequestParam(value = "name") String studentName) {

        StudentResponse findStudentByName = studentService.findStudentByName(studentName);
        return ApiResponse.success(findStudentByName, 200, findStudentByName.name());
    }
}
