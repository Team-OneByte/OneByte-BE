package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.common.domain.BaseEntity;
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
public class StudentExamScore extends BaseEntity {

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

    @Column(name = "checked_student")
    boolean checkedStudent;
    @Builder
    public StudentExamScore(Student student, Exam exam, Integer score, ScoreReport scoreReport,String evaluationDetail) {

        this.student = student;
        this.exam = exam;
        this.score = score;
        this.scoreReport = scoreReport;
        this.evaluationDetail =evaluationDetail;
    }

    public void updateScore(Integer score) {
        this.score = score;
    }
    public  void updateEvaluationDetail(String evaluationDetail) {
        this.evaluationDetail = evaluationDetail;
    }
    public void updateCheckedStudent(boolean checkedStudent) {
        this.checkedStudent = checkedStudent;
    }


    public void updateScoreReport(ScoreReport scoreReport) {
        this.scoreReport = scoreReport;
    }
}
