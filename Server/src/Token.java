import com.clientgui.Packet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.lang.String;
import java.util.Random;
import java.lang.StringBuilder;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import java.io.*;
public class Token implements Serializable{
    static ConcurrentHashMap<String , Token> tokenObjects = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String , String> tokens = new ConcurrentHashMap<>(); // token => user
    private Date date;
    public Token(){
        this.date = new Date();
    }
    public boolean isExpired(){
        Date now = new Date();
        return now.getTime() - this.date.getTime() >= 24*60*60*1000;
    }
    public static void addToken(String token, String userId){
        Token t = new Token();
        tokenObjects.put(token, t);
        tokens.put(token , userId);
        try{
            saveTokens();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public synchronized static void removeToken(String token){
            tokens.remove(token);
        tokenObjects.remove(token);
    }
    public static boolean isTokenValid(String token){
        return tokens.containsKey(token);
    }
    public static boolean isExpired(String token){
        return tokenObjects.get(token).isExpired();
    }
    public static void checkToken(HashMap<String, String> parameters, ClientHandler client) throws IOException {
        String token = parameters.get("token");
        Packet result = new Packet();
        result.request = "token";
        result.parameters.put("is-valid" , "true");
        if (!Token.isTokenValid(token) || Token.isExpired(token)){
            result.parameters.put("is-valid" , "false");
        }
        if (result.parameters.get("is-valid").equals("true")){
            client.userId = Token.tokens.get(token);
            result.parameters.put("userId" , client.userId);
        }
        client.output.writeObject(result); 
    }
    static String[] validCharacters = new String[] {"a" , "b" , "c" , "d" , "e" , "f" , "g" , "h", "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" , "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" , "6" ,"7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" , "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" ,"S" , "T" , "U" , "V" , "W" , "X" , "Y" , "Z"};
    public static String generateNewToken(String userId){
        Random random = new Random(LocalDateTime.now().getSecond());
        StringBuilder result = new StringBuilder("");
        for(int i = 0;i<20;i++){
            result.append(validCharacters[(((Math.abs(random.nextInt()))%validCharacters.length))]);
        }
        if (!Token.isTokenValid(result.toString())){
            Token.addToken(result.toString(), userId);
            return result.toString();
        }
        return generateNewToken(userId);
    }
    public static void saveTokens() throws IOException {
        ObjectOutputStream serverTokenOut = new ObjectOutputStream(new FileOutputStream("tokens.db"));
        serverTokenOut.writeObject(Token.tokens);
        ObjectOutputStream serverTokenOutObjects = new ObjectOutputStream(new FileOutputStream("tokenObjects.db"));
        serverTokenOutObjects.writeObject(Token.tokenObjects);
    }
    public static void loadTokens() throws ClassNotFoundException {
        try{
            ObjectInputStream serverTokenIn = new ObjectInputStream(new FileInputStream("tokens.db"));
            Token.tokens = (ConcurrentHashMap<String, String>) serverTokenIn.readObject();
            ObjectInputStream serverTokenInObject = new ObjectInputStream(new FileInputStream("tokenObjects.db"));
            Token.tokenObjects = (ConcurrentHashMap<String, Token>) serverTokenInObject.readObject();

        }catch(IOException ex){
            System.out.println("Token files not found, creating them ...");
        }
    }
}