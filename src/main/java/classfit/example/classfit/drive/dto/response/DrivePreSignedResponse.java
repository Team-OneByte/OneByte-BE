package classfit.example.classfit.drive.dto.response;

import lombok.Builder;

@Builder
public record DrivePreSignedResponse(
        String fileName,
        String preSignedUrl
) {

    public static DrivePreSignedResponse of(String preSignedUrl, String fileName) {
        return DrivePreSignedResponse.builder()
                .fileName(fileName)
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
