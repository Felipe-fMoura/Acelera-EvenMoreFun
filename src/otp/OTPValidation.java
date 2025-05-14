package otp;

import java.util.Scanner;
import jakarta.mail.MessagingException;
import model.Usuario;
import service.UsuarioService;

public class OTPValidation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioService usuarioService = UsuarioService.getInstance();

        // meu user teste
        usuarioService.carregarUsuariosDeTeste();

        Usuario usuarioEncontrado = null;
        String email;

        // e-mail existente
        while (usuarioEncontrado == null) {
            System.out.print("Digite seu e-mail: ");
            email = scanner.nextLine();

            for (Usuario u : usuarioService.getUsuarios()) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    usuarioEncontrado = u;
                    break;
                }
            }

            if (usuarioEncontrado == null) {
                System.out.println("E-mail não encontrado no sistema. Tente novamente.");
            }
        }

        // gera e envia OTP
        String otp = OTPGenerator.generateOTP();

        try {
            EmailSender.sendOTP(usuarioEncontrado.getEmail(), otp);
            System.out.println("Um código OTP foi enviado para seu e-mail.");
        } catch (MessagingException e) {
            System.out.println("Falha ao enviar e-mail: " + e.getMessage());
            scanner.close();
            return;
        }

        // OTP com até 3 tentativas
        boolean otpCorreto = false;
        int tentativas = 3;
        while (tentativas-- > 0) {
            System.out.print("Digite o código OTP recebido: ");
            String input = scanner.nextLine();

            if (otp.equals(input)) {
                otpCorreto = true;
                break;
            } else {
                System.out.println("Código incorreto. Tentativas restantes: " + tentativas);
            }
        }

        if (!otpCorreto) {
            System.out.println("Limite de tentativas excedido.");
            scanner.close();
            return;
        }

        // atualiza a senha com validação
        System.out.println("Código correto. Digite a nova senha:");
        String novaSenha = scanner.nextLine();

        // regras
        while (!usuarioService.validarSenha(novaSenha)) {
            System.out.println("Senha fraca. A senha deve conter pelo menos 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial.");
            System.out.print("Digite a nova senha novamente: ");
            novaSenha = scanner.nextLine();
        }

        // criptografa e atualiza
        String hash = new org.jasypt.util.password.BasicPasswordEncryptor().encryptPassword(novaSenha);
        usuarioEncontrado.setSenha(hash);

        System.out.println("Senha atualizada com sucesso!");

        scanner.close();
    }
}

