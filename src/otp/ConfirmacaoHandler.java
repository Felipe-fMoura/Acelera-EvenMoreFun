/*
 * - Implementa HttpHandler para tratar requisições HTTP GET no caminho /confirmar.
 * - Recebe um parâmetro "token" via query string da URL.
 * - Valida o token consumindo-o da store (EmailTokenStore).
 * - Retorna uma resposta textual simples para o usuário:
 *   - Confirmação de e-mail validado com sucesso.
 *   - Mensagem de token inválido ou já utilizado.
 * - Responde com HTTP 400 caso o parâmetro "token" esteja ausente.
 * - Responde com HTTP 405 para métodos HTTP diferentes de GET.
 *
 * Fluxo principal:
 * - Recebe requisição GET /confirmar?token=xxxx
 * - Extrai e decodifica token da query string
 * - Consulta EmailTokenStore.consumeToken(token) para validar e consumir o token
 * - Envia resposta texto com status 200 e mensagem adequada
 *
 */

package otp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ConfirmacaoHandler implements HttpHandler {

	/*
	 * Espera receber uma URL no formato: /confirmar?token=VALOR_DO_TOKEN Valida o
	 * token e envia uma resposta textual ao usuário.
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		// Verifica o método HTTP
		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
			return;
		}

		// Obtém o token da query string
		String query = exchange.getRequestURI().getQuery();
		if (query == null || !query.contains("token=")) {
			String erro = "Parâmetro 'token' ausente na URL.";
			byte[] erroBytes = erro.getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(400, erroBytes.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(erroBytes);
			}
			return;
		}

		String token = query.split("=")[1];
		token = URLDecoder.decode(token, StandardCharsets.UTF_8);

		// Consome o token
		String email = EmailTokenStore.consumeToken(token);
		String resposta;

		if (email != null) {
			resposta = "O e-mail" + email + "foi verificado. Agora você já pode acessar sua conta normalmente.";
		} else {
			resposta = "Token inválido ou já utilizado.";
		}

		// Volta resposta
		byte[] respostaBytes = resposta.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
		exchange.sendResponseHeaders(200, respostaBytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(respostaBytes);
		}
	}
}
