package classfit.example.classfit.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 글로벌 에러
    INVALID_PARAMETER("잘못된 파라미터 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_METHOD("잘못된 METHOD 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_CONNECTION_ERROR("레디스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("객체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_ENTITY_TYPE("유효하지 않은 엔터티 타입입니다.", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("잘못된 요청입니다", HttpStatus.BAD_REQUEST),

    // 인증/인가 에러
    INVALID_CREDENTIALS("아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REQUEST_FORMAT("입력 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    COOKIE_NOT_FOUND("쿠키가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND("Refresh 토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("Refresh 토큰이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("Access 토큰이 만료되었습니다.", HttpStatus.REQUEST_TIMEOUT),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID_OR_EXPIRED("Refresh 토큰이 유효하지 않거나 만료되었습니다.", HttpStatus.NOT_FOUND),

    // Redis 관련
    REDIS_DATA_NOT_FOUND("해당 키에 대한 값이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    REDIS_SERVER_ERROR("Redis 서버에서 데이터를 처리하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_DELETE_FAILED("삭제 요청 실패: 키가 존재하지 않거나 삭제되지 않았습니다.", HttpStatus.NOT_FOUND),

    // 멤버/이메일
    MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_MEMBER_ACADEMY("로그인한 멤버는 학원에 소속되어 있지 않습니다.", HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_NOT_FOUND("계정이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_EMAIL_TYPE("지원하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST),
    AUTH_CODE_INVALID_OR_EXPIRED("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_AUTH_CODE("유효하지 않은 코드입니다.", HttpStatus.NOT_FOUND),
    EMAIL_SENDING_FAILED("이메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_FAILED("이메일 검증에 문제가 발생하였습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.CONFLICT),

    // 학원
    ACADEMY_NOT_FOUND("학원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACADEMY_MEMBERS_NOT_FOUND("학원의 멤버들을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_ACADEMY_ACCESS("해당 학원에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    ACADEMY_CODE_ALREADY_EXISTS("이미 등록된 코드가 존재합니다. 다시 시도해 주세요.", HttpStatus.CONFLICT),
    ACADEMY_ALREADY_EXISTS("이미 등록된 학원명이 존재합니다. 다시 시도해 주세요.", HttpStatus.CONFLICT),
    INVALID_INVITATION("학원으로부터 초대받지 않은 계정입니다.", HttpStatus.BAD_REQUEST),

    // 클래스
    MAIN_CLASS_NOT_FOUND("메인 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUB_CLASS_NOT_FOUND("서브 클래스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MAIN_CLASS_ALREADY_EXISTS("동일한 이름의 메인 클래스가 있습니다.", HttpStatus.CONFLICT),
    SUB_CLASS_ALREADY_EXISTS("동일한 이름의 서브 클래스가 있습니다.", HttpStatus.CONFLICT),
    INVALID_MAIN_CLASS_ACCESS("사용자가 속한 학원에 존재하는 메인 클래스가 아닙니다.", HttpStatus.FORBIDDEN),

    // 학생
    STUDENT_NOT_FOUND("학생을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STUDENT_MODIFIED_FAILED("학생의 정보 수정에 실패했습니다.", HttpStatus.NOT_MODIFIED),

    // 출결
    ATTENDANCE_NOT_FOUND("출결 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_STATUS_TYPE("유효하지 않은 출결 타입입니다.", HttpStatus.BAD_REQUEST),
    INVALID_WEEK_VALUE("4주 전부터 2주 후까지 조회가 가능합니다.", HttpStatus.BAD_REQUEST),

    // 시험/성적/리포트
    EXAM_NOT_FOUND("시험지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SCORE_NOT_FOUND("시험 성적을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND("학습 리포트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SCORE_EXCEEDS_HIGHEST("점수는 최고 점수를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
    SEARCH_NOT_AVAILABLE("검색 조건이 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    // 일정 관리
    EVENT_NOT_FOUND("해당 일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("해당 카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 드라이브
    INVALID_DRIVE_TYPE("지원하지 않는 드라이브 타입입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    public int getStatusCode() {
        return httpStatus.value();
    }
}
