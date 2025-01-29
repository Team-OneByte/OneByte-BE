package classfit.example.classfit.student.domain;

import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private Gender gender;

    @Column(nullable = false, length = 10)
    private LocalDate birth;

    @Column(nullable = false, length = 14)
    private String studentNumber;

    @Column(nullable = false, length = 14)
    private String parentNumber;

    @Column(nullable = false, length = 20)
    private String grade;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false)
    private boolean isStudent;

    private String remark;

    private boolean receivedSms;

    private String counselingLog;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentExamScore> studentExamScores = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassStudent> classStudents = new ArrayList<>();
}
