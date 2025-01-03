package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.drive.service.DriveUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final DriveUploadService driveUploadService;

    @PostMapping("/file")
    @Operation(summary = "개별 파일 업로드", description = "개별 파일 업로드 API 입니다.")
    public ApiResponse<String> uploadFile(MultipartFile multipartFile) throws IOException {
        String fileUrl = driveUploadService.uploadFile(multipartFile);
        return ApiResponse.success(fileUrl, 200, "SUCCESS");
    }

    @PostMapping("/files")
    @Operation(summary = "다중 파일 업로드", description = "다중 파일 업로드 API 입니다.")
    public ApiResponse<List<String>> uploadFiles(@RequestParam("multipartFiles") List<MultipartFile> multipartFiles) throws IOException {
        List<String> fileUrls = driveUploadService.uploadFiles(multipartFiles);
        return ApiResponse.success(fileUrls, 200, "SUCCESS");
    }
}
