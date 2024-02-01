import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientBlock {
    public static boolean block(String userId) throws IOException, ClassNotFoundException {
        Packet blockPacket = new Packet();
        blockPacket.request = "block";
        blockPacket.parameters.put("userId" , userId);
        Client.output.writeObject(blockPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : "+ result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean block() throws IOException, ClassNotFoundException {
        System.out.print("Enter userId to block: ");
        return block(Client.scanner.nextLine());
    }
    public static boolean unblock(String userId) throws IOException, ClassNotFoundException {
        Packet unblockPacket = new Packet();
        unblockPacket.request = "unblock";
        unblockPacket.parameters.put("userId" , userId);
        Client.output.writeObject(unblockPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : "+ result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean unblock() throws IOException, ClassNotFoundException {
        System.out.print("Enter userId to unblock: ");
        return unblock(Client.scanner.nextLine());
    }
    public static ArrayList<HashMap<String , Object>> getBlockList() throws IOException, ClassNotFoundException {
        Packet getBlockListPacket = new Packet();
        getBlockListPacket.request = "getBlockList";
        Client.output.writeObject(getBlockListPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
}
