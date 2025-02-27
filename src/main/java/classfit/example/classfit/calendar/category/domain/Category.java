package classfit.example.classfit.calendar.category.domain;

import classfit.example.classfit.calendar.category.domain.enumType.CategoryColor;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
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