package software.ulpgc;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    int port;
    public Server(int port) {
        this.port = port;
    }

    public int Init() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);

        server.start();
        return 0;
    }
}
