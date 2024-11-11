package classfit.example.classfit.category.controller;

import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.service.SubClassService;
import classfit.example.classfit.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class SubClassController {

    private final SubClassService subClassService;

    @PostMapping("/sub-category")
    public ApiResponse<SubClassResponse> addSubClass(@RequestHeader("member-no") Long memberId,
            @RequestBody SubClassRequest req) {
        return subClassService.addSubClass(memberId, req);
    }

    @PatchMapping("/sub-category/{subClassId}")
    public ApiResponse<SubClassResponse> updateSubClass(
            @RequestHeader(name = "member-no") Long memberId,
            @PathVariable(name = "subClassId") Long subClassId,
            @RequestBody SubClassRequest req) {
        return subClassService.updateSubClass(memberId, subClassId, req);
    }
}
