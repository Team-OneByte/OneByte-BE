package classfit.example.classfit.auth.annotation;

import classfit.example.classfit.common.exception.ClassfitAuthException;
import classfit.example.classfit.common.util.SecurityUtil;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthMember.class) != null
            && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        return memberRepository.findById(currentMemberId)
            .orElseThrow(() -> new ClassfitAuthException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}