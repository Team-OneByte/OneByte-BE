package classfit.example.classfit.academy.domain;

import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.inviteMember.domain.InviteMember;
import classfit.example.classfit.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Academy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "academy_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 8)
    private String code;

    @Builder.Default
    @OneToMany(mappedBy = "academy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "academy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InviteMember> inviteMembers = new ArrayList<>();

    public void addAcademy(Member member) {
        this.members.add(member);
        if (member.getAcademy() != this) {
            member.joinAcademy(this);
        }
    }
}
