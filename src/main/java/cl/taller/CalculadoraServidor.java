package cl.taller;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
public class CalculadoraServidor {
    public static void main(String[] args) throws Exception {
        var calculadora = new Calculadora();
        var servidor = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0
        );
        servidor.createContext("/suma", intercambio -> {
            byte[] respuesta = Integer.toString(
                calculadora.sumar(4, 3)
            ).getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=utf-8"
            );
            intercambio.sendResponseHeaders(200, respuesta.length);
            try (var salida = intercambio.getResponseBody()) {
                salida.write(respuesta);
            }
        });
        servidor.start();
        System.out.println(
            "Servidor en http://127.0.0.1:8080/suma"
        );
    }
}
