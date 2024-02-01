import com.clientgui.Packet;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ServerTweets {
    public static void tweet(Packet parameter, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");

        try {
            DBManager.tweet(client.userId, parameter.parameters.get("message"), parameter.fileData, parameter.parameters.get("retweetTo"), parameter.parameters.get("replyTo") , parameter.parameters.get("userName"));
        } catch (SQLException e) {
            e.printStackTrace();
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static void getTweet(HashMap<String,String> parameters, ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        SelectHolder holder = DBManager.selectQuery("select a.tweetId, a.userId, a.message, a.date, a.retweetTo, a.replyTo, a.retweetCount,a.photo, a.replyCount, a.likeCount, a.userName , b.avatar as profilePhoto from tweet a,users b where a.tweetId = ? and a.userId = b.userId", parameters.get("tweetId"));
        ResultSet table = holder.getTable();
        ArrayList<HashMap<String, Object>> tweets = new ArrayList<>();
        try{
            while(table.next()){
                HashMap<String , Object> tweet = new HashMap<>();
                tweet.put("userId" , table.getString("userId"));
                tweet.put("tweetId" , table.getString("tweetId"));
                tweet.put("message" , table.getString("message"));
                tweet.put("date" , table.getTimestamp("date"));
                tweet.put("retweetTo" , table.getString("retweetTo"));
                tweet.put("replyTo" , table.getString("replyTo"));
                tweet.put("retweetCount" , table.getString("retweetCount"));
                tweet.put("replyCount" , table.getString("replyCount"));
                tweet.put("likeCount" , table.getString("likeCount"));
                tweet.put("userName" , table.getString("userName"));
                tweet.put("likedByYou" , "false");
                if (DBManager.isLiked(table.getString("tweetId") , client.userId))
                    tweet.put("likedByYou" , "true");
                if (table.getBlob("profilePhoto") != null)
                    tweet.put("profilePhoto" , table.getBlob("profilePhoto").getBytes(1,(int)table.getBlob("profilePhoto").length()));
                if (table.getBlob("photo") != null)
                    tweet.put("photo" , table.getBlob("photo").getBytes(1,(int)table.getBlob("photo").length()));
                tweets.add(tweet);
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        result.maps = tweets;
        System.out.println(parameters.get("tweetId"));
        System.out.println((result.maps));
        client.output.writeObject(result);
    }

    public static void like(HashMap<String, String> parameters, ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
         try {
            if (!DBManager.isLiked(parameters.get("tweetId") , client.userId)){
                DBManager.query("insert into likes (userId,tweetId) values (?,?)" , client.userId, parameters.get("tweetId"));
                DBManager.query("update tweet set likeCount = likeCount+1 where tweetId = ?", parameters.get("tweetId"));
            }else
                 unlike(parameters , client);
        }
        catch (SQLException e) {
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static void unlike(HashMap<String, String> parameters, ClientHandler client) throws IOException {
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
         try {
            if (DBManager.isLiked(parameters.get("tweetId") , client.userId)){
                DBManager.query("delete from likes where userId = ? and tweetId = ?" , client.userId, parameters.get("tweetId"));
                DBManager.query("update tweet set likeCount = likeCount-1 where tweetId = ?", parameters.get("tweetId"));
            }else
                 like(parameters , client);
        }
        catch (SQLException e) {
            result.parameters.put("result" , e.getMessage());
        }
        client.output.writeObject(result);
    }

    public static ArrayList<HashMap<String , Object>> getTweets(String userId,ClientHandler client){
        SelectHolder holder = DBManager.selectQuery("select a.tweetId, a.userId, a.message, a.date, a.retweetTo, a.replyTo,a.photo, a.retweetCount, a.replyCount, a.likeCount, a.userName , b.avatar as profilePhoto from tweet a,users b where a.userId = ? and a.userId = b.userId order by date desc", userId);
        ResultSet table = holder.getTable();
        ArrayList<HashMap<String, Object>> tweets = new ArrayList<>();
        try{
            while(table.next()){
                HashMap<String , Object> tweet = new HashMap<>();
                tweet.put("userId" , table.getString("userId"));
                tweet.put("tweetId" , table.getString("tweetId"));
                tweet.put("message" , table.getString("message"));
                tweet.put("date" , table.getTimestamp("date"));
                tweet.put("retweetTo" , table.getString("retweetTo"));
                tweet.put("replyTo" , table.getString("replyTo"));
                tweet.put("retweetCount" , table.getString("retweetCount"));
                tweet.put("replyCount" , table.getString("replyCount"));
                tweet.put("likeCount" , table.getString("likeCount"));
                tweet.put("userName" , table.getString("userName"));
                tweet.put("likedByYou" , "false");
                if (DBManager.isLiked(table.getString("tweetId") , client.userId))
                    tweet.put("likedByYou" , "true");
                if (table.getBlob("profilePhoto") != null)
                    tweet.put("profilePhoto" , table.getBlob("profilePhoto").getBytes(1,(int)table.getBlob("profilePhoto").length()));
                if (table.getBlob("photo") != null)
                    tweet.put("photo" , table.getBlob("photo").getBytes(1,(int)table.getBlob("photo").length()));
                tweets.add(tweet);
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return tweets;
    }
    public static void getTimeLine(ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        SelectHolder holder = DBManager.selectQuery("select a.tweetId, a.userId, a.message, a.date, a.retweetTo, a.replyTo, a.retweetCount,a.photo, a.replyCount, a.likeCount, a.userName , b.avatar as profilePhoto from tweet a, users b where a.userId = b.userId and (a.userId in (select userId from follow where follower = ?) or a.likeCount > 10) and a.userId not in (select blocker from block where blocked = ?) order by a.date desc",client.userId, client.userId);
        ResultSet table = holder.getTable();
        ArrayList<HashMap<String, Object>> tweets = new ArrayList<>();
        try{
            while(table.next()){
                HashMap<String , Object> tweet = new HashMap<>();
                tweet.put("userId" , table.getString("userId"));
                tweet.put("tweetId" , table.getString("tweetId"));
                tweet.put("userId" , table.getString("userId"));
                tweet.put("message" , table.getString("message"));
                tweet.put("date" , table.getTimestamp("date"));
                tweet.put("retweetTo" , table.getString("retweetTo"));
                tweet.put("replyTo" , table.getString("replyTo"));
                tweet.put("retweetCount" , table.getString("retweetCount"));
                tweet.put("replyCount" , table.getString("replyCount"));
                tweet.put("likeCount" , table.getString("likeCount"));
                tweet.put("userName" , table.getString("userName"));
                tweet.put("likedByYou" , "false");
                if (DBManager.isLiked(table.getString("tweetId") , client.userId))
                    tweet.put("likedByYou" , "true");
                if (table.getBlob("profilePhoto") != null)
                    tweet.put("profilePhoto" , table.getBlob("profilePhoto").getBytes(1,(int)table.getBlob("profilePhoto").length()));
                if (table.getBlob("photo") != null)
                    tweet.put("photo" , table.getBlob("photo").getBytes(1,(int)table.getBlob("photo").length()));
                tweets.add(tweet);
            }
            result.maps = tweets;
            holder.getStatement().close();
            holder.getConnection().close();
        }catch(SQLException ex){
            result.parameters.put("result" , ex.getMessage());
            ex.printStackTrace();
        }
         client.output.writeObject(result);
    }

    public static void searchTweet(HashMap<String,String> parameters , ClientHandler client) throws IOException{
        Packet result = new Packet();
        result.parameters.put("result" , "successful");
        String query = "%"+parameters.get("query")+"%";
        SelectHolder holder = DBManager.selectQuery("select a.tweetId, a.userId, a.message, a.date, a.retweetTo, a.replyTo, a.retweetCount,a.photo, a.replyCount, a.likeCount, a.userName , b.avatar as profilePhoto from tweet a,users b where a.message like ? and a.userId = b.userId" , query);
        ArrayList<HashMap<String , Object>> tweets = new ArrayList<>();
        try{
            ResultSet table = holder.getTable();
            while(table.next()){
                 HashMap<String , Object> tweet = new HashMap<>();
                tweet.put("userId" , table.getString("userId"));
                tweet.put("tweetId" , table.getString("tweetId"));
                tweet.put("userId" , table.getString("userId"));
                tweet.put("message" , table.getString("message"));
                tweet.put("date" , table.getTimestamp("date"));
                tweet.put("retweetTo" , table.getString("retweetTo"));
                tweet.put("replyTo" , table.getString("replyTo"));
                tweet.put("retweetCount" , table.getString("retweetCount"));
                tweet.put("replyCount" , table.getString("replyCount"));
                tweet.put("likeCount" , table.getString("likeCount"));
                tweet.put("userName" , table.getString("userName"));
                tweet.put("likedByYou" , "false");
                if (DBManager.isLiked(parameters.get("tweetId") , client.userId))
                    tweet.put("likedByYou" , "true");
                 if (table.getBlob("profilePhoto") != null)
                    tweet.put("profilePhoto" , table.getBlob("profilePhoto").getBytes(1,(int)table.getBlob("profilePhoto").length()));
                if (table.getBlob("photo") != null)
                    tweet.put("photo" , table.getBlob("photo").getBytes(1,(int)table.getBlob("photo").length()));
                tweets.add(tweet);
            }
            holder.getStatement().close();
            holder.getConnection().close();
        }catch(SQLException ex){
            result.parameters.put("result" , ex.getMessage());
            ex.printStackTrace();
        }
        result.maps = tweets;
        client.output.writeObject(result);
    }

}
