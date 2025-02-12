package classfit.example.classfit.student.domain;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.course.domain.SubClass;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_class_id", nullable = false)
    private SubClass subClass;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();
    
    public static Enrollment create(Student student, SubClass subClass) {
        return Enrollment.builder()
            .student(student)
            .subClass(subClass)
            .build();
    }
}
