import com.clientgui.Packet;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    static ArrayList<ClientHandler> clients = new ArrayList<>();
    Socket client;
    ObjectInputStream input;
    ObjectOutputStream output;
    String userId;
    public ClientHandler(Socket client) throws IOException {
        clients.add(this);
        this.client = client;
    }

    @Override
    public void run() {
        try{
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
            while(true){
                Packet request = (Packet) input.readObject();
                System.out.println(request.request);
                switch (request.request){
                    case "sign-up":
                        ServerSignUp.signUp(request.parameters , this);
                        break;
                    case "get-tweet":
                        ServerTweets.getTweet(request.parameters, this);
                        break;
                    case "token-check" :
                        Token.checkToken(request.parameters, this);
                        break;
                    case "sign-in":
                        ServerSignIn.signIn(request.parameters, this);
                        break;
                    case "avatar":
                        ServerEditProfile.setAvatar(request.fileData, this);
                        break;
                    case "header" :
                        ServerEditProfile.setHeader(request.fileData, this);
                        break;
                    case "edit-profile-info":
                        ServerEditProfile.editProfileInfo(request.parameters, this);
                        break;
                    case "follow":
                        ServerFollow.follow(request.parameters, this);
                        break;
                    case "search" :
                        ServerSearchUser.searchUser(request.parameters, this);
                        break;
                    case "userInfo" :
                        ServerSearchUser.getUser(request.parameters, this);
                        break;
                    case "unFollow" :
                        ServerFollow.unfollow(request.parameters, this);
                        break;
                    case "getFollowers" :
                        ServerFollow.getFollowers(this);
                        break;
                    case "getFollowings" :
                        ServerFollow.getFollowings(this);
                        break;
                    case "tweet":
                        ServerTweets.tweet(request, this);
                        break;
                    case "like" :
                        ServerTweets.like(request.parameters, this);
                        break;
                    case "unlike" :
                        ServerTweets.unlike(request.parameters, this);
                        break;
                    case "direct":
                        ServerDirects.direct(request.parameters, this);
                        break;
                    case "showDirect":
                        ServerDirects.showDirects(this);
                        break;
                    case "getDirect":
                        ServerDirects.getDirect(request.parameters, this);
                        break;
                    case "getTimeLine":
                        ServerTweets.getTimeLine(this);
                        break;
                    case "searchTweet":
                        ServerTweets.searchTweet(request.parameters, this);
                        break;
                    case "block":
                        ServerBlock.block(request.parameters, this);
                        break;
                    case "unblock":
                        ServerBlock.unblock(request.parameters, this);
                        break;
                    case "getBlockList":
                        ServerBlock.getBlockList(request.parameters, this);
                        break;
                }
            }
        }catch(IOException e){
            try {
                client.close();
                output.close();
                input.close();
                clients.remove(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
