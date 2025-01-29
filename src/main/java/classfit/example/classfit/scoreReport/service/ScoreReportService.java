package classfit.example.classfit.scoreReport.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
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
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.scoreReport.domain.ScoreReportRepository;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.*;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.StudentList;
import classfit.example.classfit.studentExam.domain.*;
import classfit.example.classfit.studentExam.dto.process.ExamHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreReportService {

    private final ScoreReportRepository scoreReportRepository;
    private final MainClassRepository mainClassRepository;
    private final SubClassRepository subClassRepository;
    private final ExamRepository examRepository;
    private final ClassStudentRepository classStudentRepository;
    private final StudentExamScoreRepository studentExamScoreRepository;
    private final AcademyRepository academyRepository;

    @Transactional(readOnly = true)
    public List<ScoreReport> findByStudentId(Long studentId) {
        return scoreReportRepository.findByStudentId(studentId);
    }

    @Transactional
    public CreateReportResponse createReport(@AuthMember Member member,
                                             CreateReportRequest request) {
        MainClass mainClass = mainClassRepository.findById(request.mainClassId())
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        SubClass subClass = subClassRepository.findById(request.subClassId())
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));
        validateAcademy(member, member.getAcademy().getId());

        List<Exam> exams = examRepository.findAllById(request.examIdList());
        if (exams.isEmpty()) {
            throw new ClassfitException(ErrorCode.EXAM_NOT_FOUND);
        }

        List<ClassStudent> studentsInSubClass = classStudentRepository.findAllBySubClassId(
                subClass.getId());
        if (studentsInSubClass.isEmpty()) {
            throw new ClassfitException(ErrorCode.STUDENT_NOT_FOUND);
        }

        List<StudentList> allStudents = new ArrayList<>();

        for (ClassStudent classStudent : studentsInSubClass) {
            Student student = classStudent.getStudent();

            ScoreReport report = request.toEntity(subClass, mainClass, student, member);
            scoreReportRepository.save(report);

            allStudents.add(new StudentList(report.getId(), student.getId(), student.getName()));

            for (Long examId : request.examIdList()) {
                StudentExamScore studentExamScore = studentExamScoreRepository.findByStudentAndExamId(student, examId)
                    .orElseThrow(() -> new ClassfitException(ErrorCode.SCORE_NOT_FOUND));
                studentExamScore.updateScoreReport(report);
                studentExamScoreRepository.save(studentExamScore);
            }
        }

        return CreateReportResponse.of(
                allStudents,
                mainClass.getId(),
                subClass.getId(),
                request.reportName(),
                request.startDate(),
                request.endDate(),
                member,
                request.includeAverage()
        );
    }


    @Transactional(readOnly = true)
    public List<ReportExam> showReportExam(@AuthMember Member member, LocalDate startDate,
                                           LocalDate endDate, Long mainClassId,
                                           Long subClassId) {
        validateAcademy(member, member.getAcademy().getId());
        List<ReportExam> reports = scoreReportRepository.findExamsByCreatedAtBetween(startDate,
            endDate, mainClassId, subClassId, member.getAcademy().getId());
        return reports.stream()
            .map(report -> new ReportExam(
                report.examId(),
                report.examPeriod(),
                report.mainClassName(),
                report.subClassName(),
                report.examName(),
                report.createAt()
            ))
            .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<FindReportResponse> findReport(@AuthMember Member member, Long mainClassId,
                                               Long subClassId, String memberName) {
        MainClass mainClass = mainClassRepository.findById(mainClassId)
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        SubClass subClass = subClassRepository.findById(subClassId)
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));
        validateAcademy(member, mainClass.getAcademy().getId());
        List<ScoreReport> studentReports = scoreReportRepository.findAllReportsByMainClassAndSubClass(
            mainClassId, subClassId, member.getAcademy().getId());

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

    @Transactional(readOnly = true)
    public List<FindReportResponse> findAllReport(@AuthMember Member member) {
        Long academyId = member.getAcademy().getId();

        Academy academy = academyRepository.findById(academyId)
            .orElseThrow(() -> new ClassfitException(ErrorCode.ACADEMY_NOT_FOUND));

        List<ScoreReport> scoreReports = scoreReportRepository.findAllByAcademy(academy);

        return scoreReports.stream()
            .map(report -> new FindReportResponse(
                report.getId(),
                report.getStudent().getId(),
                report.getStudent().getName(),
                report.getReportName(),
                report.getReportCreatedBy(),
                report.getCreatedAt().toLocalDate()
            ))
            .collect(Collectors.toList());
    }


    @Transactional
    public void deleteReport(@AuthMember Member member, Long studentReportId) {
        validateAcademy(member, member.getAcademy().getId());
        studentExamScoreRepository.deleteByReportId(studentReportId);
        scoreReportRepository.deleteById(studentReportId);
    }

    @Transactional(readOnly = true)
    public List<FindClassStudent> findClassStudents(@AuthMember Member member, Long mainClassId,
                                                    Long subClassId) {

        MainClass mainClass = mainClassRepository.findById(mainClassId)
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        SubClass subClass = subClassRepository.findById(subClassId)
            .orElseThrow(
                () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));
        validateAcademy(member, member.getAcademy().getId());

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
                    () -> new ClassfitException(ErrorCode.REPORT_NOT_FOUND));
            validateAcademy(member, scoreReport.getMainClass().getAcademy().getId());

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
            .orElseThrow(() -> new ClassfitException(ErrorCode.REPORT_NOT_FOUND));

        validateAcademy(member, scoreReport.getMainClass().getAcademy().getId());

        List<AttendanceInfo> attendanceInfoList = scoreReport.getStudent().getClassStudents().stream()
            .flatMap(classStudent -> classStudent.getAttendances().stream())
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

                if (exam.getStandard() == Standard.PF) {
                    long pCount = studentExamScoreRepository.countByExamAndScore(exam, -3);
                    long fCount = studentExamScoreRepository.countByExamAndScore(exam, -4);

                    exam.updateAverage(pCount > fCount ? 100 : 0);

                    // PF 일때
                    return new ExamHistory(
                        exam.getId(),
                        exam.getExamName(),
                        exam.getStandard(),
                        String.format("%.1f", exam.getAverage()),
                        studentExamScore.getScore()
                    );
                }
                if (exam.getStandard() == Standard.QUESTION) {

                    double maxScore = exam.getHighestScore();

                    double convertedScore =
                        (double) studentExamScore.getScore() / maxScore * 100.0;

                    double convertedAverage = exam.getAverage() / maxScore * 100.0;

                    return new ExamHistory(
                        exam.getId(),
                        exam.getExamName(),
                        exam.getStandard(),
                        String.format("%.1f", convertedAverage),
                        (int) convertedScore
                    );
                }

                // score 일때
                return new ExamHistory(
                    exam.getId(),
                    exam.getExamName(),
                    exam.getStandard(),
                    String.format("%.1f", exam.getAverage()),
                    studentExamScore.getScore()
                );
            })
            .toList();

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
            scoreReport.getIncludeAverage(),
            examHistoryList,
            scoreReport.getOverallOpinion(),
            scoreReport.getStudentOpinion()
        );
    }


    private void validateAcademy(Member member, Long academyId) {
        Academy academy = academyRepository.findById(academyId)
            .orElseThrow(() -> new ClassfitException(ErrorCode.ACADEMY_NOT_FOUND));
        if (!Objects.equals(member.getAcademy().getId(), academyId)) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
        if (!Objects.equals(academy.getId(), academyId)) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
    }
}
