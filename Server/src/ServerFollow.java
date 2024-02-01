import com.clientgui.Packet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.sql.*;
public class ServerFollow {
    public static void follow(HashMap<String ,String> parameters, ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            System.out.println(parameters.get("userId") + " " + client.userId);
            if(DBManager.query("insert ignore into follow (userId, follower) select ?,? where ? not in (select blocker from block where blocked = ?) and ? not in (select blocked from block where blocker = ?)" , parameters.get("userId"), client.userId, client.userId, parameters.get("userId"), client.userId, parameters.get("userId"))==0){
                result.parameters.put("result" , "you can't follow someone that you have blocked or you have been blocked by");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }
    public static void unfollow(HashMap<String ,String> parameters, ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        try {
            DBManager.query("delete from follow where userId = ? and follower = ?" , parameters.get("userId"), client.userId);
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static void getFollowers(ClientHandler client) throws IOException {
        SelectHolder holder = DBManager.selectQuery("select * from follow where userId = ?",
            client.userId
        );
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        ResultSet table = holder.getTable();
        ArrayList<HashMap<String, Object>> users = new ArrayList<>();
        try{
            while(table.next()){
                HashMap<String , Object> userInfo = DBManager.getUserInfo(table.getString("follower"));
                users.add(userInfo);
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }
        catch(SQLException e){
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        result.maps = users;
        client.output.writeObject(result);
    }

    public static void getFollowings(ClientHandler client) throws IOException {
            SelectHolder holder = DBManager.selectQuery("select * from follow where follower = ?",
                client.userId
            );
            Packet result = new Packet();
            result.parameters.put("result" , "successful");
            ResultSet table = holder.getTable();
            ArrayList<HashMap<String, Object>> users = new ArrayList<>();
            try{
                while(table.next()){
                    System.out.println(table.getString("userId"));
                    HashMap<String , Object> userInfo = DBManager.getUserInfo(table.getString("userId"));
                    users.add(userInfo);
                }
                holder.getStatement().close();
                holder.getConnection().close();
            }
            catch(SQLException e){
                e.printStackTrace();
                result.parameters.put("result" , e.getMessage());
            }
            result.maps = users;
            client.output.writeObject(result);
        }
}
