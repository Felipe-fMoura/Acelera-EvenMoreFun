/*
 * Métodos relevantes criados:
 * - sendOTP(String toEmail, String otp): envia e-mail com código OTP no corpo.
 * - sendEmail(String toEmail, String subject, String body): envia e-mail simples com assunto e texto.
 * - sendEmailWithAttachment(String toEmail, String subject, String body, byte[] attachmentBytes, String filename): envia e-mail com anexo.
 */

package otp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailSender {

	public static void sendOTP(String toEmail, String otp) throws MessagingException {
		sendEmail(toEmail, "Seu código OTP", "Seu código OTP é: " + otp);
	}

	public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
		final String fromEmail = "emf2k25@gmail.com";
		final String password = "xxtf riyg srdr xuve";

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setSubject(subject);
		message.setText(body);

		Transport.send(message);
	}

	public static void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] attachmentBytes,
			String filename) throws MessagingException {
		final String fromEmail = "emf2k25@gmail.com";
		final String password = "xxtf riyg srdr xuve";

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setSubject(subject);

		// Parte de texto
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(body);

		// Parte do anexo (imagem, PDF, etc.)
		MimeBodyPart attachmentPart = new MimeBodyPart();
		DataSource dataSource = new ByteArrayDataSourceCustom(attachmentBytes, "application/octet-stream");
		attachmentPart.setDataHandler(new DataHandler(dataSource));
		attachmentPart.setFileName(filename);

		// Juntar tudo no corpo do e-mail
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(textPart);
		multipart.addBodyPart(attachmentPart);

		message.setContent(multipart);

		Transport.send(message);
	}

	// Classe custom para substituir ByteArrayDataSource externo
	public static class ByteArrayDataSourceCustom implements DataSource {

		private byte[] data;
		private String type;
		private String name = "ByteArrayDataSourceCustom";

		public ByteArrayDataSourceCustom(byte[] data, String type) {
			this.data = data;
			this.type = type;
		}

		// Retorna os dados do anexo como InputStream
		@Override
		public InputStream getInputStream() throws IOException {
			if (data == null) {
				throw new IOException("No data");
			}
			return new ByteArrayInputStream(data);
		}

		// Impede a escrita no fluxo de saída
		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}

		// Retorna o tipo MIME do conteúdo
		@Override
		public String getContentType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
