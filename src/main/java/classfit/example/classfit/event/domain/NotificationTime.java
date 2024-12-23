package classfit.example.classfit.event.domain;

import lombok.Getter;

@Getter
public enum NotificationTime {
    ON_TIME("정시"),
    TEN_MINUTES_BEFORE("10분 전"),
    THIRTY_MINUTES_BEFORE("30분 전"),
    ONE_HOUR_BEFORE("1시간 전"),
    TWO_HOURS_BEFORE("2시간 전"),
    ONE_DAY_BEFORE("1일 전");

    private final String description;

    NotificationTime(String description) {
        this.description = description;
    }
}
