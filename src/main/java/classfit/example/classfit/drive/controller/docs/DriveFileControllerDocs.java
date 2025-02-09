package classfit.example.classfit.drive.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DrivePreSignedResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URL;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "드라이브 컨트롤러", description = "드라이브 관련 API입니다.")
public interface DriveFileControllerDocs {

    @Operation(summary = "사전 서명된 URL 발급 요청", description = "사전 서명된 URL 발급 요청하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200")
    })
    CustomApiResponse<List<DrivePreSignedResponse>> getPreSignedUrl(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.") @RequestParam(required = false, defaultValue = "") String folderPath,
            @Parameter(description = "업로드할 파일 이름입니다.") @RequestParam List<String> fileName
    );

    @Operation(summary = "파일 업로드 확인", description = "파일 업로드 확인하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200")
    })
    CustomApiResponse<Void> upLoadConfirm(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.") @RequestParam(required = false, defaultValue = "") String folderPath,
            @Parameter(description = "UUID까지 포함된 파일명입니다.") @RequestParam List<String> fileName
    );

    @Operation(summary = "파일 다운로드", description = "다중 파일을 압축하여 다운로드하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200")
    })
    ResponseEntity<InputStreamResource> downloadMultipleFiles(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "다운로드할 파일 이름 목록입니다.") @RequestParam List<String> fileNames
    );
}
