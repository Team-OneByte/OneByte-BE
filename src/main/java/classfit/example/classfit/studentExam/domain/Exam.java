package classfit.example.classfit.studentExam.domain;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "exam")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Exam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_class_id", nullable = false)
    private SubClass subClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_class_id", nullable = false)
    private MainClass mainClass;

    @Column(name = "exam_name")
    private String examName;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "standard", nullable = false)
    private Standard standard;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_period", nullable = false)
    private ExamPeriod examPeriod;

    @Column(name = "highest_score")
    private Integer highestScore; // 시험 생성 시 만점 점수

    @Column(name = "exam_range")
    private String examRange;

    @Column(name = "perfect_score")
    private Integer perfectScore;

    @Column(name = "low_score")
    private Integer lowestScore;

    @Column(name = "average")
    private Long average;


    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentExamScore> studentExamScores = new ArrayList<>();

    @Builder
    public Exam(SubClass subClass, MainClass mainClass, String examName, LocalDate examDate,
            Standard standard, ExamPeriod examPeriod, Integer highestScore, String examRange) {
        this.subClass = subClass;
        this.mainClass = mainClass;
        this.examName = examName;
        this.examDate = examDate;
        this.standard = standard;
        this.examPeriod = examPeriod;
        this.highestScore = highestScore;
        this.examRange = examRange;
    }

    public void updateExam(LocalDate examDate, Standard standard, Integer highestScore,
            ExamPeriod examPeriod, String examName, List<String> examRange) {
        this.examDate = examDate;
        this.standard = standard;
        this.highestScore = highestScore;
        this.examPeriod = examPeriod;
        this.examName = examName;
        this.examRange = String.join(",", examRange);
    }

    public void updateScores(Integer lowestScore, Integer perfectScore, Long average) {
        this.lowestScore = lowestScore;
        this.perfectScore = perfectScore;
        this.average = average;
    }
    public void updateHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }
}
