package classfit.example.classfit.event.domain;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.event.dto.response.EventModalResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.eventMember.domain.EventMember;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.memberCalendar.domain.MemberCalendar;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CalendarCategory category;

    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private boolean isAllDay;

    @Enumerated(EnumType.STRING)
    private EventRepeatType eventRepeatType;

    private LocalDateTime repeatEndDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventMember> attendances = new ArrayList<>();

    @Column(length = 50)
    private String location;

    @Column(length = 100)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_calendar_id")
    private MemberCalendar memberCalendar;

    public static LocalDateTime getEndDate(
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate) {
        LocalDateTime resultEndDate = startDate;

        if (eventType.equals(EventType.SCHEDULE)) {
            resultEndDate = endDate;
        }
        return resultEndDate;
    }

    public void setDates(boolean isAllDay, LocalDateTime startDate, LocalDateTime endDate) {
        this.isAllDay = isAllDay;
        if (isAllDay) {
            this.startDate = startDate.toLocalDate().atStartOfDay();
            this.endDate = startDate.toLocalDate().atTime(23, 59, 59);
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public void addAttendee(Member member) {
        EventMember eventMember = EventMember.builder()
            .event(this)
            .member(member)
            .build();

        this.attendances.add(eventMember);
    }

    public void update(
        String name,
        CalendarCategory category,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isAllDay,
        MemberCalendar memberCalendar
    ) {
        this.name = name;
        this.category = category;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAllDay = isAllDay;
        this.memberCalendar = memberCalendar;
    }

    public void dragUpdate(
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static EventResponse buildEventResponse(Event event) {
        return EventResponse.of(
            event.getId(),
            event.getName(),
            event.getEventType(),
            event.getStartDate(),
            event.getEndDate()
        );
    }

    public static EventModalResponse buildModalEventResponse(Event event) {
        Long categoryId = (event.getCategory() != null) ? event.getCategory().getId() : null;

        return EventModalResponse.of(
            event.getId(),
            event.getName(),
            event.getMemberCalendar().getType(),
            event.getEventType(),
            event.getStartDate(),
            event.getEndDate(),
            categoryId,
            event.getEventRepeatType(),
            event.getRepeatEndDate(),
            event.isAllDay()
        );
    }
}
