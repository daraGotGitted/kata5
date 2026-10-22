import software.ulpgc.Server;

import java.io.IOException;

public class Main {
    public static void main() throws IOException {
        Server s = new Server(3001);
        s.Init();
    }
}
