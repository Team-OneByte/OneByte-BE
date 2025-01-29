package classfit.example.classfit.academy.controller.docs;

import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "학원 컨트롤러", description = "학원 관련 API입니다.")
public interface AcademyControllerDocs {

    @Operation(summary = "학원 생성", description = "학원 생성 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학원 생성 완료.")
    })
    CustomApiResponse<AcademyResponse> createAcademy(@Valid @RequestBody AcademyCreateRequest request);

    @Operation(summary = "학원 가입", description = "학원 코드로 등록된 학원에 가입하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학원 가입 성공")
    })
    CustomApiResponse<Void> joinAcademy(@Valid @RequestBody AcademyJoinRequest request);
}
