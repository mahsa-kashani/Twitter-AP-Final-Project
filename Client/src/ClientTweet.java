import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientTweet {
    public static boolean tweet(String message , String fileName , String retweetTo , String replyTo) throws IOException, ClassNotFoundException {
        Packet tweetPacket = new Packet();
        tweetPacket.request = "tweet";
        tweetPacket.parameters.put("message" , message);
        tweetPacket.fileData = Files.readAllBytes(Path.of(fileName));
        tweetPacket.parameters.put("retweetTo" , retweetTo);
        tweetPacket.parameters.put("replyTo" , replyTo);
        Client.output.writeObject(tweetPacket);
        Packet result = (Packet)Client.input.readObject();
        if (!result.parameters.get("result").equals("successful")) {
            System.out.println("Server Error : "+result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean tweet() throws IOException, ClassNotFoundException {
        System.out.print("Enter message: ");
        String message = Client.scanner.nextLine();
        if(message.length() > 280){
            System.out.println("The message is larger than 280 characters!");
            return false;
        }
        System.out.print("Enter the fileName of photo: ");
        String photoPath = Client.scanner.nextLine();
        BufferedImage image = ImageIO.read(new File(photoPath));
        if (image.getWidth() != 900 || image.getHeight() != 1600){
            System.out.println("The size of the picture invalid, try a 900*1600 picture");
            return false;
        }
        return tweet(message , photoPath , "" , "");
    }
    public static boolean retweet() throws IOException, ClassNotFoundException {
        System.out.print("Enter message: ");
        String message = Client.scanner.nextLine();
        if(message.length() > 280){
            System.out.println("The message is larger than 280 characters!");
            return false;
        }
        System.out.print("Enter the fileName of photo: ");
        String photoPath = Client.scanner.nextLine();
        BufferedImage image = ImageIO.read(new File(photoPath));
        if (image.getWidth() != 900 || image.getHeight() != 1600){
            System.out.println("The size of the picture invalid, try a 900*1600 picture");
            return false;
        }
        System.out.print("Enter the tweetId to retweet: ");
        String retweetTo = Client.scanner.nextLine();
        return tweet(message , photoPath , retweetTo , "");
    }
    public static boolean reply() throws IOException, ClassNotFoundException {
        System.out.print("Enter message: ");
        String message = Client.scanner.nextLine();
        if(message.length() > 280){
            System.out.println("The message is larger than 280 characters!");
            return false;
        }
        System.out.print("Enter the fileName of photo: ");
        String photoPath = Client.scanner.nextLine();
        BufferedImage image = ImageIO.read(new File(photoPath));
        if (image.getWidth() != 900 || image.getHeight() != 1600){
            System.out.println("The size of the picture invalid, try a 900*1600 picture");
            return false;
        }
        System.out.print("Enter the tweetId to reply: ");
        String replyTo = Client.scanner.nextLine();
        return tweet(message , photoPath , "" , replyTo);
    }
    public static boolean like(String tweetId) throws IOException, ClassNotFoundException {
        Packet likePacket = new Packet();
        likePacket.request = "like";
        likePacket.parameters.put("tweetId" , tweetId);
        Client.output.writeObject(likePacket);
        Packet result = (Packet)Client.input.readObject();
        if (!result.parameters.get("result").equals("successful")) {
            System.out.println("Server Error : "+result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean like() throws IOException, ClassNotFoundException {
        System.out.print("Enter the tweetId to like: ");
        String tweetId = Client.scanner.nextLine();
        return like(tweetId);
    }
    public static boolean unlike(String tweetId) throws IOException, ClassNotFoundException {
        Packet unlikePacket = new Packet();
        unlikePacket.request = "unlike";
        unlikePacket.parameters.put("tweetId" , tweetId);
        Client.output.writeObject(unlikePacket);
        Packet result = (Packet)Client.input.readObject();
        if (!result.parameters.get("result").equals("successful")) {
            System.out.println("Server Error : "+result.parameters.get("result"));
            return false;
        }
        return true;
    }
    public static boolean unlike() throws IOException, ClassNotFoundException {
        System.out.print("Enter the tweetId to unlike: ");
        String tweetId = Client.scanner.nextLine();
        return unlike(tweetId);
    }
    
    public static ArrayList<HashMap<String , Object>> getTimeLine() throws IOException, ClassNotFoundException {
        Packet timeLinePacket = new Packet();
        timeLinePacket.request = "getTimeLine";
        Client.output.writeObject(timeLinePacket);
        Packet result = (Packet)Client.input.readObject();
        if(!result.parameters.get("result").equals("successful"))
            System.out.println("Server error : "+ result.parameters.get("result"));
        return result.maps;
    }
}
