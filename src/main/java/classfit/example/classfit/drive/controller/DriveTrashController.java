package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.service.DriveTrashService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
@Tag(name = "드라이브 휴지통 컨트롤러", description = "드라이브 휴지통 관련 API입니다.")
public class DriveTrashController {

    private final DriveTrashService driveTrashService;

    @PostMapping("/trash")
    public ApiResponse<String> moveToTrash(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "파일이름 및 폴더(folder/), 폴더 입력 시 하위 목록까지 휴지통으로 이동합니다.")
        @RequestParam String fileName
    ) {
        String trashPath = driveTrashService.moveToTrash(member, driveType, fileName);
        return ApiResponse.success(trashPath, 200, "휴지통 이동 완료");
    }
}
