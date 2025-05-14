package otp;

import java.util.Scanner;
import jakarta.mail.MessagingException;

public class OTPValidation {
    public static void main(String[] args) {
    	
    	System.setProperty("mail.debug", "false"); //tenho preguica de ajustar o warning.

        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu e-mail: ");
        String email = scanner.nextLine();

        String otp = OTPGenerator.generateOTP();

        try {
            EmailSender.sendOTP(email, otp);
            System.out.println("Um código OTP foi enviado para seu e-mail.");
        } catch (MessagingException e) {
            System.out.println("Falha ao enviar e-mail: " + e.getMessage());
            scanner.close();
            return;
        }

        String input;
        do {
            System.out.print("Digite o código OTP recebido: ");
            input = scanner.nextLine();

            if (!otp.equals(input)) {
                System.out.println("Código incorreto. Tente novamente.");
            }

        } while (!otp.equals(input));

        System.out.println("Código correto. Digite a nova senha:");
        String novaSenha = scanner.nextLine();
        System.out.println("Senha atualizada com sucesso: " + novaSenha);

        scanner.close();
    }
}

