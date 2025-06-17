/*
 * Métodos relevantes criados:
 * - saveToken(String token, String email): armazena o token associado ao e-mail.
 * - consumeToken(String token): remove o token e retorna o e-mail associado.
 * - isEmailConfirmed(String email): verifica se o e-mail já foi confirmado (token removido).
 */

package otp;

import java.util.HashMap;
import java.util.Map;

public class EmailTokenStore {
    private static Map<String, String> tokenToEmail = new HashMap<>();

    public static void saveToken(String token, String email) {
        tokenToEmail.put(token, email);
    }

    public static String consumeToken(String token) {
        return tokenToEmail.remove(token);
    }

    public static boolean isEmailConfirmed(String email) {
        return !tokenToEmail.containsValue(email);
    }
}
