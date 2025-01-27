package classfit.example.classfit.classStudent.domain;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_student_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_class_id", nullable = false)
    private SubClass subClass;

    @OneToMany(mappedBy = "classStudent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    public void addStudent(Student student) {
        this.student = student;
    }

    public void addSubClass(SubClass subClass) {
        if (this.subClass != null) {
            this.subClass.getClassStudents().remove(this);
        }

        this.subClass = subClass;

        if (!subClass.getClassStudents().contains(this)) {
            subClass.getClassStudents().add(this);
        }
    }
}
