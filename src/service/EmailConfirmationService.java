package service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import jakarta.mail.MessagingException;
import otp.EmailSender;


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
