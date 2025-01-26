package classfit.example.classfit.attendance.domain;

import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    private LocalDate date;

    private int week;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "class_student_id")
    private ClassStudent classStudent;

    @Builder
    public Attendance(LocalDate date, int week, AttendanceStatus status, ClassStudent classStudent) {
        this.date = date;
        this.week = week;
        this.status = status;
        this.classStudent = classStudent;
    }

    public void updateStatus(String status) {
        try {
            this.status = AttendanceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ClassfitException(ErrorCode.INVALID_STATUS_TYPE);
        }
    }
}