package classfit.example.classfit.scoreReport.service;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.attendance.dto.process.AttendanceInfo;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.scoreReport.domain.ScoreReportRepository;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.scoreReport.dto.response.FindClassStudent;
import classfit.example.classfit.scoreReport.dto.response.FindReportResponse;
import classfit.example.classfit.scoreReport.dto.response.SentStudentOpinionResponse;
import classfit.example.classfit.scoreReport.dto.response.ShowStudentReportResponse;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.StudentList;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
import classfit.example.classfit.studentExam.dto.process.ExamHistory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScoreReportService {

    private final ScoreReportRepository scoreReportRepository;
    private final MainClassRepository mainClassRepository;
    private final SubClassRepository subClassRepository;
    private final ExamRepository examRepository;
    private final ClassStudentRepository classStudentRepository;
    private final StudentExamScoreRepository studentExamScoreRepository;

    @Transactional
    public CreateReportResponse createReport(@AuthMember Member member,
            CreateReportRequest request) {
        MainClass mainClass = mainClassRepository.findById(request.mainClassId())
                .orElseThrow(
                        () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass subClass = subClassRepository.findById(request.subClassId())
                .orElseThrow(
                        () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        List<Exam> exams = examRepository.findAllById(request.examIdList());
        if (exams.isEmpty()) {
            throw new ClassfitException("시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND);
        }

        List<ClassStudent> studentsInSubClass = classStudentRepository.findAllBySubClassId(
                subClass.getId());
        if (studentsInSubClass.isEmpty()) {
            throw new ClassfitException("해당 클래스에 학생이 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<StudentList> allStudents = new ArrayList<>();

        for (ClassStudent classStudent : studentsInSubClass) {
            Student student = classStudent.getStudent();
            allStudents.add(new StudentList(student.getId(), student.getName()));

            ScoreReport report = ScoreReport.builder()
                    .mainClass(mainClass)
                    .subClass(subClass)
                    .student(student)
                    .reportName(request.reportName())
                    .overallOpinion(request.overallOpinion())
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .build();
            scoreReportRepository.save(report);

            for (Long examId : request.examIdList()) {
                StudentExamScore studentExamScore = studentExamScoreRepository.findByStudentAndExamId(
                                student, examId)
                        .orElseThrow(() -> new ClassfitException("시험 점수를 찾을 수 없어요.",
                                HttpStatus.NOT_FOUND));

                studentExamScore.updateScoreReport(report);
                studentExamScoreRepository.save(studentExamScore);

            }
        }

        return CreateReportResponse.builder()
                .mainClassId(mainClass.getId())
                .subClassId(subClass.getId())
                .studentList(allStudents)
                .reportName(request.reportName())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .examIdList(exams.stream().map(Exam::getId).toList())
                .overallOpinion(request.overallOpinion())
                .build();
    }


    @Transactional(readOnly = true)
    public List<ReportExam> showReportExam(LocalDate startDate, LocalDate endDate, Long mainClassId,
            Long subClassId) {
        List<ReportExam> reports = scoreReportRepository.findExamsByCreatedAtBetween(startDate,
                endDate, mainClassId, subClassId);
        return reports.stream()
                .map(report -> new ReportExam(
                        report.examId(),
                        report.examPeriod(),
                        report.mainClassName(),
                        report.subClassName(),
                        report.examName()
                ))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<FindReportResponse> findReport(@AuthMember Member member, Long mainClassId,
            Long subClassId, String memberName) {
        MainClass mainClass = mainClassRepository.findById(mainClassId)
                .orElseThrow(
                        () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass subClass = subClassRepository.findById(subClassId)
                .orElseThrow(
                        () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        List<ScoreReport> studentReports = scoreReportRepository.findFirstReportByStudent(
                mainClassId, subClassId);

        return studentReports.stream()
                .map(report -> new FindReportResponse(
                        report.getId(),
                        report.getStudent().getId(),
                        report.getStudent().getName(),
                        report.getReportName(),
                        member.getName(),
                        report.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReport(@AuthMember Member member, Long studentReportId) {
        scoreReportRepository.deleteById(studentReportId);
    }

    @Transactional(readOnly = true)
    public List<FindClassStudent> findClassStudents(@AuthMember Member member, Long mainClassId,
            Long subClassId) {
        MainClass mainClass = mainClassRepository.findById(mainClassId)
                .orElseThrow(
                        () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass subClass = subClassRepository.findById(subClassId)
                .orElseThrow(
                        () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        List<FindClassStudent> classStudents = classStudentRepository.findStudentIdsByMainClassIdAndSubClassId(
                mainClassId, subClassId);
        return classStudents.stream()
                .map(classStudent -> new FindClassStudent(
                        classStudent.studentId(), classStudent.studentName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SentStudentOpinionResponse> sentStudentOpinion(@AuthMember Member member,
            List<SentStudentOpinionRequest> requests) {
        List<SentStudentOpinionResponse> responses = new ArrayList<>();

        for (SentStudentOpinionRequest request : requests) {
            ScoreReport scoreReport = scoreReportRepository.findById(request.reportId())
                    .orElseThrow(
                            () -> new ClassfitException("학습리포트를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

            scoreReport.updateStudentOpinion(request.studentOpinion());

            SentStudentOpinionResponse response = new SentStudentOpinionResponse(
                    scoreReport.getId(),
                    scoreReport.getStudent().getId(),
                    scoreReport.getStudent().getName(),
                    scoreReport.getStudentOpinion()
            );
            responses.add(response);
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public ShowStudentReportResponse showStudentReport(@AuthMember Member member, Long reportId) {
        ScoreReport scoreReport = scoreReportRepository.findById(reportId)
                .orElseThrow(
                        () -> new ClassfitException("학습리포트를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        List<AttendanceInfo> attendanceInfoList = scoreReport.getStudent().getAttendances().stream()
                .collect(Collectors.groupingBy(
                        Attendance::getStatus,
                        Collectors.summingInt(attendance -> 1)
                ))
                .entrySet()
                .stream()
                .map(entry -> new AttendanceInfo(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
        Integer totalAttendanceCount = attendanceInfoList.stream()
                .mapToInt(AttendanceInfo::attendanceCount)
                .sum();
        List<StudentExamScore> studentExamScores = studentExamScoreRepository.findByScoreReport(
                scoreReport);

        List<ExamHistory> examHistoryList = studentExamScores.stream()
                .filter(studentExamScore -> studentExamScore.getScoreReport().getId()
                        .equals(scoreReport.getId()))
                .map(studentExamScore -> {
                    Exam exam = studentExamScore.getExam();
                    return new ExamHistory(
                            exam.getId(),
                            exam.getExamName(),
                            exam.getAverage(),
                            studentExamScore.getScore()
                    );
                })
                .collect(Collectors.toList());

        return new ShowStudentReportResponse(
                scoreReport.getStudent().getId(),
                scoreReport.getStudent().getName(),
                scoreReport.getMainClass().getMainClassName(),
                scoreReport.getSubClass().getSubClassName(),
                scoreReport.getReportName(),
                scoreReport.getStartDate(),
                scoreReport.getEndDate(),
                attendanceInfoList,
                totalAttendanceCount,
                examHistoryList,
                scoreReport.getOverallOpinion(),
                scoreReport.getStudentOpinion()
        );

    }

}
