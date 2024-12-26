package classfit.example.classfit.scoreReport.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.scoreReport.domain.ScoreReportRepository;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import java.util.List;
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

    @Transactional
    public CreateReportResponse createReport(@AuthMember Member member, CreateReportRequest request) {
        MainClass mainClass = mainClassRepository.findById(request.mainClassId())
                .orElseThrow(() -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass subClass = subClassRepository.findById(request.subClassId())
                .orElseThrow(() -> new ClassfitException("서브 클래스를 찾을 수 없어요.",HttpStatus.NOT_FOUND));
        List<Exam> exams = examRepository.findAllById(request.examIdList());

        ScoreReport report = request.toEntity(subClass, mainClass,exams);
        scoreReportRepository.save(report);

        return CreateReportResponse.builder()
                .reportId(report.getId())
                .mainClassId(mainClass.getId())
                .subClassId(subClass.getId())
                .reportName(report.getReportName())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .examIdList(exams.stream().map(Exam::getId).toList())
                .overallOpinion(report.getOverallOpinion())
                .build();
    }
}
