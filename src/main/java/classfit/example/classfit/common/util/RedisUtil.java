package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            String value = valueOperations.get(key);

            if (value == null) {
                throw new ClassfitException("해당 키에 대한 값이 존재하지 않습니다: " + key, HttpStatus.NOT_FOUND);
            }

            return value;
        } catch (DataAccessException e) {
            throw new ClassfitException("Redis 서버에서 데이터를 가져오는 중 오류가 발생했습니다: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setDataExpire(String key, String value, long duration) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
        } catch (DataAccessException e) {
            log.error("Redis 서버에 데이터를 설정하는 중 오류 발생", e); // 로그 추가
            throw new ClassfitException("Redis 서버에 데이터를 설정하는 중 오류가 발생했습니다: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteData(String key) {
        try {
            Boolean result = redisTemplate.delete(key);

            if (Boolean.FALSE.equals(result)) {
                throw new ClassfitException("삭제 요청 실패: 키가 존재하지 않거나 삭제되지 않았습니다: ", HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            throw new ClassfitException("Redis 서버에서 데이터를 삭제하는 중 오류가 발생했습니다: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
