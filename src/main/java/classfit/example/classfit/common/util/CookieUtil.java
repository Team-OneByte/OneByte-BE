package classfit.example.classfit.common.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static ResponseCookie setCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();
    }
}
