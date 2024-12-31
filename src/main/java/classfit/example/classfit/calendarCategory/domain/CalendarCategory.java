package classfit.example.classfit.calendarCategory.domain;

import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CalendarCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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