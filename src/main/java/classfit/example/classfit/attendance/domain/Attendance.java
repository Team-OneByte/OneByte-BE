package classfit.example.classfit.attendance.domain;

import classfit.example.classfit.student.domain.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static classfit.example.classfit.common.exception.ClassfitException.INVALID_STATUS_TYPE;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public Attendance(LocalDate date, int week, AttendanceStatus status, Student student) {
        this.date = date;
        this.week = week;
        this.status = status;
        this.student = student;
    }

    public void updateStatus(String status) {
        try {
            this.status = AttendanceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_STATUS_TYPE);
        }
    }
}