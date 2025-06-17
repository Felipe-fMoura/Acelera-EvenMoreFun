/*FYI
 *Métodos:
 * - handle(HttpExchange exchange): trata a requisição HTTP e responde ao cliente se o token é válido ou não.
 */

package otp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.EmailTokenStore;

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
        String query = exchange.getRequestURI().getQuery();
        String token = query.split("=")[1];
        token = URLDecoder.decode(token, StandardCharsets.UTF_8);

        String email = EmailTokenStore.consumeToken(token);
        String resposta;

        if (email != null) {
            resposta = "E-mail " + email + " confirmado com sucesso!";
        } else {
            resposta = "Token inválido ou já utilizado.";
        }

        exchange.sendResponseHeaders(200, resposta.length());
        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes());
        os.close();
    }
}
