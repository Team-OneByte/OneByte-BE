package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.scoreReport.domain.ScoreReport;
import classfit.example.classfit.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamScore extends BaseEntity {

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

    @Column(name = "score",nullable = true)
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ScoreReport scoreReport;

    @Column(name = "evaluation_detail")
    private String evaluationDetail;

    @Column(name = "checked_student")
    boolean checkedStudent;

    @Column(name = "standrd_status")
    @Enumerated(EnumType.STRING)
    private StandardStatus standardStatus;


    public void updateScore(Integer score) {
        this.score = score;
    }

    public void updateEvaluationDetail(String evaluationDetail) {
        this.evaluationDetail = evaluationDetail;
    }

    public void updateCheckedStudent(boolean checkedStudent) {
        this.checkedStudent = checkedStudent;
    }


    public void updateScoreReport(ScoreReport scoreReport) {
        this.scoreReport = scoreReport;
    }

    public void updateStandardStatus(StandardStatus standardStatus) {
        this.standardStatus = standardStatus;
    }

        public static ExamScore toEntity(Student student, Exam exam, StandardStatus standardStatus, boolean checkedStudent) {
            return ExamScore.builder()
                    .student(student)
                    .exam(exam)
                    .score(builder().score)
                    .standardStatus(standardStatus)
                    .checkedStudent(checkedStudent)
                    .build();
        }



    }
