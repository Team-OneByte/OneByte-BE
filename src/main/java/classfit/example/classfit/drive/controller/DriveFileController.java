package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveFileControllerDocs;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DrivePreSignedResponse;
import classfit.example.classfit.drive.service.DriveDownloadService;
import classfit.example.classfit.drive.service.DriveUploadService;
import classfit.example.classfit.member.domain.Member;
import java.net.URL;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
public class DriveFileController implements DriveFileControllerDocs {

    private final DriveUploadService driveUploadService;
    private final DriveDownloadService driveDownloadService;

    @Override
    @PostMapping("/request-url")
    public CustomApiResponse<List<DrivePreSignedResponse>> getPreSignedUrl(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam String folderPath,
            @RequestParam List<String> fileName
    ) {
        List<DrivePreSignedResponse> preSignedUrl = driveUploadService.getPreSignedUrl(member, driveType, folderPath, fileName);
        return CustomApiResponse.success(preSignedUrl, 200, "사전 서명된 URL 생성 성공");
    }

    @Override
    @PostMapping("/upload-confirm")
    public CustomApiResponse<Void> upLoadConfirm(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam String folderPath,
            @RequestParam List<String> fileName
    ) {
        driveUploadService.uploadConfirm(member, driveType, folderPath, fileName);
        return CustomApiResponse.success(null, 200, "파일 업로드 확인 성공");
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
