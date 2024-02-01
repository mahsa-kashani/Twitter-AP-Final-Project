import java.io.IOException;
import java.util.*;

public class ClientSearch {
    public static ArrayList<HashMap<String , Object>> search(String search) throws IOException, ClassNotFoundException {
        Packet searchPacket = new Packet();
        searchPacket.request = "search";
        searchPacket.parameters.put("searchQuery" , search);
        Client.output.writeObject(searchPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
    public static ArrayList<HashMap<String , Object>> search() throws IOException, ClassNotFoundException {
        System.out.print("Enter the Query to search : ");
        return search(Client.scanner.nextLine());
    }
    public static HashMap<String , Object> getInfo(String userId) throws IOException, ClassNotFoundException {
        Packet userInfoPacket = new Packet();
        userInfoPacket.request = "userInfo";
        userInfoPacket.parameters.put("userId" , userId);
        Client.output.writeObject(userInfoPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps.get(0);
    }
    public static HashMap<String , Object> getInfo() throws IOException, ClassNotFoundException {
        System.out.print("Enter userId to search: ");
        return getInfo(Client.scanner.nextLine());
    }
    public static ArrayList<HashMap<String , Object>> searchTweet(String query) throws IOException, ClassNotFoundException {
        Packet searchTweetPacket = new Packet();
        searchTweetPacket.request = "searchTweet";
        searchTweetPacket.parameters.put("query" , query);
        Client.output.writeObject(searchTweetPacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
    public static ArrayList<HashMap<String , Object>> searchTweet() throws IOException, ClassNotFoundException {
        System.out.print("Enter query to search: ");
        return searchTweet(Client.scanner.nextLine());
    }
}
