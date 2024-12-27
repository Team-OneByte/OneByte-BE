package classfit.example.classfit.event.controller;

import classfit.example.classfit.event.dto.request.EventModalCreateRequest;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.event.dto.request.EventCreateRequest;
import classfit.example.classfit.event.dto.response.EventCreateResponse;
import classfit.example.classfit.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/event")
    @Operation(summary = "캘린더 일정 등록", description = "캘린더 일정 등록하는 api 입니다.")
    public ApiResponse<EventCreateResponse> createEvent(@RequestBody EventCreateRequest request) {
        EventCreateResponse createdEvent = eventService.createEvent(request);
        return ApiResponse.success(createdEvent, 200, "CREATED");
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 일정 조회", description = "월별 일정들을 조회하는 api 입니다.")
    public ApiResponse<List<EventCreateResponse>> getMonthlyEvents(
        @RequestParam long categoryId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        List<EventCreateResponse> events = eventService.getMonthlyEventsByCategory(categoryId, year, month);
        return ApiResponse.success(events, 200, "SUCCESS");
    }

    @PostMapping("/modal")
    @Operation(summary = "모달 일정 등록", description = "모달 일정을 등록하는 api 입니다.")
    public ApiResponse<EventCreateResponse> createModalEvent(@RequestBody EventModalCreateRequest request) {
        EventCreateResponse createdModalEvent = eventService.createModalEvent(request);
        return ApiResponse.success(createdModalEvent, 200, "CREATED");
    }

    @GetMapping("/modal/{eventId}")
    @Operation(summary = "모달 일정 상세 조회", description = "모달 일정을 상세조회하는 api 입니다.")
    public ApiResponse<EventCreateResponse> getMonthlyEvent(
        @PathVariable Long eventId
    ) {
        EventCreateResponse event = eventService.getMonthlyEvent(eventId);
        return ApiResponse.success(event, 200, "SUCCESS");
    }
}
