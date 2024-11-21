package classfit.example.classfit.category.domain;

import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "subClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClassStudent> classStudents = new ArrayList<>();

    public SubClass(String subClassName, Member member, MainClass mainClass) {
        this.subClassName = subClassName;
        this.member = member;
        this.mainClass = mainClass;
    }

    // 업데이트 관련 메서드
    public void updateSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }
}
