import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class DBManager {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://LocalHost:3306/twitter", "root", "Ponanaa7");
    }
    public synchronized static int query (String query , String ...inputs) throws SQLException{
        try(Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            for (int i = 1; i <= inputs.length; i++) {
                    statement.setString(i, inputs[i-1]);
            }
            return statement.executeUpdate();
        }
        catch (SQLException ex){
            throw ex;
        }
    }
    public synchronized static void tweet (String userId, String message, byte[] photo, String retweetTo, String replyTo , String userName) throws SQLException, IOException{
        try(Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("insert into tweet (userId,message,photo,date,retweetTo,replyTo,userName) values (?,?,?,?,?,?,?)");
        ) {
            if(retweetTo != null && !retweetTo.equals("")){
                DBManager.query("update tweet set retweetCount = retweetCount+1 where tweetId = ?", retweetTo);
            }
            if(replyTo != null && !replyTo.equals("")){
                DBManager.query("update tweet set replyCount = replyCount+1 where tweetId = ?", replyTo);
            }
            Files.write(Path.of("temp"), Objects.requireNonNullElseGet(photo, () -> new byte[0]));
            statement.setBlob(3, new FileInputStream("temp"));
            statement.setString(1, userId);
            statement.setString(2, message);
            statement.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            statement.setString(5, retweetTo);
            statement.setString(6, replyTo);
            statement.setString(7, userName);
            statement.executeUpdate();
        }
        catch (SQLException ex){
            throw ex;
        }
    }

    public synchronized static void setAvatar (String userId , byte[] fileData) throws SQLException{
        try(Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("update users set avatar = ? where userId = ?")
        ) {
            Files.write(Path.of("temp"), fileData);
            statement.setBlob(1, new FileInputStream(new File("temp")));
            statement.setString(2, userId);
            statement.executeUpdate();
        }
        catch (SQLException ex){
            throw ex;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static void setHeader (String userId , byte[] fileData) throws SQLException{
        try(Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("update users set header = ? where userId = ?")
        ) {
            Files.write(Path.of("temp"), fileData);
            statement.setBlob(1, new FileInputStream(new File("temp")));
            statement.setString(2, userId);
            statement.executeUpdate();
        }
        catch (SQLException ex){
            throw ex;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static SelectHolder selectQuery(String query, String ...inputs){
        try{
            Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 1; i <= inputs.length; i++) {
                statement.setString(i, inputs[i-1]);
            }
            return new SelectHolder(connection, statement, statement.executeQuery());
        }
        catch(SQLException ex){
            ex.printStackTrace(); // exception
        }
        return null;
    }

    public synchronized static HashMap<String, Object> getUserInfo(String userId) throws SQLException{
        SelectHolder holder = DBManager.selectQuery("select * from users where userId = ?",
            userId
        );
        ResultSet table = holder.getTable();
        HashMap<String, Object> userInfo = new HashMap<>();
        while(table.next()){
            userInfo.put("userId" , table.getString("userId"));
            userInfo.put("userName" , table.getString("userName"));
            userInfo.put("userLastName" , table.getString("userLastName"));
            userInfo.put("is-followed" , ServerSearchUser.isFollowed(table.getString("userId"), userId));
            if (table.getBlob("avatar") != null)
                userInfo.put("avatar" , table.getBlob("avatar").getBytes(1,(int)table.getBlob("avatar").length()));
            }
        holder.getStatement().close();
        holder.getConnection().close();
        return userInfo;
    }

    public static boolean isLiked(String tweetId, String userId) throws SQLException{
        SelectHolder holder = DBManager.selectQuery("select * from likes where tweetId = ? and userId = ?", tweetId, userId);
        boolean found = false;
        if(holder.getTable().next())
            found = true;
        holder.getStatement().close();
        holder.getConnection().close();
        return found;
    }

    public synchronized static void direct (String sender, String message, String receiver) throws SQLException, IOException{
        try(Connection connection = DBManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("insert into direct (sender,receiver,message,date) values (?,?,?,?)");
        ) {statement.setString(1, sender);
            statement.setString(2, receiver);
            statement.setString(3, message);
            statement.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            statement.executeUpdate();
        }
        catch (SQLException ex){
            throw ex;
        }
    }
}
