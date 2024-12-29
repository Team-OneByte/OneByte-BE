package classfit.example.classfit.scoreReport.domain;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "score_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScoreReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "report_name")
    private String reportName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_class_id", nullable = false)
    private SubClass subClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_class_id", nullable = false)
    private MainClass mainClass;

    @Column(name = "overall_opinion")
    private String overallOpinion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student",nullable = false)
    private Student student;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @Column(name = "exam_id_list")
    private List<StudentExamScore> examIdList;

    @Builder
    public ScoreReport(SubClass subClass,MainClass mainClass,String reportName,Student student,String overallOpinion) {
        this.subClass = subClass;
        this.mainClass = mainClass;
        this.reportName = reportName;
        this.student = student;
        this.overallOpinion = overallOpinion;
    }
}
