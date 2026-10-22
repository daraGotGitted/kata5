import software.ulpgc.Room;
import software.ulpgc.Server;
import software.ulpgc.Client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        switch (args[0]) {
            case "server":
                Server s = new Server(Integer.parseInt(args[1]));
                s.Run();
                System.out.printf("Server running on port: %s\n", args[1]);
                System.out.print("Server options:\n\t-Create room: create [name]\n\t-Stop server: exit\n\n");
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    String[] command = scanner.nextLine().split(" ");
                    if(command[0].equalsIgnoreCase("Exit")) break;
                    if(command[0].equals("create")) {
                        s.CreateRoom(command[1]);
                    }
                }
                s.Close();
                break;
            case "list":
                String res = Client.RetrieveResponse(String.format("http://localhost:%s/list", args[1]));
                System.out.println(res);
                break;
            case "connect":
                // connect [port] [user] [room_id]
                String url = String.format("http://localhost:%s/room/%s", args[1], args[3]);
                boolean[] running = {true};
                String[] lastMsg = {""};

                new Thread(() -> {
                    while(running[0]) {
                        try {
                            String resMsg = Client.RetrieveResponse(url);
                            if (!lastMsg[0].equals(resMsg)) {
                                System.out.println(resMsg);
                                lastMsg[0] = resMsg;
                            }
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Scanner msgScanner = new Scanner(System.in);
                while(true) {
                    String line = msgScanner.nextLine();
                    String msg = String.format("%s: %s", args[2], line);
                    lastMsg[0] = msg;
                    if(line.equalsIgnoreCase("/exit")) {
                        running[0] = false;
                        break;
                    }
                    Client.PostRequest(url, msg);
                }
                break;
        }
    }
}
