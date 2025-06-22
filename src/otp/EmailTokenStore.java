/*
 * saveToken(String token, String email)
 * Salva um token único associado ao e-mail para confirmação:
 * - Armazena o par token -> e-mail no mapa estático.
 *
 * consumeToken(String token)
 * Consome (remove e retorna) o e-mail associado ao token:
 * - Remove o token do mapa para garantir uso único.
 * - Retorna o e-mail vinculado ou null se token inválido.
 *
 * isEmailConfirmed(String email)
 * Verifica se o e-mail já foi confirmado:
 * - Retorna true se o e-mail não estiver mais associado a nenhum token (confirmado).
 *
 * Estruturas e técnicas utilizadas:
 * - Uso de Map estático para armazenamento temporário.
 * - Controle de confirmação por token de uso único.
 * - Operações simples de put, remove e containsValue para gerenciamento dos tokens.
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
