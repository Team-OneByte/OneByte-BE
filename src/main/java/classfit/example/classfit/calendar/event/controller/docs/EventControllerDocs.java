package classfit.example.classfit.calendar.event.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.calendar.event.dto.request.EventCreateRequest;
import classfit.example.classfit.calendar.event.dto.request.EventDragUpdate;
import classfit.example.classfit.calendar.event.dto.request.EventModalRequest;
import classfit.example.classfit.calendar.event.dto.response.EventModalResponse;
import classfit.example.classfit.calendar.event.dto.response.EventMonthlyResponse;
import classfit.example.classfit.calendar.event.dto.response.EventResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.response.AcademyMemberResponse;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "일정관리 컨트롤러", description = "일정관리 관련 API입니다.")
public interface EventControllerDocs {

    @Operation(summary = "캘린더 일정 등록", description = "캘린더 일정 등록하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "캘린더 일정 등록 성공")
    })
    CustomApiResponse<EventResponse> createEvent(
            @AuthMember Member member,
            @RequestBody EventCreateRequest request
    );

    @Operation(summary = "일정 등록 시 학원생 필드 조회", description = "일정 등록 시 학원생 필드를 조회하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학원생 필드 조회 성공")
    })
    CustomApiResponse<List<AcademyMemberResponse>> getAcademyMembers(@AuthMember Member member);

    @Operation(summary = "월별 일정 조회", description = "월별 일정들을 조회하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "월별 일정 조회 성공")
    })
    CustomApiResponse<List<EventMonthlyResponse>> getMonthlyEvents(
            @AuthMember Member member,
            @RequestParam CalendarType calendarType,
            @RequestParam int year,
            @RequestParam int month
    );

    @Operation(summary = "모달 일정 등록", description = "모달 일정을 등록하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "모달 일정 등록 성공")
    })
    CustomApiResponse<EventResponse> createModalEvent(
            @AuthMember Member member,
            @RequestBody EventModalRequest request
    );

    @Operation(summary = "모달 일정 상세 조회", description = "모달 일정을 상세조회하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "모달 일정 상세 조회 성공")
    })
    CustomApiResponse<EventModalResponse> getEventDetails(@PathVariable Long eventId);

    @Operation(summary = "모달 일정 수정", description = "모달 일정을 수정하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "모달 일정 수정 성공")
    })
    CustomApiResponse<EventResponse> updateEventDetails(
            @AuthMember Member member,
            @PathVariable Long eventId,
            @RequestBody EventModalRequest request
    );

    @Operation(summary = "모달 일정 삭제", description = "모달 일정을 삭제하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "204", description = "모달 일정 삭제 성공")
    })
    CustomApiResponse<Void> deleteEvent(@PathVariable Long eventId);

    @Operation(summary = "드래그 앤 드롭 일정 날짜 수정", description = "드래그 앤 드롭으로 일정 날짜 수정하는 api 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "드래그 앤 드롭 일정 날짜 수정 성공")
    })
    CustomApiResponse<EventResponse> dragUpdateEventDate(
            @PathVariable Long eventId,
            @RequestBody EventDragUpdate eventDragUpdate
    );
}
