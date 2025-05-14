package otp;

import java.util.Random;

public class OTPGenerator {
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Garante 6 dígitos
        return String.valueOf(otp);
    }
}
