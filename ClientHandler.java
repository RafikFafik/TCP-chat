import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler implements Runnable {
    final DataInputStream input;
    final DataOutputStream output;
    boolean isConnected = true;
    Socket socket;
    String nickname;

    public ClientHandler(Socket sock, String nick, DataInputStream dis, DataOutputStream dos) {
        this.input = dis;
        this.output = dos;
        this.socket = sock;
        this.nickname = nick;
    }

    private int getClientCurrentIndex() {
        for (ClientHandler availableClients : Server.clients) {
            if (availableClients.nickname.equals(nickname))
                return Server.clients.indexOf(availableClients);
        }
        return -1;
    }

    private void onLeaveMessage(String nickname) throws IOException {
        for (ClientHandler availableClients : Server.clients) {
            if (availableClients.nickname.equals(nickname))
                continue;
            availableClients.output.writeUTF("[" + nickname + " disconnected]");
        }
    }

    @Override
    public void run() {
        String received;
        while (isConnected) {
            try {
                received = input.readUTF();
                if (received.equals("logout")) {
                    socket.close();
                    continue;
                }
                for (ClientHandler availableClients : Server.clients) {
                    if (availableClients.nickname.equals(nickname))
                        continue;
                    availableClients.output.writeUTF(nickname + ": " + received);
                }
            } catch (IOException e) {
                try {
                    onLeaveMessage(nickname);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                isConnected = false;
                int currentIndex = getClientCurrentIndex();
                Server.clients.remove(currentIndex);
                Server.clearScreen();
                Server.printCurrentClients();
            }
        }
    }
}