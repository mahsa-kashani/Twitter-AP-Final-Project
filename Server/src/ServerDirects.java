import com.clientgui.Packet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.sql.*;

public class ServerDirects {
    public static void direct(HashMap<String , String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        String message = parameters.get("message");
        String reciever = parameters.get("receiverId");
        String sender = client.userId;
        try {
            DBManager.direct(sender,message,reciever);
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }
    public static void showDirects(ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            DBManager.query("create table temp (SELECT sender as userId , date from direct where receiver = ? union select receiver as userId , date from direct where sender = ? order by date desc)" , client.userId, client.userId);
            SelectHolder holder = DBManager.selectQuery(" select distinct userId from temp");
            DBManager.query("drop table temp");
            ResultSet table = holder.getTable();
            ArrayList<HashMap<String, Object>> users = new ArrayList<>();
            while(table.next()){
                HashMap<String, Object> user = DBManager.getUserInfo(table.getString("userId"));
                users.add(user);
            }
            result.maps = users;
            holder.getStatement().close();
            holder.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static void getDirect(HashMap<String, String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        String userId = parameters.get("userId");
        SelectHolder holder = DBManager.selectQuery("select * from direct where (sender = ? and receiver = ?) or (receiver = ? and sender = ?) order by date desc", userId, client.userId, userId, client.userId);
        ArrayList<HashMap<String,Object>> messages = new ArrayList<>();
        try{
            ResultSet table = holder.getTable();
            while(table.next()){
                HashMap<String, Object> message = new HashMap<>();
                message.put("sender" , table.getString("sender"));
                message.put("receiver" , table.getString("receiver"));
                message.put("message" , table.getString("message"));
                message.put("date" , table.getTimestamp("date"));
                messages.add(message);
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }
        catch (SQLException ex){
            result.parameters.put("result" , ex.getMessage());
        }
        result.maps = messages;
        client.output.writeObject(result);
    }
}
