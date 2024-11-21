package classfit.example.classfit.category.domain;

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
public class MainClass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "main_class_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String mainClassName;

    @OneToMany(mappedBy = "mainClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubClass> subClasses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    public MainClass(String mainClassName, Member member) {
        this.mainClassName = mainClassName;
        this.member = member;
    }

    // 업데이트 관련 메서드
    public void updateMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }
}
