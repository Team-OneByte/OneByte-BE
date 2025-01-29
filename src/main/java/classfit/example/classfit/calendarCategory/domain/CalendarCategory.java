package classfit.example.classfit.calendarCategory.domain;

import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_category_id")
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryColor color;

    @ManyToOne
    @JoinColumn(name = "member_calendar_id")
    private MemberCalendar memberCalendar;

    public void updateNameAndColor(String newName, String newColor) {
        this.name = newName;
        this.color = CategoryColor.valueOf(newColor);
    }
}