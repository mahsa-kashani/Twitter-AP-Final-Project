import com.clientgui.Packet;

import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
public class ServerSignUp {
    public static void signUp(HashMap<String , String> values , ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.request = "sign-up";
        result.parameters.put("result" , "successful");
        String userId = values.get("userId");
        String name = values.get("name");
        String lastName = values.get("lastName");
        String email = values.get("email");
        String phoneNumber = values.get("phoneNumber");
        String password = values.get("password");
        String country = values.get("country");
        String birthDate = values.get("birthDate");
        String signUpDate = new Date().toString();
        try {
            DBManager.query("INSERT INTO users (" +
                                                    "userId," +
                    "userName," +
                                                    "userLastName," +
                                                    "email," +
                                                    "phoneNumber," +
                                                    "userPassword," +
                                                    "country," +
                                                    "birthDate," +
                                                    "signUpDate)  " +
                                                    "VALUES (?,?,?,?,?,?,?,?,?)",userId, name, lastName, email, phoneNumber, password, country, birthDate, signUpDate);
        } catch (SQLException e) {
            if (e instanceof java.sql.SQLIntegrityConstraintViolationException){
                if (e.getMessage().contains("Duplicate")){
                    if (e.getMessage().contains("users.PRIMARY")){
                        result.parameters.put("result" , "duplicate-id");
                    }
                    else if(e.getMessage().contains("users.email")){
                        result.parameters.put("result" , "duplicate-email");
                    }
                    else if(e.getMessage().contains("users.phoneNumber")){
                        result.parameters.put("result" , "duplicate-phoneNumber");
                    }
                }
            }
            e.printStackTrace();
        }
        if (result.parameters.get("result").equals("successful")){
            result.parameters.put("token" , Token.generateNewToken(userId));
            client.userId = userId;
        }
        client.output.writeObject(result);
    }

}