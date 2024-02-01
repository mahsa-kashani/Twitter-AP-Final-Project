import java.io.IOException;
import java.net.*;

public class Server {
        public static void main(String[] args) throws IOException, ClassNotFoundException {
            ServerSocket server = new ServerSocket(1500);
            Token.loadTokens();
            while(true){
                Socket client = server.accept();
                Thread thread = new Thread(new ClientHandler(client));
                thread.start();
            }
    }
}