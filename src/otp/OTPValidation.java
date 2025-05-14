package otp;

import java.util.Scanner;
import javax.mail.MessagingException;

public class OTPValidation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu e-mail: ");
        String email = scanner.nextLine();

        String otp = OTPGenerator.generateOTP();

        try {
            EmailSender.sendOTP(email, otp);
            System.out.println("Um código OTP foi enviado para seu e-mail.");
        } catch (MessagingException e) {
            System.out.println("Falha ao enviar e-mail: " + e.getMessage());
            return;
        }

        System.out.print("Digite o código OTP recebido: ");
        String input = scanner.nextLine();

        if (otp.equals(input)) {
            System.out.println("Código correto. Digite a nova senha:");
            String novaSenha = scanner.nextLine();
            System.out.println("Senha atualizada com sucesso: " + novaSenha); // Simulação
        } else {
            System.out.println("Código incorreto.");
        }

        scanner.close();
    }
}
