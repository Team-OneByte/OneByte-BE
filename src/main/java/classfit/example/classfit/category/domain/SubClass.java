package classfit.example.classfit.category.domain;

import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubClass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_class_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String subClassName;

    @ManyToOne
    @JoinColumn(name = "main_class_id", nullable = false)
    private MainClass mainClass;


    @OneToMany(mappedBy = "subClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClassStudent> classStudents = new ArrayList<>();

    public SubClass(String subClassName, MainClass mainClass) {
        this.subClassName = subClassName;
        this.mainClass = mainClass;
    }

    // 업데이트 관련 메서드
    public void updateSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }
}
