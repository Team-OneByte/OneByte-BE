package classfit.example.classfit.attendance.controller;

import classfit.example.classfit.attendance.service.AttendanceExportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "출결 엑셀 다운로드 컨트롤러", description = "엑셀 다운로드 관련 API입니다.")
public class AttendanceExportController {
    private final AttendanceExportService attendanceExportService;

    @GetMapping("/excel/download")
    public ResponseEntity<byte[]> exportAttendance(
            @Parameter(description = "다운로드 받을 달 (1~12 사이의 값)")
            @RequestParam("month") int month,
            @Parameter(description = "메인 클래스 아이디 (필수 아님)")
            @RequestParam(required = false) Long mainClassId,
            @Parameter(description = "서브 클래스 아이디 (필수 아님)")
            @RequestParam(required = false) Long subClassId) {
        try {
            return generateExcelFile(mainClassId, subClassId, month);
        } catch (RuntimeException | UnsupportedEncodingException e) {
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<byte[]> generateExcelFile(Long mainClassId, Long subClassId, int month) throws UnsupportedEncodingException {
        byte[] excelFile = attendanceExportService.generateAttendanceExcel(month, subClassId);
        String fileName = generateFileName(mainClassId, subClassId, month);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
    }

    private String generateFileName(Long mainClassId, Long subClassId, int month) throws UnsupportedEncodingException {
        String fileName;

        if (mainClassId != null && subClassId != null) {
            String mainClassName = attendanceExportService.getMainClassName(mainClassId);
            String subClassName = attendanceExportService.getSubClassName(subClassId);
            fileName = "출결_" + month + "월" + "_" + mainClassName + "/" + subClassName + ".xlsx";
        } else {
            fileName = "출결_" + month + "월" + ".xlsx";
        }
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }
}
