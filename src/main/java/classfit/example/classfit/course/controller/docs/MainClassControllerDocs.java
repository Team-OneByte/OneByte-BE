package classfit.example.classfit.course.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.course.dto.request.MainClassRequest;
import classfit.example.classfit.course.dto.response.AllMainClassResponse;
import classfit.example.classfit.course.dto.response.MainClassResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "메인 클래스 컨트롤러", description = "메인 클래스 관련 API입니다.")
public interface MainClassControllerDocs {

    @Operation(summary = "메인 클래스 추가", description = "메인 클래스를 추가하는 API입니다.", responses = {
            @ApiResponse(responseCode = "201", description = "메인 클래스 생성 성공")
    })
    CustomApiResponse<MainClassResponse> createMainClass(
            @AuthMember Member findMember,
            @Valid @RequestBody MainClassRequest req
    );

    @Operation(summary = "메인 클래스 조회", description = "메인 클래스를 조회할 수 있는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "메인 클래스 조회 성공")
    })
    CustomApiResponse<List<AllMainClassResponse>> showMainClass(@AuthMember Member findMember);

    @Operation(summary = "메인 클래스 삭제", description = "메인 클래스를 삭제하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "메인 클래스 삭제 성공")
    })
    CustomApiResponse<Void> deleteMainClass(
            @AuthMember Member findMember,
            @PathVariable(name = "mainClassId") Long mainClassId
    );

    @Operation(summary = "메인 클래스 수정", description = "메인 클래스를 수정하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "메인 클래스 수정 성공")
    })
    CustomApiResponse<MainClassResponse> updateMainClass(
            @AuthMember Member findMember,
            @PathVariable(name = "mainClassId") Long mainClassId,
            @Valid @RequestBody MainClassRequest request
    );
}