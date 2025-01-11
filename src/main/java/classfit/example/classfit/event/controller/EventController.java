package classfit.example.classfit.event.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.response.EventMontylyResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.service.EventService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.response.AcademyMemberResponse;
import classfit.example.classfit.member.service.MemberService;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "일정관리 컨트롤러", description = "일정관리 관련 API입니다.")
public class EventController {
    private final EventService eventService;
    private final MemberService memberService;

    @PostMapping("/event")
    @Operation(summary = "캘린더 일정 등록", description = "캘린더 일정 등록하는 api 입니다.")
    public ApiResponse<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        EventResponse createdEvent = eventService.createEvent(request);
        return ApiResponse.success(createdEvent, 200, "CREATED");
    }

    @GetMapping("/academy-members")
    @Operation(summary = "참석자 필드 조회", description = "일정 등록 시 참석자 필드에 들어갈 리스트를 조회하는 api 입니다.")
    public ApiResponse<List<AcademyMemberResponse>> getAcademyMembers(@AuthMember Member loggedInMember) {
        List<AcademyMemberResponse> members = memberService.getMembersByLoggedInMemberAcademy(loggedInMember);
        return ApiResponse.success(members, 200, "SUCCESS");
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 일정 조회", description = "월별 일정들을 조회하는 api 입니다.")
    public ApiResponse<List<EventMontylyResponse>> getMonthlyEvents(
        @RequestParam CalendarType calendarType,
        @RequestParam int year,
        @RequestParam int month
    ) {
        List<EventMontylyResponse> events = eventService.getMonthlyEventsByCalendarType(calendarType, year, month);
        return ApiResponse.success(events, 200, "SUCCESS");
    }

    @PostMapping("/modal")
    @Operation(summary = "모달 일정 등록", description = "모달 일정을 등록하는 api 입니다.")
    public ApiResponse<EventResponse> createModalEvent(@RequestBody EventModalRequest request) {
        EventResponse createdModalEvent = eventService.createModalEvent(request);
        return ApiResponse.success(createdModalEvent, 200, "CREATED");
    }

    @GetMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 상세 조회", description = "모달 일정을 상세조회하는 api 입니다.")
    public ApiResponse<EventResponse> getEventDetails(
        @PathVariable Long eventId
    ) {
        EventResponse event = eventService.getEvent(eventId);
        return ApiResponse.success(event, 200, "SUCCESS");
    }

    @PatchMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 수정", description = "모달 일정을 수정하는 api 입니다.")
    public ApiResponse<EventResponse> updateEventDetails(
        @PathVariable Long eventId,
        @RequestBody EventModalRequest request
    ) {
        EventResponse updatedEvent = eventService.updateEvent(eventId, request);
        return ApiResponse.success(updatedEvent, 200, "UPDATED");
    }

    @DeleteMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 삭제", description = "모달 일정을 삭제하는 api 입니다.")
    public ApiResponse<EventResponse> deleteEvent(
        @PathVariable Long eventId
    ) {
        eventService.deleteEvent(eventId);
        return ApiResponse.success(null, 204, "DELETED");
    }
}
