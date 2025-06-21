/*FYI
 *Métodos:
 * - handle(HttpExchange exchange): trata a requisição HTTP e responde ao cliente se o token é válido ou não.
 */

package otp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ConfirmacaoHandler implements HttpHandler {
	
	/*
     * Espera receber uma URL no formato: /confirmar?token=VALOR_DO_TOKEN
     * Valida o token e envia uma resposta textual ao usuário.
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
