package com.clientgui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    static Socket server;
    static ObjectOutputStream output;
    static ObjectInputStream input;
    static Scanner scanner;
    static String userId = "";
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("startPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Twitter");
//        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        stage.show();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try{
                    Main.server = new Socket("127.0.0.1" , 1500);
                    Main.output = new ObjectOutputStream(Main.server.getOutputStream());
                    Main.input = new ObjectInputStream(Main.server.getInputStream());
                    Main.scanner = new Scanner(System.in);
                    String token = "";
                    try{
                        token = Main.getToken();
                    }catch (IOException ex) {
                        Main.startSignIn();
                        stage.close();
                        return;
                    }
                    Packet tokenPacket = new Packet();
                    tokenPacket.request = "token-check";
                    tokenPacket.parameters.put("token" , token);
                    Main.output.writeObject(tokenPacket);
                    Packet result = (Packet)Main.input.readObject();
                    if (result.parameters.get("is-valid").equals("true")){
        //                 ArrayList<HashMap<String, Object>> tweets = ClientTweet.getTimeLine();
        //                 if(tweets.size() == 0)
        //                     System.out.println("There are no tweets yet");
        //                 else
        //                     System.out.println(tweets);
                        Main.userId = result.parameters.get("userId");
                        Main.startTwitter();
                        stage.close();
                    }
                    else{
                        Main.startSignIn();
                        stage.close();
                    }
                }catch(IOException ex){
                    ((StartPageController)fxmlLoader.getController()).connectionStatus();
                    ex.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void startTwitter() throws IOException{
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Twitter");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        stage.show();
    }
    public static void startSignUp() throws IOException{
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign up");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((SignUpController)(fxmlLoader.getController())).stage = stage;
        stage.show();
    }

    public static ArrayList<HashMap<String , Object>> getTimeLine() {
        Packet timeLinePacket = new Packet();
        timeLinePacket.request = "getTimeLine";
        try{
            Main.output.writeObject(timeLinePacket);
            Packet result = (Packet) Main.input.readObject();
            if(!result.parameters.get("result").equals("successful"))
                System.out.println("Server error : "+ result.parameters.get("result"));
            return result.maps;
        }
        catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Get TimeLine Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }
        return null;
    }

    public static void startSignIn() throws IOException{
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("signIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign In");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((SignInController)(fxmlLoader.getController())).stage = stage;
        stage.show();
    }

    public static boolean regexChecker(String theRegex , String strToCheck){
        theRegex = " "+theRegex+" ";
        strToCheck = " "+strToCheck+" ";
        Pattern checkRegex = Pattern.compile(theRegex);
        Matcher matcher = checkRegex.matcher(strToCheck);
        while(matcher.find()){
            if(matcher.group().length()!=0){
                return true;
            }
        }
        return false;
    }

    public static boolean unlike(String tweetId) {
        Packet unlikePacket = new Packet();
        unlikePacket.request = "unlike";
        unlikePacket.parameters.put("tweetId" , tweetId);
        try{
            Main.output.writeObject(unlikePacket);
            Packet result = (Packet)Main.input.readObject();
            if (!result.parameters.get("result").equals("successful")) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Unlike Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return false;
            }
            return true;
        }
        catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Unlike Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }
        return false;
    }

    public static boolean like(String tweetId) {
        Packet likePacket = new Packet();
        likePacket.request = "like";
        likePacket.parameters.put("tweetId" , tweetId);
        try{
            Main.output.writeObject(likePacket);
            Packet result = (Packet)Main.input.readObject();
            if (!result.parameters.get("result").equals("successful")) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Like Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return false;
            }
            return true;
        }
         catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Get Tweet Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }
        return false;
    }

     public static HashMap<String, Object> getTweet(String tweetId){
        Packet userInfoPacket = new Packet();
        userInfoPacket.request = "get-tweet";
        userInfoPacket.parameters.put("tweetId" , tweetId);
        try{
            Main.output.writeObject(userInfoPacket);
            Packet result = (Packet) Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("SignIn Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return null;
            }
            return result.maps.get(0);
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Get Tweet Error");
        errorAlert.setContentText("Error : Connection Interrupted!");
        errorAlert.showAndWait();
        Platform.exit();
        }
        return null;
    }

    public static boolean tweet(String message , byte[]file , String retweetTo , String replyTo , String userName) {
        Packet tweetPacket = new Packet();
        tweetPacket.request = "tweet";
        tweetPacket.parameters.put("message" , message);
        try{
            tweetPacket.fileData = file;
            tweetPacket.parameters.put("retweetTo" , retweetTo);
            tweetPacket.parameters.put("replyTo" , replyTo);
            tweetPacket.parameters.put("userName" , userName);
            Main.output.writeObject(tweetPacket);
            Packet result = (Packet)Main.input.readObject();
            if (!result.parameters.get("result").equals("successful")) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Tweet Error");
                errorAlert.setContentText("Server Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return false;
            }
            return true;
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Tweet Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return false;
        }
        return true;
    }
    public static ArrayList<HashMap<String , Object>> searchUser(String search) {
        Packet searchPacket = new Packet();
        searchPacket.request = "search";
        searchPacket.parameters.put("searchQuery" , search);
        try{
            Main.output.writeObject(searchPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                System.out.println("Server error : "+ result.parameters.get("result"));
                return null;
            }
            return result.maps;
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Tweet Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return null;
        }
        return null;
    }

    public static ArrayList<HashMap<String , Object>> searchTweet(String query) {
        Packet searchTweetPacket = new Packet();
        searchTweetPacket.request = "searchTweet";
        searchTweetPacket.parameters.put("query" , query);
        try{
            Main.output.writeObject(searchTweetPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful"))
                System.out.println("Server error : "+ result.parameters.get("result"));
            return result.maps;
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Tweet Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return null;
        }
        return null;
    }

    public static void follow(String userId) {
        Packet followPacket = new Packet();
        followPacket.request = "follow";
        followPacket.parameters.put("userId" , userId);
        try{
            Main.output.writeObject(followPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Follow Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
            }
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Follow Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }
    }

    public static void unfollow(String userId) {
        Packet followPacket = new Packet();
        followPacket.request = "unFollow";
        followPacket.parameters.put("userId" , userId);
        try{
            Main.output.writeObject(followPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Unfollow Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
            }
        }catch(ClassNotFoundException ex){
            Platform.exit();
            ex.printStackTrace();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Unfollow Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }
    }

    public static String getToken() throws IOException{
        return Files.readString(Paths.get("token.txt"));
    }

    public static void main(String[] args) {
        launch();
    }
}