import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    final static int ServerPort = 2137;

    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            Scanner scanner = new Scanner(System.in);
            Socket socket = new Socket(ip, ServerPort);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            System.out.print("Enter nick: ");
            String nick = scanner.nextLine();
            output.writeUTF(nick);
            System.out.println(input.readUTF());
            Thread sendMessage = new Thread(() -> {
                while (true) {
                    System.out.print(nick + ": ");
                    try {
                        String msg = scanner.nextLine();
                        if (msg.isEmpty())
                            continue;
                        output.writeUTF(msg);
                    } catch (IOException e) {
                        System.out.println("Server unavailable, exit");
                        System.exit(0);
                    }
                }
            });

            Thread readMessage = new Thread(() -> {
                while (true) {
                    try {
                        String msg = input.readUTF();
                        System.out.print("\r");
                        System.out.print(msg + "\n" + nick + ": ");
                    } catch (IOException e) {
                        System.err.println("[Disconnect]");
                        System.exit(1);
                    }
                }
            });

            sendMessage.start();
            readMessage.start();
        } catch (IOException e) {
            System.err.println("Cannot connect to server");

        }
    }
} 