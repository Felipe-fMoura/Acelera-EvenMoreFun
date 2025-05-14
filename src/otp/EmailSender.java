package otp;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void sendOTP(String toEmail, String otp) throws MessagingException {
        final String fromEmail = "emf2k25@gmail.com"; 
        final String password = "xxtf riyg srdr xuve";         

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Seu código OTP");
        message.setText("Seu código OTP é: " + otp);

        Transport.send(message);
    }
}
