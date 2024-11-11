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
import java.util.List;
import lombok.Getter;

@Entity
@Getter
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
    private List<ClassStudent> classStudents;


    public void setMember(Member member) {
        this.member = member;
    }

    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }

    public void setMainClass(MainClass mainClass) {
        this.mainClass = mainClass;
    }
}
