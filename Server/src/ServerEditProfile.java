import com.clientgui.Packet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ServerEditProfile {

    public static void setAvatar(byte[] avatar,ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            DBManager.setAvatar(client.userId, avatar);
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }
    public static void setHeader(byte[] avatar,ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            DBManager.setHeader(client.userId, avatar);
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static void editProfileInfo(HashMap<String,String> parameters , ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        String bio = parameters.get("bio");
        String location = parameters.get("location");
        String website = parameters.get("website");
        try {
            DBManager.query("update users set bio = ?, location = ?,website = ? where userId = ?" , bio, location, website, client.userId);
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

}
