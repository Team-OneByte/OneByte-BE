package classfit.example.classfit.studentExam.repository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.QExam;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ExamRepositoryImpl implements ExamRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public ExamRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Exam> findExamsByConditions(Long academyId, Long mainClassId, Long subClassId, String memberName, String examName) {
        QExam exam = QExam.exam;
        BooleanBuilder builder = new BooleanBuilder();

        if (academyId != null) {
            builder.and(exam.mainClass.academy.id.eq(academyId));
        }
        if (mainClassId != null) {
            builder.and(exam.mainClass.id.eq(mainClassId));
        }
        if (subClassId != null) {
            builder.and(exam.subClass.id.eq(subClassId));
        }
        if (memberName != null && !memberName.isEmpty()) {
            builder.and(exam.mainClass.academy.members.any().name.containsIgnoreCase(memberName));
        }
        if (examName != null && !examName.isEmpty()) {
            builder.and(exam.examName.containsIgnoreCase(examName));
        }

        return queryFactory
                .selectFrom(exam)
                .where(builder)
                .fetch();
    }
}
