package classfit.example.classfit.event.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.event.controller.docs.EventControllerDocs;
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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class EventController implements EventControllerDocs {
    private final EventService eventService;
    private final MemberService memberService;

    @PostMapping("/event")
    public CustomApiResponse<EventResponse> createEvent(
        @AuthMember Member member,
        @RequestBody EventCreateRequest request
    ) {
        EventResponse createdEvent = eventService.createEvent(member, request);
        return CustomApiResponse.success(createdEvent, 200, "CREATED");
    }

    @GetMapping("/academy-members")
    public CustomApiResponse<List<AcademyMemberResponse>> getAcademyMembers(@AuthMember Member member) {
        List<AcademyMemberResponse> members = memberService.getMembersByLoggedInMemberAcademy(member);
        return CustomApiResponse.success(members, 200, "SUCCESS");
    }

    @GetMapping("/monthly")
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
    public CustomApiResponse<EventResponse> createModalEvent(
        @AuthMember Member member,
        @RequestBody EventModalRequest request
    ) {
        EventResponse createdModalEvent = eventService.createModalEvent(member, request);
        return CustomApiResponse.success(createdModalEvent, 200, "CREATED");
    }

    @GetMapping("/modal/{eventId}")
    public CustomApiResponse<EventModalResponse> getEventDetails(@PathVariable Long eventId) {
        EventModalResponse event = eventService.getEvent(eventId);
        return CustomApiResponse.success(event, 200, "SUCCESS");
    }

    @PatchMapping("/modal/{eventId}")
    public CustomApiResponse<EventResponse> updateEventDetails(
        @AuthMember Member member,
        @PathVariable Long eventId,
        @RequestBody EventModalRequest request
    ) {
        EventResponse updatedEvent = eventService.updateEvent(member, eventId, request);
        return CustomApiResponse.success(updatedEvent, 200, "UPDATED");
    }

    @DeleteMapping("/modal/{eventId}")
    public CustomApiResponse<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return CustomApiResponse.success(null, 204, "DELETED");
    }

    @PatchMapping("/drag/{eventId}")
    public CustomApiResponse<EventResponse> dragUpdateEventDate(
        @PathVariable Long eventId,
        @RequestBody EventDragUpdate eventDragUpdate
    ) {
        EventResponse updatedEvent = eventService.dragUpdateEvent(eventId, eventDragUpdate);
        return CustomApiResponse.success(updatedEvent, 200, "UPDATED");
    }
}
