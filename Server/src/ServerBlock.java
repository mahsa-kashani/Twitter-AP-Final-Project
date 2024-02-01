import com.clientgui.Packet;

import java.sql.SQLException;
import java.util.*;
import java.io.IOException;
import java.sql.*;
public class ServerBlock {
    public static void block(HashMap<String, String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            DBManager.query("insert into block (blocked,blocker) values (?,?)" , parameters.get("userId"), client.userId);
            DBManager.query("delete from follow where (userId = ? and follower = ?) or (follower = ? and userId = ?)" , parameters.get("userId"), client.userId, parameters.get("userId"), client.userId);
        } catch (SQLException e) {
            result.parameters.put("result" , e.getMessage());
            e.printStackTrace();
        }
        client.output.writeObject(result);
    }
    public static void unblock(HashMap<String, String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            if(DBManager.query("delete from block where blocked = ? and blocker = ?" , parameters.get("userId"), client.userId)==0){
                result.parameters.put("result" , "You can't unblock someone you haven't blocked yet!");
            }
        } catch (SQLException e) {
            result.parameters.put("result" , e.getMessage());
            e.printStackTrace();
        }
        client.output.writeObject(result);
    }
    public static void getBlockList(HashMap<String, String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        SelectHolder holder = DBManager.selectQuery("select blocked from block where blocker = ?" , client.userId);
        ArrayList<HashMap<String, Object>> users = new ArrayList<>();
        try{
            ResultSet table = holder.getTable();
            while(table.next()){
                users.add(DBManager.getUserInfo(table.getString("blocked")));
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }catch(SQLException e){
            result.parameters.put("result" , e.getMessage());
            e.printStackTrace();
        }
        result.maps = users;
        client.output.writeObject(result);
    }

}
