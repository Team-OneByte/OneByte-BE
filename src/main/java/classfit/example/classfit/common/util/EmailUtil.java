package classfit.example.classfit.common.util;

import java.util.Random;

public class EmailUtil {

    public static String createdCode() {
        int leftLimit = 48;             // number '0'
        int rightLimit = 122;           // alphabet 'z'
        int targetStringLength = 8;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    public static String splitEmail(String email) {
        String[] splitEmail = email.split("@");
        return splitEmail[0];
    }
}
