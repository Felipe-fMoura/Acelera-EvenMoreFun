package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.EmailTokenStore;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ConfirmacaoHandler implements HttpHandler {
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
