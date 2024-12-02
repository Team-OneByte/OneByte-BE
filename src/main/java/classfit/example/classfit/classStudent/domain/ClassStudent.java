package classfit.example.classfit.classStudent.domain;

import classfit.example.classfit.attendance.domain.Attendance;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.student.domain.Student;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setSubClass(SubClass subClass) {
        if (this.subClass != null) {
            this.subClass.getClassStudents().remove(this);
        }

        this.subClass = subClass;

        if (!subClass.getClassStudents().contains(this)) {
            subClass.getClassStudents().add(this);
        }
    }
}
