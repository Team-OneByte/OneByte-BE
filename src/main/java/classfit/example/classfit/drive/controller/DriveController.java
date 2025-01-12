package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.drive.service.DriveDownloadService;
import classfit.example.classfit.drive.service.DriveGetService;
import classfit.example.classfit.drive.service.DriveUploadService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
@Tag(name = "드라이브 컨트롤러", description = "드라이브 관련 API입니다.")
public class DriveController {

    private final DriveGetService driveGetService;
    private final DriveUploadService driveUploadService;
    private final DriveDownloadService driveDownloadService;

    @GetMapping("/files")
    @Operation(summary = "파일 목록 조회", description = "S3에 업로드된 파일들을 조회하는 API입니다.")
    public ApiResponse<List<FileResponse>> getFiles(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<FileResponse> fileUrls = driveGetService.getFilesFromS3(member, driveType, folderPath);
        return ApiResponse.success(fileUrls, 200, "SUCCESS");
    }

    @PostMapping("/files")
    @Operation(summary = "다중 파일 업로드", description = "다중 파일 업로드 API 입니다.")
    public ApiResponse<List<String>> uploadFiles(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @RequestParam("multipartFiles") List<MultipartFile> multipartFiles,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<String> fileUrls = driveUploadService.uploadFiles(member, driveType, multipartFiles, folderPath);
        return ApiResponse.success(fileUrls, 200, "SUCCESS");
    }

    @GetMapping("/download")
    @Operation(summary = "파일 다운로드", description = "파일 다운로드 API 입니다.")
    public ResponseEntity<InputStreamResource> downloadFile(
        @RequestParam("fileName") String fileName
    ) {
        InputStreamResource resource = driveDownloadService.downloadFile(fileName);
        String fileExtension = DriveUtil.getFileExtension(fileName);
        String contentType = FileType.getContentType(fileExtension);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/search")
    @Operation(summary = "파일명으로 검색", description = "파일명을 기준으로 파일을 검색하는 API입니다.")
    public ApiResponse<List<FileResponse>> searchFilesByName(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "검색할 파일명입니다. 빈 값이면 모든 파일이 조회됩니다.")
        @RequestParam(required = false, defaultValue = "") String fileName,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더로 검색됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<FileResponse> files = driveGetService.searchFilesByName(member, driveType, fileName, folderPath);
        return ApiResponse.success(files, 200, "SUCCESS");
    }

    @GetMapping("/filter")
    @Operation(summary = "확장자 필터링", description = "파일 확장자로 필터링하여 파일을 조회하는 API입니다.")
    public ApiResponse<List<FileResponse>> filterFilesByExtension(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "파일 유형 필터입니다. 빈 값이면 모든 확장자가 조회됩니다.")
        @RequestParam FileType fileType,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더로 검색됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath

    ) {
        List<FileResponse> files = driveGetService.classifyFilesByType(member, driveType, fileType, folderPath);
        return ApiResponse.success(files, 200, "확장자 필터링 성공");
    }
}
