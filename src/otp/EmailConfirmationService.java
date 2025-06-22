/*
 * - Responsável por iniciar o fluxo de confirmação de e-mail do usuário.
 * - Gera um token único via UUID.
 * - Salva o token associado ao e-mail no EmailTokenStore.
 * - Monta o link de confirmação contendo o token como parâmetro URL codificado.
 * - Envia um e-mail para o usuário com o link de confirmação.
 * - Em caso de erro no envio, imprime stack trace (pode ser melhorado com logging ou tratamento adequado).
 *
 * Fluxo principal:
 * - Chamar iniciarConfirmacaoEmail(email, nome) para disparar o e-mail de confirmação.
 * - O usuário recebe o link e, ao clicar, acessa endpoint que valida o token.
 */

package otp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import jakarta.mail.MessagingException;

public class EmailConfirmationService {
	public static void iniciarConfirmacaoEmail(String email, String nome) {
		String token = UUID.randomUUID().toString();
		EmailTokenStore.saveToken(token, email);
		String link = "http://localhost:8080/confirmar?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
		String corpo = "Olá " + nome + ",\n\nClique no link para confirmar seu cadastro:\n" + link;
		try {
			EmailSender.sendEmail(email, "Confirme seu cadastro", corpo);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
