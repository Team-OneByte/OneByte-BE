package classfit.example.classfit.common.util;

import classfit.example.classfit.auth.security.custom.CustomUserDetails;
import classfit.example.classfit.common.exception.ClassfitAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getMemberId();
        } else {
            throw new ClassfitAuthException("로그인한 회원의 정보를 확인할 수 없습니다", HttpStatus.NOT_FOUND);
        }
    }
}
