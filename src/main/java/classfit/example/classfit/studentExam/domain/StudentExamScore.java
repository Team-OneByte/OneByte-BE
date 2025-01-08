package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "student_exam_score")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentExamScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "score", nullable = false)
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ScoreReport scoreReport;

    @Column(name = "evaluation_detail")
    private String evaluationDetail;


    @Builder
    public StudentExamScore(Student student, Exam exam, Integer score, ScoreReport scoreReport,String evaluationDetail) {

        this.student = student;
        this.exam = exam;
        this.score = score;
        this.scoreReport = scoreReport;
        this.evaluationDetail =evaluationDetail;
    }

    public void updateScore(Integer score) {
        if (score != -3 && score != -4 && score != -5) {
            throw new ClassfitException("점수는 -3, -4, -5만 허용됩니다. 그 외의 값은 유효하지 않습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        this.score = score;
    }
    public  void updateEvaluationDetail(String evaluationDetail) {
        this.evaluationDetail = evaluationDetail;
    }

    public void updateScoreReport(ScoreReport scoreReport) {
        this.scoreReport = scoreReport;
    }
}
