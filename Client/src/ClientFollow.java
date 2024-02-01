import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientFollow {
    public static boolean follow(String userId) throws IOException, ClassNotFoundException {
        Packet followPacket = new Packet();
        followPacket.request = "follow";
        followPacket.parameters.put("userId" , userId);
        Client.output.writeObject(followPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : "+ result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean follow() throws IOException, ClassNotFoundException {
        System.out.print("Enter the userId to follow : ");
        return follow(Client.scanner.nextLine());
    }

    public static boolean unFollow(String userId) throws IOException, ClassNotFoundException {
        Packet followPacket = new Packet();
        followPacket.request = "unFollow";
        followPacket.parameters.put("userId" , userId);
        Client.output.writeObject(followPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : "+ result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean unFollow() throws IOException, ClassNotFoundException {
        System.out.print("Enter the userId to follow: ");
        return unFollow(Client.scanner.nextLine());
    }
    public static ArrayList<HashMap<String , Object>> getFollowers() throws IOException, ClassNotFoundException {
        Packet searchPacket = new Packet();
        searchPacket.request = "getFollowers";
        Client.output.writeObject(searchPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
    public static ArrayList<HashMap<String , Object>> getFollowings() throws IOException, ClassNotFoundException {
        Packet searchPacket = new Packet();
        searchPacket.request = "getFollowings";
        Client.output.writeObject(searchPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
}
