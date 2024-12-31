package classfit.example.classfit.event.domain;

import static classfit.example.classfit.event.domain.EventType.SCHEDULE;

import classfit.example.classfit.calendarCategory.domain.CalendarCategory;
import classfit.example.classfit.common.domain.BaseEntity;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Optional;
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
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CalendarCategory category;

    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private boolean isAllDay;
    private boolean isRepeating;

    @Enumerated(EnumType.STRING)
    private NotificationTime notificationTime;

    @Column(length = 50)
    private String location;

    @Column(length = 100)
    private String memo;

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

    public void update(
        String name,
        CalendarCategory category,
        EventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isAllDay
    ) {
        this.name = name;
        this.category = category;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAllDay = isAllDay;
    }
}
