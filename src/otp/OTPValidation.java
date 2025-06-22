/*
 * OTPValidation (classe com método main)

 *    - usuarioService.carregarUsuariosDeTeste()
 *      Preenche a lista de usuários para teste, permitindo simular o processo.
 *
 * 2 Validação do e-mail informado:
 *    - Loop que solicita o e-mail até que seja encontrado na lista de usuários cadastrados.
 *    - Percorre a lista de usuários com getUsuarios() para comparar o e-mail digitado.
 *    - Caso e-mail não seja encontrado, exibe mensagem de erro e repete o pedido.
 *
 * 3 Geração e envio do código OTP:
 *    - OTPGenerator.generateOTP()
 *      Gera um código OTP aleatório para validação temporária.
 *    - EmailSender.sendOTP(email, otp)
 *      Envia o código OTP para o e-mail do usuário usando o serviço SMTP configurado.
 *    - Em caso de erro no envio, o programa encerra com mensagem de falha.
 *
 * 4 Validação do código OTP recebido:
 *    - Permite até 3 tentativas para o usuário digitar o código correto.
 *    - Cada entrada do usuário é comparada com o código OTP gerado.
 *    - Se o código está incorreto, informa tentativas restantes.
 *    - Se o limite de tentativas for excedido, encerra o programa.
 *
 * 5 Solicitação e validação da nova senha:
 *    - Após confirmação do OTP, solicita ao usuário a nova senha.
 *    - Usa usuarioService.validarSenha(String senha) para checar regras de força da senha:
 *        - Pelo menos 8 caracteres.
 *        - Contém letra maiúscula, minúscula, número e caractere especial.
 *    - Se a senha não for válida, repete a solicitação até uma senha válida ser fornecida.
 *
 * 6 Criptografia e atualização da senha:
 *    - Utiliza BasicPasswordEncryptor do Jasypt para criptografar a senha em hash seguro.
 *    - Atualiza o objeto Usuario com a senha criptografada.
 *    - Exibe mensagem de sucesso.
 *
 * 7 Finalização:
 *    - Fecha o Scanner para liberar o recurso.
 *
 * Técnicas e bibliotecas usadas:
 * - Scanner: para entrada de dados via console.
 * - Laços while: para repetição de entrada e validação.
 * - Comparação de Strings para validação de e-mail e OTP.
 * - Geração de OTP para autenticação temporária.
 * - Envio de e-mail com Jakarta Mail (via EmailSender).
 * - Validação de senha com regras customizadas (implementadas em UsuarioService).
 * - Criptografia com Jasypt (BasicPasswordEncryptor).
 */


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
			System.out.println(
					"Senha fraca. A senha deve conter pelo menos 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial.");
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
