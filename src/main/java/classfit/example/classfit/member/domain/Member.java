package classfit.example.classfit.member.domain;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.member.dto.request.MemberUpdateInfoRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 15)
    private String role;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private MemberStatus status;

    @Column(length = 20)
    private LocalDate birthDate;

    @Column(length = 20)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    public void addAcademy(Academy academy) {
        this.academy = academy;
    }

    public void updateRole(String admin) {
        this.role = admin;
    }

    public void updateInfo(MemberUpdateInfoRequest request) {
        this.birthDate = request.birth();
        this.subject = request.subject();
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
