/*
 * Método relevante criado:
 * - generateOTP(): gera um código OTP numérico de 6 dígitos.
 */

package otp;

import java.util.Random;

public class OTPGenerator {
	public static String generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
}
