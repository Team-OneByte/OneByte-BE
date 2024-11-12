package classfit.example.classfit.attendance.service;

import classfit.example.classfit.attendance.repository.AttendanceRepository;
import classfit.example.classfit.category.repository.MainClassRespository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.domain.Attendance;
import classfit.example.classfit.domain.MainClass;
import classfit.example.classfit.domain.SubClass;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static classfit.example.classfit.exception.ClassfitException.MAIN_CLASS_NOT_FOUND;
import static classfit.example.classfit.exception.ClassfitException.SUB_CLASS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AttendanceExportService {
    private final AttendanceRepository attendanceRepository;
    private final MainClassRespository mainClassRespository;
    private final SubClassRepository subClassRepository;

    @Transactional
    public byte[] generateAttendanceExcel(int month, Long subClassId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("출결");

            List<Attendance> attendances = fetchAttendances(month, subClassId);
            createHeaderRow(sheet);
            fillAttendanceData(sheet, attendances);

            return writeAndReturnByteArray(workbook);
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.", e);
        }
    }

    private List<Attendance> fetchAttendances(int month, Long subClassId) {
        try {
            if (subClassId != null) {
                return attendanceRepository.findBySubClassIdAndMonth(subClassId, month);
            } else {
                return attendanceRepository.findByMonth(month);
            }
        } catch (Exception e) {
            throw new RuntimeException("출석 데이터 조회 중 오류가 발생했습니다.", e);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("학생 이름");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("날짜");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("출결 상태");
    }

    private void createDataRow(Sheet sheet, Attendance attendance, int rowNum) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(attendance.getStudent().getName());
        row.createCell(1).setCellValue(attendance.getDate().toString());
        row.createCell(2).setCellValue(attendance.getStatus().name());
    }

    private void fillAttendanceData(Sheet sheet, List<Attendance> attendances) {
        int rowNum = 1;
        for (Attendance attendance : attendances) {
            createDataRow(sheet, attendance, rowNum++);
        }
    }

    private byte[] writeAndReturnByteArray(Workbook workbook) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 작성 중 오류가 발생했습니다.", e);
        }
    }

    public String getMainClassName(Long mainClassId) {
        return mainClassRespository.findById(mainClassId)
                .map(MainClass::getMainClassName)
                .orElse(MAIN_CLASS_NOT_FOUND);
    }

    public String getSubClassName(Long subClassId) {
        return subClassRepository.findById(subClassId)
                .map(SubClass::getSubClassName)
                .orElse(SUB_CLASS_NOT_FOUND);
    }
}
