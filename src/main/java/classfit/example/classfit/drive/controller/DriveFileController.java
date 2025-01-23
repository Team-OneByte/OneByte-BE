package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.service.DriveDownloadService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
@Tag(name = "드라이브 컨트롤러", description = "드라이브 관련 API입니다.")
public class DriveFileController {
    private final DriveUploadService driveUploadService;
    private final DriveDownloadService driveDownloadService;

    @PostMapping("/files")
    @Operation(summary = "다중 파일 업로드", description = "다중 파일 업로드 API 입니다.")
    public CustomApiResponse<List<String>> uploadFiles(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @RequestParam("multipartFiles") List<MultipartFile> multipartFiles,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<String> fileUrls = driveUploadService.uploadFiles(member, driveType, multipartFiles, folderPath);
        return CustomApiResponse.success(fileUrls, 200, "SUCCESS");
    }

    @GetMapping("/download")
    @Operation(summary = "파일 다운로드", description = "다중 파일을 압축하여 다운로드하는 API 입니다.")
    public ResponseEntity<InputStreamResource> downloadMultipleFiles(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @RequestParam List<String> fileNames
    ) {
        InputStreamResource resource = driveDownloadService.downloadMultipleFiles(member, driveType, fileNames);
        String zipFileName = "files.zip";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
