package classfit.example.classfit.inviteMember.domain;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_member_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 8)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private InviteStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id", nullable = false)
    private Academy academy;
}
