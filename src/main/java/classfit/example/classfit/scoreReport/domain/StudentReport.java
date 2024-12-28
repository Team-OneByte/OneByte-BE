package classfit.example.classfit.scoreReport.domain;

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
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO 성적 리포트 생성시 학생 개별 리포트에 선택한 시험 히스토리 담기도록
//TODO 출석 , 지난 시험의 성적 통계비교 떠야함
@Entity
@Table(name = "student_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudentReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ScoreReport scoreReport;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_report_id")
    @Column(name = "exam_id_list")
    private List<StudentExamScore> examIdList;

    @Builder
    public StudentReport(ScoreReport scoreReport, Student student,List<StudentExamScore> examIdList) {
        this.scoreReport = scoreReport;
        this.student = student;
        this.examIdList = examIdList;
    }

}
