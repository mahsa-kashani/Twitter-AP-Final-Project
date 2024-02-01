import com.clientgui.Packet;

import java.sql.SQLException;
import java.sql.*;
import java.util.*;
import java.io.IOException;
public class ServerSearchUser {

    public static boolean isFollowed(String userId, String follower) throws SQLException {
        SelectHolder holder = DBManager.selectQuery("select * from follow where userId = ? and follower = ?", userId, follower);
        ResultSet table = holder.getTable();
        boolean result = table.next();
        holder.getStatement().close();
        holder.getConnection().close();
        return result;
    }

    public static void searchUser(HashMap<String, String> parameters , ClientHandler client) throws IOException {
        String query = "%"+parameters.get("searchQuery")+"%";
        SelectHolder holder = DBManager.selectQuery("select * from users where (userId LIKE ? or userName + ' '+ userLastName LIKE ? or userName LIKE ? or userLastName LIKE ?) and userId not in (select blocker from block where blocked = ?)",
            query, query, query, query , client.userId
        );
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        ResultSet table = holder.getTable();
        ArrayList<HashMap<String, Object>> users = new ArrayList<>();
        try{
            while(table.next()){
                HashMap<String , Object> userInfo = new HashMap<>();
                userInfo.put("userId" , table.getString("userId"));
                userInfo.put("userName" , table.getString("userName"));
                userInfo.put("userLastName" , table.getString("userLastName"));
                userInfo.put("is-followed" , isFollowed(table.getString("userId"), client.userId));
                if (table.getBlob("avatar") != null)
                    userInfo.put("avatar" , table.getBlob("avatar").getBytes(1,(int)table.getBlob("avatar").length()));
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
    public static boolean isBlocked(String blocker, String blocked) throws SQLException{
        SelectHolder holder = DBManager.selectQuery("select * from block where (blocked = ? and blocker = ?) or (blocked = ? and blocker = ?)" , blocked , blocker, blocker , blocked);
        ResultSet table = holder.getTable();
        boolean result = false;
        if(table.next()){
            result = true;
        }
        holder.getStatement().close();
        holder.getConnection().close();
        return result;
    }
    public static void getUser(HashMap<String, String> parameters , ClientHandler client) throws IOException {
        String userId = parameters.get("userId");
        SelectHolder holder = DBManager.selectQuery("select * from users where userId = ? and userId not in (select blocker from block where blocked = ?)",
            userId, client.userId
        );
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        ResultSet table = holder.getTable();
        HashMap<String, Object> userInfo = new HashMap<>();
        try{
            boolean found = false;
            while(table.next()){
                found = true;
                userInfo.put("userId" , table.getString("userId"));
                userInfo.put("userName" , table.getString("userName"));
                userInfo.put("userLastName" , table.getString("userLastName"));
                userInfo.put("email" , table.getString("email"));
                userInfo.put("phoneNumber" , table.getString("phoneNumber"));
                userInfo.put("country" , table.getString("country"));
                userInfo.put("birthDate", table.getString("birthDate"));
                userInfo.put("signUpDate" , table.getString("signUpDate"));
                userInfo.put("bio" , table.getString("bio"));
                userInfo.put("location" , table.getString("location"));
                userInfo.put("website" , table.getString("website"));
                userInfo.put("is-followed" , isFollowed(table.getString("userId"), client.userId));
                userInfo.put("isBlocked" , isBlocked(table.getString("userId") , client.userId));
                userInfo.put("tweets" , ServerTweets.getTweets(userId,client));
                if (table.getBlob("avatar") != null)
                    userInfo.put("avatar" , table.getBlob("avatar").getBytes(1,(int)table.getBlob("avatar").length()));
                if (table.getBlob("header") != null)
                    userInfo.put("header" , table.getBlob("header").getBytes(1,(int)table.getBlob("header").length()));
            }
            if(!found){
                result.parameters.put("result" , "User not found, maybe you are blocked");
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }
        catch(SQLException e){
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        result.maps = new ArrayList<>();
        result.maps.add(userInfo);
        System.out.println(result.parameters);
        System.out.println(result.maps.get(0));
        client.output.writeObject(result);
    }
}
