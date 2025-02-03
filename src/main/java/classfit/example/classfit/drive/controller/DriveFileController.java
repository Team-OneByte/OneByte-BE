package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveFileControllerDocs;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.service.DriveDownloadService;
import classfit.example.classfit.drive.service.DriveUploadService;
import classfit.example.classfit.member.domain.Member;
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
public class DriveFileController implements DriveFileControllerDocs {

    private final DriveUploadService driveUploadService;
    private final DriveDownloadService driveDownloadService;

    @Override
    @PostMapping("/files")
    public CustomApiResponse<List<String>> uploadFiles(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam("multipartFiles") List<MultipartFile> multipartFiles,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<String> fileUrls = driveUploadService.uploadFiles(member, driveType, multipartFiles, folderPath);
        return CustomApiResponse.success(fileUrls, 200, "다중 파일 업로드 성공");
    }

    @Override
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadMultipleFiles(
            @AuthMember Member member,
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
