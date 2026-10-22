package software.ulpgc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Server {
    private String[] currentMessage = new String[5];
    private HttpServer server;
    private int port;
    private ArrayList<Room> roomList;

    public Server(int port) {
        this.port = port;
        this.roomList = new ArrayList<>();
        Arrays.fill(currentMessage, "");
    }

    public void Run() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(this.port), 0);

            server.createContext("/list", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String res = "";
                    for(Room r : Server.this.roomList) {
                        res += String.format("%s (Id: %s)\n", r.getName(), r.getId());
                    }
                    exchange.sendResponseHeaders(200, res.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(res.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            });

            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Close() {
        this.server.stop(0);
    }

    public void CreateRoom(String name) {
        int room_id = this.roomList.size();
        Room r = new Room(room_id, name);
        roomList.add(r);
        server.createContext(String.format("/room/%d", room_id), new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                switch (method) {
                    case "GET":
                        exchange.sendResponseHeaders(200, Server.this.currentMessage[room_id].getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os_get = exchange.getResponseBody();
                        os_get.write(Server.this.currentMessage[room_id].getBytes(StandardCharsets.UTF_8));
                        os_get.close();
                        break;
                    case "POST":
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                        Server.this.currentMessage[room_id] = isr.readAllLines().stream().collect(Collectors.joining("\n"));
                        isr.close();
                        exchange.sendResponseHeaders(204, -1);
                        OutputStream os_post = exchange.getResponseBody();
                        break;
                }

            }
        });
        System.out.printf("Added room %s\n", name);
    }
}
