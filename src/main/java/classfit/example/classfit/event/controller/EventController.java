package classfit.example.classfit.event.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.request.EventDragUpdate;
import classfit.example.classfit.event.dto.request.EventModalRequest;
import classfit.example.classfit.event.dto.response.EventModalResponse;
import classfit.example.classfit.event.dto.response.EventMonthlyResponse;
import classfit.example.classfit.event.dto.response.EventResponse;
import classfit.example.classfit.event.service.EventService;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.response.AcademyMemberResponse;
import classfit.example.classfit.member.service.MemberService;
import classfit.example.classfit.memberCalendar.domain.CalendarType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "일정관리 컨트롤러", description = "일정관리 관련 API입니다.")
public class EventController {
    private final EventService eventService;
    private final MemberService memberService;

    @PostMapping("/event")
    @Operation(summary = "캘린더 일정 등록", description = "캘린더 일정 등록하는 api 입니다.")
    public CustomApiResponse<EventResponse> createEvent(
        @AuthMember Member member,
        @RequestBody EventCreateRequest request
    ) {
        EventResponse createdEvent = eventService.createEvent(member, request);
        return CustomApiResponse.success(createdEvent, 200, "CREATED");
    }

    @GetMapping("/academy-members")
    @Operation(summary = "참석자 필드 조회", description = "일정 등록 시 참석자 필드에 들어갈 리스트를 조회하는 api 입니다.")
    public CustomApiResponse<List<AcademyMemberResponse>> getAcademyMembers(@AuthMember Member loggedInMember) {
        List<AcademyMemberResponse> members = memberService.getMembersByLoggedInMemberAcademy(loggedInMember);
        return CustomApiResponse.success(members, 200, "SUCCESS");
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 일정 조회", description = "월별 일정들을 조회하는 api 입니다.")
    public CustomApiResponse<List<EventMonthlyResponse>> getMonthlyEvents(
        @AuthMember Member member,
        @RequestParam CalendarType calendarType,
        @RequestParam int year,
        @RequestParam int month
    ) {
        List<EventMonthlyResponse> events = eventService.getMonthlyEventsByCalendarType(calendarType, year, month, member);
        return CustomApiResponse.success(events, 200, "SUCCESS");
    }

    @PostMapping("/modal")
    @Operation(summary = "모달 일정 등록", description = "모달 일정을 등록하는 api 입니다.")
    public CustomApiResponse<EventResponse> createModalEvent(
        @AuthMember Member member,
        @RequestBody EventModalRequest request
    ) {
        EventResponse createdModalEvent = eventService.createModalEvent(member, request);
        return CustomApiResponse.success(createdModalEvent, 200, "CREATED");
    }

    @GetMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 상세 조회", description = "모달 일정을 상세조회하는 api 입니다.")
    public CustomApiResponse<EventModalResponse> getEventDetails(
        @PathVariable Long eventId
    ) {
        EventModalResponse event = eventService.getEvent(eventId);
        return CustomApiResponse.success(event, 200, "SUCCESS");
    }

    @PatchMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 수정", description = "모달 일정을 수정하는 api 입니다.")
    public CustomApiResponse<EventResponse> updateEventDetails(
        @AuthMember Member member,
        @PathVariable Long eventId,
        @RequestBody EventModalRequest request
    ) {
        EventResponse updatedEvent = eventService.updateEvent(member, eventId, request);
        return CustomApiResponse.success(updatedEvent, 200, "UPDATED");
    }

    @DeleteMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 삭제", description = "모달 일정을 삭제하는 api 입니다.")
    public CustomApiResponse<EventResponse> deleteEvent(
        @PathVariable Long eventId
    ) {
        eventService.deleteEvent(eventId);
        return CustomApiResponse.success(null, 204, "DELETED");
    }

    @PatchMapping("/drag/{eventId}")
    @Operation(summary = "드래그 앤 드롭 일정 날짜 수정", description = "드래그 앤 드롭으로 일정 날짜 수정하는 api 입니다.")
    public CustomApiResponse<EventResponse> dragUpdateEventDate(
        @PathVariable Long eventId,
        @RequestBody EventDragUpdate eventDragUpdate
    ) {
        EventResponse updatedEvent = eventService.dragUpdateEvent(eventId, eventDragUpdate);
        return CustomApiResponse.success(updatedEvent, 200, "UPDATED");
    }
}
