import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.nio.file.*;

public class Client {
    static Socket server;
    static ObjectOutputStream output;
    static ObjectInputStream input;
    static Scanner scanner;
    public static void searchMenu() throws IOException, ClassNotFoundException {
        System.out.println("1. Search user");
        System.out.println("2. Search tweet");
        String choice = scanner.nextLine();
        switch (choice){
            case "1" -> {
                ArrayList<HashMap<String, Object>> users = ClientSearch.search();
                if(users.size() == 0)
                    System.out.println("No User found");
                else
                    System.out.println(users);
            }
            case "2" -> {
                ArrayList<HashMap<String, Object>> tweets = ClientSearch.searchTweet();
                if(tweets.size() == 0)
                    System.out.println("No tweet found");
                else
                    System.out.println(tweets);
            }
        }
        twitterMenu();
    }
    public static void mainMenu() throws IOException, ClassNotFoundException {
        System.out.println("1.sign-up");
        System.out.println("2.sign-in");
        String choice = scanner.nextLine();
        boolean result;
        switch (choice){
            case "1" -> {
                result = ClientSignUp.signUp();
                if(result){
                    twitterMenu();
                }
            }
            case "2" -> {
                result = ClientSignIn.signIn();
                if(result){
                    twitterMenu();
                }
            }
        }
        mainMenu();
    }
    public static void editProfileMenu() throws IOException, ClassNotFoundException {
        System.out.println("0. Back to Twitter");
        System.out.println("1. Change/Set Avatar");
        System.out.println("2. Change/Set Header");
        System.out.println("3. Change/Set Profile Info");

        String choice = scanner.nextLine();
        switch (choice){
            case "0" -> twitterMenu();
            case "1" -> {
                try {
                    if(!ClientEditProfile.setAvatar())
                        System.out.println("Setting Avatar failed, try again");
                    else
                        System.out.println("Avatar has been set successfully");
                } catch (IOException e) {
                    System.out.println("File not found");
                }
            }
            case "2" ->{
                try {
                    if(!ClientEditProfile.setHeader())
                        System.out.println("Setting Header failed, try again");
                    else
                        System.out.println("Header has been set successfully");
                } catch (IOException e) {
                    System.out.println("File not found");
                }
            }
            case "3" -> {
                if(!ClientEditProfile.editProfileInfo())
                    System.out.println("Setting profile Info failed, try again");
                else
                    System.out.println("Profile Info has been set successfully");
            }
        }
        editProfileMenu();
    }
    public static void twitterMenu() throws IOException, ClassNotFoundException {
        System.out.println("0. Sign-Out"); // done
        System.out.println("1. Edit Profile"); // done
        System.out.println("2. Search"); // done
        System.out.println("3. Follow someone"); // done
        System.out.println("4. Unfollow someone"); // done
        System.out.println("5. Get followers"); // done
        System.out.println("6. Get followings"); // going on
        System.out.println("7. Get UserInfo"); // done
        System.out.println("8. Tweet"); // done
        System.out.println("9. Retweet"); //done
        System.out.println("10. Reply"); //done
        System.out.println("11. Like"); // done
        System.out.println("12. Unlike"); // done
        System.out.println("13. Show direct"); // done
        System.out.println("14. Get Direct"); // done
        System.out.println("15. Direct message"); // done
        System.out.println("16. Refresh"); //done
        System.out.println("17. Block");
        System.out.println("18. Unblock");
        System.out.println("19. getBlockList");

        String choice = scanner.nextLine();
        switch (choice){
            case "0" -> mainMenu();
            case "1" -> editProfileMenu();
            case "2" -> searchMenu();
            case "3" -> {
                if(!ClientFollow.follow())
                    System.out.println("Following failed, try again");
                else
                    System.out.println("Following has been done successfully");
            }
            case "4" -> {
                if(!ClientFollow.unFollow())
                    System.out.println("UnFollowing failed, try again");
                else
                    System.out.println("UnFollowing has been done successfully");
            }
            case "5" -> {
                ArrayList<HashMap<String, Object>> result = ClientFollow.getFollowers();
                if(result.size() == 0)
                    System.out.println("No follower found");
                else
                    System.out.println(result);
            }
            case "6" -> {
                ArrayList<HashMap<String, Object>> result = ClientFollow.getFollowings();
                if(result.size() == 0)
                    System.out.println("No following found");
                else
                    System.out.println(result);
            }
            case "7" -> {
                HashMap<String , Object> userInfo = ClientSearch.getInfo();
                System.out.println("User info : \n" + userInfo);
            }
            case "8" -> {
                if(!ClientTweet.tweet())
                    System.out.println("Tweeting failed!");
                else
                    System.out.println("Tweet has been sent successfully");
            }
            case "9" -> {
                if(!ClientTweet.retweet())
                    System.out.println("retweeting failed!");
                else
                    System.out.println("retweet has been sent successfully");
            }
            case "10" -> {
                if(!ClientTweet.reply())
                    System.out.println("replying failed!");
                else
                    System.out.println("reply has been sent successfully");
            }
            case "11" -> {
                if(!ClientTweet.like())
                    System.out.println("liking failed!");
                else
                    System.out.println("like successfully");
            }
            case "12" -> {
                if(!ClientTweet.unlike())
                    System.out.println("unliking failed!");
                else
                    System.out.println("unlike successfully");
            }
            case "13" -> {
                ArrayList<HashMap<String, Object>> result = ClientDirect.showDirects();
                if(result.size() == 0)
                    System.out.println("There are no messages yet");
                else
                    System.out.println(result);
            }
            case "14" -> {
                ArrayList<HashMap<String, Object>> result = ClientDirect.getDirect();
                if(result.size() == 0)
                    System.out.println("There are no messages yet");
                else
                    System.out.println(result);
            }
            case "15" -> {
                if(!ClientDirect.direct())
                    System.out.println("Failed to send direct message");
                else
                    System.out.println("Successfuly sent the direct message");
            }
            case "16" -> {
                ArrayList<HashMap<String, Object>> result = ClientTweet.getTimeLine();
                if(result.size() == 0)
                    System.out.println("There are no tweets yet");
                else
                    System.out.println(result);
            }
            case "17" -> {
                if(!ClientBlock.block())
                    System.out.println("Blocking failed!");
                else
                    System.out.println("Block successfully");
            }
            case "18" -> {
                if(!ClientBlock.unblock())
                    System.out.println("Unblocking failed!");
                else
                    System.out.println("Unblock successfully");
            }
            case "19" -> {
                ArrayList<HashMap<String, Object>> result = ClientBlock.getBlockList();
                if(result.size() == 0)
                    System.out.println("There are no blocked accounts yet");
                else
                    System.out.println(result);
            }
        }
        twitterMenu();
    }
    public static String getToken() throws IOException{
        return Files.readString(Paths.get("token.txt"));
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        server = new Socket("127.0.0.1" , 1500);
        output = new ObjectOutputStream(server.getOutputStream());
        input = new ObjectInputStream(server.getInputStream());
        //System.out.println("connected to server");
        scanner = new Scanner(System.in);
        try{
            String token = getToken();
            Packet tokenPacket = new Packet();
            tokenPacket.request = "token-check";
            tokenPacket.parameters.put("token" , token);
            output.writeObject(tokenPacket);
            Packet result = (Packet)input.readObject();
            if (result.parameters.get("is-valid").equals("true")){
                ArrayList<HashMap<String, Object>> tweets = ClientTweet.getTimeLine();
                if(tweets.size() == 0)
                    System.out.println("There are no tweets yet");
                else
                    System.out.println(tweets);
                twitterMenu();
            }
            else
                mainMenu();
        }catch(IOException ex){
            mainMenu();
        }
    }
}