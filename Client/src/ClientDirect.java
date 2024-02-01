import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientDirect {
    public static ArrayList<HashMap<String , Object>> showDirects() throws IOException, ClassNotFoundException {
        Packet showDirectPacket = new Packet();
        showDirectPacket.request = "showDirect";
        Client.output.writeObject(showDirectPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
    public static boolean direct(String receiverId, String message) throws IOException, ClassNotFoundException {
        Packet directPacket = new Packet();
        directPacket.request = "direct";
        directPacket.parameters.put("receiverId" , receiverId);
        directPacket.parameters.put("message" , message);
        Client.output.writeObject(directPacket);
        Packet result = (Packet) Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : "+ result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean direct() throws IOException, ClassNotFoundException {
        System.out.print("Enter the recieverId: ");
        String receiverId = Client.scanner.nextLine();
        System.out.print("Enter the message : ");
        String message = Client.scanner.nextLine();
        return direct(receiverId, message);
    }
    public static ArrayList<HashMap<String , Object>> getDirect(String userId) throws IOException, ClassNotFoundException {
        Packet getDirectPacket = new Packet();
        getDirectPacket.request = "getDirect";
        getDirectPacket.parameters.put("userId" , userId);
        Client.output.writeObject(getDirectPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
    public static ArrayList<HashMap<String , Object>> getDirect() throws IOException, ClassNotFoundException {
        System.out.print("Enter userId to get message: ");
        return getDirect(Client.scanner.nextLine());
    }
}
