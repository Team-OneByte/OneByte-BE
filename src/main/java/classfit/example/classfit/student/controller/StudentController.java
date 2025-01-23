package classfit.example.classfit.student.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import classfit.example.classfit.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "학생 컨트롤러", description = "학생 관련 API")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/")
    @Operation(summary = "학생 정보 등록", description = "학생 정보 등록하는 API 입니다.")
    public CustomApiResponse<StudentResponse> registerStudent(@RequestBody @Valid StudentRequest req) {

        StudentResponse studentResponse = studentService.registerStudent(req);
        return CustomApiResponse.success(studentResponse, 201, "CREATED STUDENT");
    }

    @GetMapping("/")
    @Operation(summary = "학생 정보 조회", description = "전체 학생 정보 조회하는 API 입니다. ")
    public CustomApiResponse<List<StudentResponse>> studentInfoAll(@AuthMember Member member) {

        List<StudentResponse> studentList = studentService.studentInfoAll(member);
        return CustomApiResponse.success(studentList, 200, "FIND STUDENTS");
    }

    @DeleteMapping("/")
    @Operation(summary = "학생 정보 삭제", description = "학생 정보 삭제하는 API 입니다. ")
    public CustomApiResponse<List<Long>> deleteStudent(@AuthMember Member member, @RequestParam List<Long> studentIds) {

        studentService.deleteStudent(member, studentIds);
        return CustomApiResponse.success(studentIds.stream().toList(), 200, "DELETED STUDENT");
    }

    @PatchMapping("/{studentId}")
    @Operation(summary = "학생 정보 수정", description = "학생 정보를 수정하는 API 입니다. ")
    public CustomApiResponse<Long> updateStudent(
        @PathVariable Long studentId, @RequestBody @Valid StudentUpdateRequest req) {

        studentService.updateStudent(studentId, req);
        return CustomApiResponse.success(studentId, 200, "UPDATED STUDENT");
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "개인 학생 정보 조회", description = "특정 학생 정보를 조회하는 API 입니다. ")
    public CustomApiResponse<StudentInfoResponse> studentInfo(@PathVariable Long studentId) {

        StudentInfoResponse studentInfo = studentService.getStudentInfo(studentId);
        return CustomApiResponse.success(studentInfo, 200, studentInfo.name() + "의 정보");
    }

    @GetMapping("/search")
    @Operation(summary = "학생 이름 검색", description = "특정 학생 이름으로 목록 조회하는 API 입니다.")
    public CustomApiResponse<List<StudentResponse>> findStudentByName(
        @RequestParam(value = "name") String studentName) {

        List<StudentResponse> findStudents = studentService.findStudentsByName(studentName);
        return CustomApiResponse.success(findStudents, 200, "FIND STUDENTS");
    }
}
