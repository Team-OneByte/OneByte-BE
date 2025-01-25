package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.controller.docs.AttendanceControllerDocs;
import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequest;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.service.AttendanceService;
import classfit.example.classfit.attendance.service.AttendanceUpdateService;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class AttendanceController implements AttendanceControllerDocs {
    private final AttendanceService attendanceService;
    private final AttendanceUpdateService attendanceUpdateService;

    @GetMapping("/")
    public CustomApiResponse<List<StudentAttendanceResponse>> getAttendance(
        @AuthMember Member member,
        @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange(weekOffset);
        Page<Student> students = attendanceService.getAllStudents(page, member);
        List<StudentAttendanceResponse> studentAttendances = attendanceService.getStudentAttendance(students.getContent(), weekRange);
        return CustomApiResponse.success(studentAttendances, 200, "전체 학생 출결 조회 성공");
    }

    @GetMapping("/{mainClassId}/{subClassId}")
    public CustomApiResponse<List<StudentAttendanceResponse>> getClassAttendance(
        @AuthMember Member member,
        @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @PathVariable("mainClassId") Long mainClassId,
        @PathVariable("subClassId") Long subClassId
    ) {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange(weekOffset);
        Page<ClassStudent> students = attendanceService.getClassStudentsByMainClassAndSubClass(mainClassId, subClassId, page, member);
        List<StudentAttendanceResponse> studentAttendances = attendanceService.getStudentAttendance(students.getContent(), weekRange);
        return CustomApiResponse.success(studentAttendances, 200, "특정 클래스 출결 조회 성공");
    }

    @PatchMapping("/")
    public CustomApiResponse<List<StudentAttendanceResponse>> updateAttendance(
        @AuthMember Member member,
        @RequestBody List<StudentAttendanceUpdateRequest> requestDTO
    ) {
        List<StudentAttendanceResponse> updatedStudents = attendanceUpdateService.updateStudentAttendances(requestDTO, member);
        return CustomApiResponse.success(updatedStudents, 200, "출결 수정 성공");
    }
}
