package classfit.example.classfit.common.util;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
                throw new ClassfitException(ErrorCode.REDIS_DATA_NOT_FOUND);
            }

            return value;
        } catch (DataAccessException e) {
            throw new ClassfitException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }

    public void setDataExpire(String key, String value, long duration) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
        } catch (DataAccessException e) {
            throw new ClassfitException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }

    public void deleteData(String key) {
        try {
            Boolean result = redisTemplate.delete(key);

            if (Boolean.FALSE.equals(result)) {
                throw new ClassfitException(ErrorCode.REDIS_DELETE_FAILED);
            }
        } catch (DataAccessException e) {
            throw new ClassfitException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }
}
