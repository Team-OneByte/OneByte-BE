package classfit.example.classfit.drive.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Tag(name = "드라이브 컨트롤러", description = "드라이브 관련 API입니다.")
public interface DriveGetControllerDocs {

    @Operation(summary = "파일 목록 조회", description = "S3에 업로드된 파일들을 조회하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "파일 조회 성공")
    })
    CustomApiResponse<List<FileResponse>> getFiles(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.") @RequestParam(required = false, defaultValue = "") String folderPath
    );

    @Operation(summary = "파일명으로 검색", description = "파일명을 기준으로 파일을 검색하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "파일 이름 검색 성공")
    })
    CustomApiResponse<List<FileResponse>> searchFilesByName(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
        @Parameter(description = "검색할 파일명입니다. 빈 값이면 모든 파일이 조회됩니다.") @RequestParam(required = false, defaultValue = "") String fileName,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더를 검색합니다.") @RequestParam(required = false, defaultValue = "") String folderPath
    );

    @Operation(summary = "확장자 필터링", description = "파일 확장자로 필터링하여 파일을 조회하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "확장자 필터링 성공")
    })
    CustomApiResponse<List<FileResponse>> filterFilesByExtension(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
        @Parameter(description = "파일 유형 필터링입니다. 빈 값이면 모든 확장자가 조회됩니다.") @RequestParam FileType fileType,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더를 검색합니다.") @RequestParam(required = false, defaultValue = "") String folderPath
    );
}
