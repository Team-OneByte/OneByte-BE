package classfit.example.classfit.common.util;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .maxAge(maxAge)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
