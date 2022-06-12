import java.io.*;
import java.net.*;
import java.util.HashMap;
import com.sun.net.httpserver.*;


class Server {

    public static  void main(String args[]) {

        HttpHandler handler = new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                URI uri = exchange.getRequestURI();

                if (exchange.getRequestMethod().equalsIgnoreCase("GET") && uri.getQuery().contains("=")) {

                    String[] stringParams = uri.getQuery().split("=");
                    if (!stringParams[0].equals("name")) {
                        exchange.sendResponseHeaders(400, -1);
                    } else {
                        String name = stringParams[1];
                        Headers responseHeaders = exchange.getResponseHeaders();
                        responseHeaders.set("Content-Type", "application/json");
                        OutputStream responseBody  = exchange.getResponseBody();
                        exchange.sendResponseHeaders(200, 0);
                        responseBody.write("{\"name\":\"Andreas\",\"address\":\"Munkegata 1\",\"phoneNumber\":\"01234567\",\"dateOfBirth\":\"2000-01-01\"}".getBytes());
                        responseBody.close();
                    }
                } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    System.out.println(new String(exchange.getRequestBody().readAllBytes()));
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            }
        };

        String context = "/person";

        try {

            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
            httpServer.createContext(context, handler);
            httpServer.setExecutor(null);

            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}