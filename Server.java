import java.io.*;
import java.util.*;
import java.net.*;

public class Server {
    static Vector<ClientHandler> clients = new Vector<>();

    private static boolean checkNicknameAccessibility(String nick) {
        for(ClientHandler nicknames : clients) {
            if(nick.equals(nicknames.nickname))
                return false;
        }
        return true;
    }
    public static void printCurrentClients() {
        System.out.println("Online: ");
        for(ClientHandler client : clients) {
            System.out.println("- " + client.nickname);
        }
        System.out.println();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void informAboutConnection(String nickname) throws IOException {
        for(ClientHandler availableClients : Server.clients) {
            if(availableClients.nickname.equals(nickname))
                continue;
            availableClients.output.writeUTF("\r[" + nickname + " connected to the chat]");
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2137);
            Socket socket;
            System.out.println("Server started");
            //noinspection InfiniteLoopStatement
            while (true) {
                socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                String nickname = input.readUTF();
                if(nickname.isEmpty()) {
                    output.writeUTF("Nickname cannot be empty");
                    socket.close();
                    continue;
                }
                if(!checkNicknameAccessibility(nickname)) {
                    output.writeUTF("Nickname '" + nickname + "' has been chosen");
                    socket.close();
                    continue;
                }
                else
                    output.writeUTF("[Connected - type logout or press ctrl+c to exit]");
                informAboutConnection(nickname);
                ClientHandler clientHandler = new ClientHandler(socket, nickname , input, output);
                Thread thread = new Thread(clientHandler);
                clients.add(clientHandler);
                thread.start();
                clearScreen();
                printCurrentClients();
            }
        } catch (IOException e) {
            System.err.println("Server cannot start");
        }
    }
}