package classfit.example.classfit.drive.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "drive_type")
public abstract class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drive_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String objectName;

    @Column(length = 50)
    private String objectPath;

    @Column(nullable = false)
    private String objectUrl;

    @Column(nullable = false, length = 100)
    private String objectSize;

    @Column(nullable = false, length = 100)
    private String objectType;

    @Column(nullable = false, length = 100)
    private String uploadedBy;
}
