package classfit.example.classfit.drive.dto.response;

import lombok.Builder;

@Builder
public record DrivePreSignedResponse(
        String objectName,
        String preSignedUrl
) {

    public static DrivePreSignedResponse of(String objectName, String preSignedUrl) {
        return DrivePreSignedResponse.builder()
                .objectName(objectName)
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
