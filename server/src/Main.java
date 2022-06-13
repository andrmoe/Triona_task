import java.io.*;
import java.net.*;
import java.util.HashMap;
import com.sun.net.httpserver.*;


class Person {

    private String name;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;

    private static HashMap<String, Person> storage = new HashMap<>();

    public Person(String json) {
        // Remove curly braces

        json = json.substring(1, json.length()-1);

        if (json.length() == 0) {
            throw new IllegalArgumentException();
        }

        String[] keysAndValues = json.split(",");

        for (String keyAndValue: keysAndValues) {
            String[] param = keyAndValue.split(":");
            switch (param[0].substring(1, param[0].length()-1)) {
                case "name" -> this.name = param[1].substring(1, param[1].length()-1);
                case "address" -> this.address = param[1].substring(1, param[1].length()-1);
                case "phoneNumber" -> this.phoneNumber = param[1].substring(1, param[1].length()-1);
                case "dateOfBirth" -> this.dateOfBirth = param[1].substring(1, param[1].length()-1);
            }
        }
    }

    public Person() {
        String message = "Not Found";
        name = message;
        address = message;
        phoneNumber = message;
        dateOfBirth = message;
    }

    public String toJSON() {
        return "{\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"phoneNumber\":\"" +
                phoneNumber + "\",\"dateOfBirth\":\"" + dateOfBirth + "\"}";
    }

    public void store() {
        storage.put(name, this);
    }

    public static Person lookUp(String name) {
        if (storage.containsKey(name)) {
            return storage.get(name);
        } else {
            return new Person();
        }
    }
}


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
                        responseBody.write(Person.lookUp(name.replace('_', ' ')).toJSON().getBytes());
                        responseBody.close();
                    }
                } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    Person person = new Person(new String(exchange.getRequestBody().readAllBytes()));
                    person.store();
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