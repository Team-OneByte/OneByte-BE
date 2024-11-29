package classfit.example.classfit.member.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.response.MemberResponse;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;

    @Transactional
    public MemberResponse signIn(MemberRequest request) {

        String emailToken = redisUtil.getData("Email Token : " + request.email());

        if (!emailToken.equals(request.emailToken())) {
            throw new ClassfitException("이메일 검증에 문제가 발생하였습니다. 이메일 인증을 다시 시도해 주세요", HttpStatus.NOT_FOUND);
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new ClassfitException("이미 사용 중인 이메일(ID) 입니다.", HttpStatus.CONFLICT);
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new ClassfitException("비밀번호가 일치하지 않습니다.", HttpStatus.CONFLICT);
        }

        Member member = request.toEntity(bCryptPasswordEncoder);

        memberRepository.save(member);
        return MemberResponse.from(member);
    }
}
