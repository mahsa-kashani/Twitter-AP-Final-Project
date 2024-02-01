import com.clientgui.Packet;

import java.io.IOException;
import java.util.*;
import java.sql.*;
public class ServerSignIn {
    public static void signIn(HashMap<String, String> parameters, ClientHandler client) throws IOException{
        Packet result= new Packet();
        String userId = parameters.get("userId");
        String password = parameters.get("password");
        String query = "SELECT * FROM users where userId = ?";
        SelectHolder holder = DBManager.selectQuery(query , userId);
        ResultSet table = holder.getTable();
        try{
            result.parameters.put("result" , "user-not-found");
            while(table.next()){
                if(table.getString("userPassword").equals(password)){
                    for(String token : Token.tokens.keySet()){
                        if(Token.tokens.get(token).equals(userId)){
                            Token.removeToken(token);
                        }
                    }
                    String newToken = Token.generateNewToken(userId);
                    result.parameters.put("token" , newToken);
                    result.parameters.put("result" , "successful");
                    client.userId = parameters.get("userId");
                }
                else{
                    result.parameters.put("result" , "wrong-password");
                }
            }
            holder.getConnection().close();
            holder.getStatement().close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        client.output.writeObject(result);
    }
}
