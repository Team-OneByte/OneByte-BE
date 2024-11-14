package classfit.example.classfit.domain;

import classfit.example.classfit.common.BaseEntity;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


    // 업데이트 관련 메서드
    public void updateMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public MainClass(String mainClassName, Member member) {
        this.mainClassName = mainClassName;
        this.member = member;
    }
}
