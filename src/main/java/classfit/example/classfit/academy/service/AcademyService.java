package classfit.example.classfit.academy.service;

import classfit.example.classfit.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcademyService {

    public String createCode() {
        return CodeUtil.createdCode();
    }
}

