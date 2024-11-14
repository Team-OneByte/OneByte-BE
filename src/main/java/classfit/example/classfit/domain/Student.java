package classfit.example.classfit.domain;

import classfit.example.classfit.common.BaseEntity;
import classfit.example.classfit.common.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Column(nullable = false, length = 10)
    private String grade;

    @Column(nullable = false, length = 10)
    private String address;

    @Column(nullable = false)
    private boolean isStudent;

    private String remark;

    private boolean receivedSms;
    
    private String counselingLog;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Attendance> attendances = new ArrayList<>();

}
