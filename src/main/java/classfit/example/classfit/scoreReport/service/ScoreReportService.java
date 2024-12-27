package classfit.example.classfit.scoreReport.service;

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
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.dto.StudentList;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
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
            ScoreReport report = request.toEntity(subClass, mainClass, student);
            scoreReportRepository.save(report);
            allStudents.add(new StudentList(student.getId(), student.getName()));
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
    public List<ReportExam> showReportExam(LocalDate startDate, LocalDate endDate) {
        List<ReportExam> reports = scoreReportRepository.findExamsByCreatedAtBetween(startDate,
                endDate);
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
}
