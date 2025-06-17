package otp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.EventoService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

            String html = "<html><head><meta charset='UTF-8'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; text-align: center; padding-top: 50px; }" +
                    "h1 { color: #2ecc71; }" +
                    "</style></head>" +
                    "<body><h1>✅ Presença confirmada com sucesso!</h1></body></html>";

            sendHtmlResponse(exchange, 200, html);

        } catch (Exception e) {
            e.printStackTrace();

            String html = "<html><head><meta charset='UTF-8'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #fff0f0; text-align: center; padding-top: 50px; }" +
                    "h1 { color: #e74c3c; }" +
                    "</style></head>" +
                    "<body><h1>❌ Erro ao registrar presença</h1><p>" + e.getMessage() + "</p></body></html>";

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
        if (query == null || query.isEmpty()) return params;

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
