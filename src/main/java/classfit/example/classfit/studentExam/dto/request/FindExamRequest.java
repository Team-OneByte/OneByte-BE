package classfit.example.classfit.studentExam.dto.request;

// 강사명 / 시험명으로 검색 -> null일땐 전체 조회
public record FindExamRequest(String memberName, String examName) {

}
