package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.dto.response.FileInfo;
import classfit.example.classfit.drive.service.DriveDownloadService;
import classfit.example.classfit.drive.service.DriveGetService;
import classfit.example.classfit.drive.service.DriveUploadService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ApiResponse<List<FileInfo>> getFiles(
        @AuthMember Member member,
        @RequestParam DriveType driveType
    ) {
        List<FileInfo> fileUrls = driveGetService.getFilesFromS3(member, driveType);
        return ApiResponse.success(fileUrls, 200, "SUCCESS");
    }

    @PostMapping("/files")
    @Operation(summary = "다중 파일 업로드", description = "다중 파일 업로드 API 입니다.")
    public ApiResponse<List<String>> uploadFiles(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam("multipartFiles") List<MultipartFile> multipartFiles
    ) throws IOException {
        List<String> fileUrls = driveUploadService.uploadFiles(member, driveType, multipartFiles);
        return ApiResponse.success(fileUrls, 200, "SUCCESS");
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
        @RequestParam("fileName") String fileName
    ) throws UnsupportedEncodingException {
        InputStreamResource resource = driveDownloadService.downloadFile(fileName);
        String fileExtension = driveDownloadService.getFileExtension(fileName);
        String contentType = driveDownloadService.getContentType(fileExtension);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
