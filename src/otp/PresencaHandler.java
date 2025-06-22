/*
 * PresencaHandler (implementa HttpHandler)
 *
 * Descrição geral:
 * - Trata requisições HTTP GET para confirmar a presença de um usuário em um evento.
 * - Recebe os parâmetros via query string: eventoId e usuarioId.
 * - Chama o serviço EventoService para marcar a presença do usuário no evento.
 * - Retorna uma resposta HTML simples informando sucesso ou erro na operação.
 *
 * Métodos e fluxo:
 *
 * 1. handle(HttpExchange exchange)
 *    - Verifica se o método HTTP é GET, caso contrário retorna 405 Method Not Allowed.
 *    - Obtém a query string da URL e parseia os parâmetros (eventoId e usuarioId).
 *    - Converte os parâmetros para inteiros.
 *    - Chama eventoService.marcarPresenca(eventoId, usuarioId) para registrar presença.
 *    - Se a operação ocorrer com sucesso, envia resposta HTML indicando confirmação.
 *    - Se ocorrer erro, captura exceção e envia uma resposta HTML de erro com mensagem.
 *
 * 2. sendHtmlResponse(HttpExchange exchange, int statusCode, String html)
 *    - Método auxiliar para enviar resposta HTTP com conteúdo HTML.
 *    - Define o header Content-Type para "text/html; charset=UTF-8".
 *    - Envia o status HTTP e escreve o corpo da resposta.
 *
 * 3. parseQuery(String query)
 *    - Converte a query string em um Map<String, String> com chave e valor.
 *    - Trata codificação URL (UTF-8) para chave e valor.
 *    - Retorna mapa vazio caso query seja nula ou vazia.
 *
 * Técnicas utilizadas:
 * - Uso da API HttpServer do JDK (com.sun.net.httpserver) para criar servidor HTTP leve.
 * - Manipulação manual de query string para extrair parâmetros.
 * - Uso de try-catch para tratamento de exceções na lógica de presença.
 * - Geração dinâmica de resposta HTML simples para feedback ao usuário.
 *
 */

package otp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import service.EventoService;

public class PresencaHandler implements HttpHandler {

	private final EventoService eventoService = EventoService.getInstance(); // ajuste conforme seu acesso

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (!"GET".equals(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(405, -1); // Method Not Allowed
			return;
		}

		String query = exchange.getRequestURI().getQuery();
		Map<String, String> params = parseQuery(query);

		try {
			int eventoId = Integer.parseInt(params.get("eventoId"));
			int usuarioId = Integer.parseInt(params.get("usuarioId"));

			eventoService.marcarPresenca(eventoId, usuarioId);

			String html = "<html><head><meta charset='UTF-8'>" + "<style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f9f9f9; text-align: center; padding-top: 50px; }"
					+ "h1 { color: #2ecc71; }" + "</style></head>"
					+ "<body><h1>✅ Presença confirmada com sucesso!</h1></body></html>";

			sendHtmlResponse(exchange, 200, html);

		} catch (Exception e) {
			e.printStackTrace();

			String html = "<html><head><meta charset='UTF-8'>" + "<style>"
					+ "body { font-family: Arial, sans-serif; background-color: #fff0f0; text-align: center; padding-top: 50px; }"
					+ "h1 { color: #e74c3c; }" + "</style></head>" + "<body><h1>❌ Erro ao registrar presença</h1><p>"
					+ e.getMessage() + "</p></body></html>";

			sendHtmlResponse(exchange, 400, html);
		}
	}

	private void sendHtmlResponse(HttpExchange exchange, int statusCode, String html) throws IOException {
		byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(statusCode, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	private Map<String, String> parseQuery(String query) {
		Map<String, String> params = new HashMap<>();
		if (query == null || query.isEmpty()) {
			return params;
		}

		for (String pair : query.split("&")) {
			String[] parts = pair.split("=");
			if (parts.length == 2) {
				String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
				String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
				params.put(key, value);
			}
		}
		return params;
	}
}
