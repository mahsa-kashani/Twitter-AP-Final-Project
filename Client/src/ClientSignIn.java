import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.nio.file.*;

public class ClientSignIn {

    public static void saveTokenToFile(String token) throws IOException{
        Files.writeString(Paths.get("token.txt") , token);
    }

    public static boolean signIn(String userId, String password) throws IOException, ClassNotFoundException {
        Packet signInPacket = new Packet();
        signInPacket.request = "sign-in";
        signInPacket.parameters.put("userId" , userId);
        signInPacket.parameters.put("password" , password);
        Client.output.writeObject(signInPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Error : "+ result.parameters.get("result"));
            return false;
        }
        String token = result.parameters.get("token");
        saveTokenToFile(token);
        return true;
    }

    public static boolean signIn() throws IOException , ClassNotFoundException{
        System.out.print("Please Enter userId: ");
        String userId = Client.scanner.nextLine();

        System.out.print("Please Enter password: ");
        String password = Client.scanner.nextLine();
        return signIn(userId , password);
    }
}
