package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.dto.request.StudentAttendanceUpdateRequest;
import classfit.example.classfit.attendance.dto.response.StudentAttendanceResponse;
import classfit.example.classfit.attendance.service.AttendanceService;
import classfit.example.classfit.attendance.service.AttendanceUpdateService;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "출결 관리 컨트롤러", description = "출결 관련 API입니다.")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceUpdateService attendanceUpdateService;

    @GetMapping("/")
    @Operation(summary = "전체 출결 관리 조회", description = "전체 학생을 클릭 시 조회되는 출결 api 입니다.")
    public ApiResponse<List<StudentAttendanceResponse>> getAttendance(
        @AuthMember Member member,
        @Parameter(description = "이전 또는 이후 출결 조회 시 필요한 값으로, 현재는 0(디폴트), 이전 주는 음수로, 다음 주는 양수로 표시합니다. 4주 전부터 2주 후까지(-4 ~ +2) 조회 가능합니다.")
        @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @Parameter(description = "조회할 페이지 번호로, 기본값은 0이며, 페이지는 0부터 시작합니다.")
        @RequestParam(value = "page", defaultValue = "0") int page) {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange(weekOffset);
        Page<Student> students = attendanceService.getAllStudents(page, member);
        List<StudentAttendanceResponse> studentAttendances = attendanceService.getStudentAttendance(students.getContent(), weekRange);
        return ApiResponse.success(studentAttendances, 200, "SUCCESS");
    }

    @GetMapping("/{mainClassId}/{subClassId}")
    @Operation(summary = "특정 클래스 출결 관리 조회", description = "특정 클래스를 클릭 시 조회되는 출결 api 입니다.")
    public ApiResponse<List<StudentAttendanceResponse>> getClassAttendance(
        @AuthMember Member member,
        @Parameter(description = "이전 또는 이후 출결 조회 시 필요한 값으로, 현재는 0(디폴트), 이전 주는 음수로, 다음 주는 양수로 표시합니다. 4주 전부터 2주 후까지(-4 ~ +2) 조회 가능합니다.")
        @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
        @Parameter(description = "조회할 페이지 번호로, 기본값은 0이며, 페이지는 0부터 시작합니다.")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @PathVariable("mainClassId") Long mainClassId,
        @PathVariable("subClassId") Long subClassId) {
        List<LocalDate> weekRange = attendanceService.getWeeklyAttendanceRange(weekOffset);
        Page<ClassStudent> students = attendanceService.getClassStudentsByMainClassAndSubClass(mainClassId, subClassId, page, member);
        List<StudentAttendanceResponse> studentAttendances = attendanceService.getStudentAttendance(students.getContent(), weekRange);
        return ApiResponse.success(studentAttendances, 200, "SUCCESS");
    }

    @PatchMapping("/")
    @Operation(summary = "클래스 출결 수정", description = "학생 출결을 수정할 때 사용되는 api 입니다.")
    public ApiResponse<List<StudentAttendanceResponse>> updateAttendance(
        @AuthMember Member member,
        @RequestBody List<StudentAttendanceUpdateRequest> requestDTO) {
        List<StudentAttendanceResponse> updatedStudents = attendanceUpdateService.updateStudentAttendances(requestDTO, member);
        return ApiResponse.success(updatedStudents, 200, "UPDATED");
    }
}
