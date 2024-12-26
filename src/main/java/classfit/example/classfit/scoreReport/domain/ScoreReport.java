package classfit.example.classfit.scoreReport.domain;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.studentExam.domain.Exam;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CascadeType;

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

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_class_id", nullable = false)
    private SubClass subClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_class_id", nullable = false)
    private MainClass mainClass;

    @OneToMany(orphanRemoval = true)
    @Column(name = "exam_list")
    private List<Exam> examList;

    @Column(name = "overall_opinion")
    private String overallOpinion;

    // 개별의견 관련 보류 - 전체학생을 다보여줌 ?
    //TODO 리포트 생성시 해당 반의 학생들의 개별 리포트까지 모두 생성되야함

    @Builder
    public ScoreReport(SubClass subClass,MainClass mainClass,String reportName,LocalDate startDate,LocalDate endDate,List<Exam> examList,String overallOpinion) {
        this.subClass = subClass;
        this.mainClass = mainClass;
        this.reportName = reportName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.examList = examList;
        this.overallOpinion = overallOpinion;
    }
}
