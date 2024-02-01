package com.clientgui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    Tab homePane, searchPane , directPane , profilePane, tweetPane;
    @FXML
    TabPane mainTabPane;
    @FXML
    ImageView header,profilePhoto;
    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    Label name , userId , bio , location , website , joinDate , followingNumber , followersNumber;
    @FXML
    VBox profileVbox,homeVbox, searchTweetVbox, searchUserVbox;
    @FXML
    TextField searchQuery;
    @FXML
    Button tweetButton;

    ArrayList<HashMap<String , Object>> following = null , followers = null;

    public static ArrayList<HashMap<String , Object>> getUser(String userId) throws IOException, ClassNotFoundException {
        Packet userInfoPacket = new Packet();
        userInfoPacket.request = "userInfo";
        userInfoPacket.parameters.put("userId" , userId);
        Main.output.writeObject(userInfoPacket);
        Packet result = (Packet) Main.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("User Search Error");
            errorAlert.setContentText("Error : "+result.parameters.get("result"));
            errorAlert.showAndWait();
            return null;
        }
        return result.maps;
    }
    public ArrayList<HashMap<String , Object>> getFollowings() throws IOException, ClassNotFoundException {
        Packet searchPacket = new Packet();
        searchPacket.request = "getFollowings";
        Packet result;
        try{
            Main.output.writeObject(searchPacket);
            result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Get Following Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return null;
            }
            return result.maps;
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Get Following Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return null;
        }catch (ClassNotFoundException e){
            Platform.exit();
            return null;
        }
    }
    public ArrayList<HashMap<String , Object>> getFollowers() throws IOException, ClassNotFoundException {
        Packet searchPacket = new Packet();
        searchPacket.request = "getFollowers";
        Packet result;
        try{
            Main.output.writeObject(searchPacket);
            result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Get Following Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return null;
            }
            return result.maps;
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Get Following Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return null;
        }catch (ClassNotFoundException e){
            Platform.exit();
            return null;
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tooltip t = new Tooltip("Home");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        homePane.setTooltip(t);
        t = new Tooltip("Search");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        searchPane.setTooltip(t);
        t = new Tooltip("Messages");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        directPane.setTooltip(t);
        t = new Tooltip("Profile");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        profilePane.setTooltip(t);
        t = new Tooltip("New tweet");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        tweetButton.setTooltip(t);
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if(newTab == homePane){
                refresh();
            }
        });
        refresh();
    }

    public void refresh(){
        //profile
        HashMap<String , Object> userInfo = null;
        try {
            if(getUser(Main.userId) == null){
                Platform.exit();
                return;
            }
            userInfo = getUser(Main.userId).get(0);
        } catch (IOException e){
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignIn Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            Platform.exit();
            e.printStackTrace();
            return;
        }
        try {
            header.setImage(new Image(new FileInputStream("ClientGUI/defaultHeader.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if(userInfo.get("header") != null)
            header.setImage(new Image(new ByteArrayInputStream((byte[]) userInfo.get("header"))));
        System.out.println("1p");
        setProfilePhoto(profilePhoto, userInfo.get("avatar"), 150,150);
        System.out.println("1");

        name.setText(userInfo.get("userName") + " " + userInfo.get("userLastName"));
        userId.setText("@"+userInfo.get("userId"));
        if(!userInfo.get("bio").equals(""))
            bio.setText((String) userInfo.get("bio"));
        if(!userInfo.get("location").equals(""))
            location.setText(userInfo.get("location")+"⟟");
        if(!userInfo.get("website").equals(""))
            website.setText(userInfo.get("website")+"\uD83D\uDD17");
        joinDate.setText("Joined on " + userInfo.get("signUpDate")+"\uD83D\uDCC6");
        try {
            if(getFollowings() == null || getFollowers() == null){
                Platform.exit();
                return;
            }
            following = getFollowings();
            followers = getFollowers();
            followingNumber.setText(following.size()+ " Following");
            followersNumber.setText(followers.size()+ " Followers");
        } catch (IOException e){
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignIn Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
            return;
        }catch (ClassNotFoundException e){
            Platform.exit();
            e.printStackTrace();
            return;
        }
        setTweets(profileVbox, (ArrayList<HashMap<String , Object>>)(userInfo.get("tweets")),false);
        ArrayList<HashMap<String , Object>> timeline = Main.getTimeLine();
        setTweets(homeVbox , timeline, true);

    }

    public void startRetweet(String retweetTo , String userName) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("retweetPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Retweet");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((RetweetPageController)(fxmlLoader.getController())).stage = stage;
        ((RetweetPageController)(fxmlLoader.getController())).retweetTo = retweetTo;
        ((RetweetPageController)(fxmlLoader.getController())).userName = userName;
        ((RetweetPageController)(fxmlLoader.getController())).mainPage = this;
        stage.show();
    }
    public void startReply(String replyTo , String userName) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("replyPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Reply");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((ReplyPageController)(fxmlLoader.getController())).stage = stage;
        ((ReplyPageController)(fxmlLoader.getController())).replyTo = replyTo;
        ((ReplyPageController)(fxmlLoader.getController())).userName = userName;
        ((ReplyPageController)(fxmlLoader.getController())).mainPage = this;
        stage.show();
    }

    public HBox setTweet(HashMap<String,Object> tweet){
        ImageView tweetImage = new ImageView();
        HBox tweetImageHBox = new HBox(tweetImage);
        tweetImageHBox.setAlignment(Pos.CENTER);
        if(tweet.get("photo") != null)
            tweetImage.setImage(new Image(new ByteArrayInputStream((byte[]) tweet.get("photo"))));
        tweetImage.setFitWidth(400);
        tweetImage.setPreserveRatio(true);
        ImageView profile = new ImageView();
        setProfilePhoto(profile , tweet.get("profilePhoto") , 50 , 50);
        Label date = new Label((tweet.get("date")).toString());
        Label userId = new Label("@"+tweet.get("userId")) , userName = new Label((String) tweet.get("userName"));
        Label retweet = new Label(tweet.get("retweetCount")+" \uD83D\uDD01");
        retweet.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c7075;");
        retweet.setAlignment(Pos.CENTER_LEFT);
        Label reply = new Label(tweet.get("replyCount")+" \uD83D\uDCAC");
        reply.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c7075;");
        reply.setAlignment(Pos.CENTER_LEFT);
        Label like = new Label();
        if(tweet.get("likedByYou").equals("false")){
            like.setText(tweet.get("likeCount")+" ♡");
        }
        else{
            like.setText(tweet.get("likeCount")+" ❤");
        }
        like.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(tweet.get("likedByYou").equals("false")){
                    boolean liked = Main.like(""+tweet.get("tweetId"));
                    if(liked){
                        tweet.put("likeCount", (Integer.parseInt(tweet.get("likeCount")+"")+1)+"");
                        like.setText((Integer.parseInt(tweet.get("likeCount")+""))+" ❤");
                        tweet.put("likedByYou", "true");
                    }
                }else{
                    boolean liked = Main.unlike(""+tweet.get("tweetId"));
                    if(liked){
                        tweet.put("likeCount", (Integer.parseInt(tweet.get("likeCount")+"")-1)+"");
                        like.setText((Integer.parseInt(tweet.get("likeCount")+""))+" ♡");
                        tweet.put("likedByYou", "false");
                    }
                }
            }
        });
        like.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c7075;");
        like.setAlignment(Pos.CENTER_LEFT);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("        Retweet        ");
        MenuItem item2 = new MenuItem("      Retweet With Quoute      ");
        contextMenu.getItems().addAll(item1, item2);
        contextMenu.setStyle("-fx-background-color : black; -fx-text-fill: white; -fx-border-color : white");

        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Main.tweet("" , null , (String) tweet.get("tweetId") , "" , name.getText());
            }
        });
        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    startRetweet((String) tweet.get("tweetId") , name.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        retweet.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(retweet,event.getScreenX(),event.getScreenY());
            }
        });
        reply.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    startReply((String) tweet.get("tweetId") , name.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c7075;");
        userId.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c7075;");
        userName.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight : BOLD;");
        HBox information = new HBox(date , userId , userName) , tools = new HBox(like , retweet , reply);
        tools.setMargin(reply , new Insets(0 , 0 , 0 ,200));
        tools.setMargin(like , new Insets(0 , 200 , 0 , 0));
        information.setAlignment(Pos.TOP_RIGHT);
        tools.setAlignment(Pos.BOTTOM_CENTER);
        tools.setPadding(new Insets(25,0,0,0));
        information.setMargin(date , new Insets(0,10,0,0));
        information.setMargin(userId , new Insets(0,10,0,0));
        information.setMargin(userName , new Insets(0,0,0,5));
        Label tweetText = new Label();
        if (tweet.get("message")!= null && !tweet.get("message").equals(""))
            tweetText.setText((String) tweet.get("message"));
        tweetText.setWrapText(true);
        tweetText.setStyle("-fx-text-fill : white; -fx-font-size: 15px; ");
        tweetText.setAlignment(Pos.CENTER_LEFT);

        VBox outerVbox = new VBox();
        outerVbox.setAlignment(Pos.TOP_LEFT);
        outerVbox.setPadding(new Insets(0,5,0,0));

        outerVbox.setFillWidth(true);
        HBox outerHbox = new HBox(outerVbox , profile);
        outerHbox.setMargin(outerVbox , new Insets(0 , 5 , 0 , 0));
        outerHbox.setPadding(new Insets(10 , 30 , 10 , 20));
        outerHbox.setFillHeight(true);
        outerHbox.setAlignment(Pos.TOP_CENTER);
        outerHbox.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-border-width: 0 1 1 1");
        if(!tweet.get("retweetTo").equals("")){
            HashMap<String , Object> retweeted = Main.getTweet((String) tweet.get("retweetTo"));
            if(tweet.get("photo")!=null || !tweet.get("message").equals("")){
                // RetweetWithQuote
                HBox retweetedHbox = setTweet(retweeted);
                retweetedHbox.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-border-width: 1 1 1 1");
                outerVbox.getChildren().addAll(information , tweetText , tweetImageHBox, retweetedHbox , tools);
                outerHbox.setMargin(retweetedHbox, new Insets(0,0,0,35));
                tweetImageHBox.setMargin(tweetImage , new Insets(25 ,0 ,0 ,0));
                outerVbox.setMargin(information , new Insets(0 , 15 , 0 , 0));
                outerVbox.setMargin(tweetText , new Insets(10 , 40 , 0 , 0));
                if (tweet.get("photo")!=null){
                    outerVbox.setMargin(retweetedHbox, new Insets(30,0,0,0));
                }
            }
            else{
                // Retweet
                HBox retweetedHbox = setTweet(retweeted);
                retweetedHbox.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-border-width: 1 1 1 1");
                Label top = new Label(tweet.get("userName")+" retweeted \uD83D\uDD01");
                HBox topHbox = new HBox(top);
                topHbox.setAlignment(Pos.TOP_RIGHT);
                top.setStyle("-fx-text-fill : #6c7075;");
                outerVbox.getChildren().addAll(topHbox , retweetedHbox);
                outerVbox.setMargin(topHbox, new Insets(0,0,5,0));
            }
        }
        else if(!tweet.get("replyTo").equals("")){
            HashMap<String , Object> replied = Main.getTweet((String) tweet.get("replyTo"));
            Label top = new Label(tweet.get("userName")+" replied to \uD83D\uDCAC");
            HBox topHbox = new HBox(top);
            topHbox.setAlignment(Pos.TOP_RIGHT);
            top.setStyle("-fx-text-fill : #6c7075;");
            HBox retweetedHbox = setTweet(replied);
            retweetedHbox.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-border-width: 1 1 1 1");
            outerVbox.getChildren().addAll(topHbox, information , tweetText , tweetImageHBox, retweetedHbox , tools);
            if (tweet.get("photo")!=null){
                outerVbox.setMargin(retweetedHbox, new Insets(30,0,0,0));
            }
            outerHbox.setMargin(retweetedHbox, new Insets(0,0,0,35));
            tweetImageHBox.setMargin(tweetImage , new Insets(25 ,0 ,0 ,0));
            outerVbox.setMargin(information , new Insets(0 , 15 , 0 , 0));
            outerVbox.setMargin(tweetText , new Insets(10 , 40 , 0 , 0));
            topHbox.setMargin(top, new Insets(0,0,5,0));
        }
        else{
            outerVbox.getChildren().addAll(information , tweetText , tweetImageHBox , tools);
            tweetImageHBox.setMargin(tweetImage , new Insets(25 ,0 ,0 ,0));
            outerVbox.setMargin(information , new Insets(0 , 15 , 0 , 0));
            outerVbox.setMargin(tweetText , new Insets(10 , 40 , 0 , 0));
        }
        return outerHbox;
    }

    public void setTweets(VBox father, ArrayList<HashMap<String , Object>> tweets, boolean isTimeLine){
        Iterator<Node> fatherIt = father.getChildren().iterator();
        while(fatherIt.hasNext()){
            Node node = fatherIt.next();
            if(node instanceof AnchorPane || node instanceof Button){
                continue;
            }
            fatherIt.remove();
        }
        Label defaultTweet = new Label("No tweets yet");
        defaultTweet.setTextFill(Color.WHITE);
        if(isTimeLine)
            defaultTweet.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-font-size : 17; -fx-border-width : 1 1 1 1;");
        else
            defaultTweet.setStyle("-fx-border-color: gray; -fx-border-radius : 5; -fx-font-size : 17; -fx-border-width : 0 1 1 1;");

        defaultTweet.setPrefSize(873.6 , 200);
        defaultTweet.setAlignment(Pos.CENTER);
        if (tweets.size()==0){
            father.getChildren().add(defaultTweet);
            return;
        }
        for(HashMap<String , Object> tweet : tweets){
           HBox tweetHbox = setTweet(tweet);
           father.getChildren().add(tweetHbox);
        }

    }

    public static void setProfilePhoto(ImageView profile, Object photo, int width, int height){
        try {
            profile.setImage(generateProfilePicture(new Image(new FileInputStream("ClientGUI/defaultAvatar.png")) , width , height));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if(photo!=null)
            profile.setImage(generateProfilePicture(new Image(new ByteArrayInputStream((byte[]) photo)) ,width,height));
    }
    public static Image generateProfilePicture(Image profilePic,int width , int height) { //pass 124 124
        ImageView image = new ImageView();
        image.setImage(profilePic);
        image.setStyle(
                "    -fx-border-width: 1;\n" +
                "    -fx-border-color: cyan;\n" +
                "    -fx-pref-width: 50;\n" +
                "    -fx-pref-height: 50;\n" +
                "    -fx-background-color: red;\n" +
                "     -fx-border-style: none;");
        AnchorPane g = new AnchorPane();
        g.setPrefWidth(width);
        g.setPrefHeight(height);
        g.setStyle("-fx-background-color: transparent;-fx-border-color:black;-fx-background-radius: 100;-fx-border-width: 0; -fx-border-style: none; ");
        Circle c = new Circle(width/2 - 1);
        c.smoothProperty().set(true);
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setFill(new ImagePattern(profilePic));
        AnchorPane.setLeftAnchor(c, 0.0);
        AnchorPane.setRightAnchor(c, 0.0);
        AnchorPane.setTopAnchor(c, 0.0);
        AnchorPane.setBottomAnchor(c, 0.0);
        g.getChildren().add(c);
        Scene scene = new Scene(g);
        scene.setFill(Color.TRANSPARENT);
        WritableImage wimg = new WritableImage(width,height);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scene.snapshot(wimg);
            }
        });
        return wimg;
    }
    public void startEditProfile() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("editPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Edit Profile");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((EditPageController)(fxmlLoader.getController())).stage = stage;
        ((EditPageController)(fxmlLoader.getController())).mainPage = this;
        stage.show();
    }
    public void startFollowPage(boolean isFollowing) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("followPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Follow List");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((FollowPageController)(fxmlLoader.getController())).stage = stage;
        ((FollowPageController)(fxmlLoader.getController())).following = following;
        ((FollowPageController)(fxmlLoader.getController())).followers = followers;
        ((FollowPageController)(fxmlLoader.getController())).mainPage = this;
        ((FollowPageController)(fxmlLoader.getController())).start();
        if(isFollowing)
            ((FollowPageController)(fxmlLoader.getController())).tabpane.getSelectionModel().select(1); // Following
        else
            ((FollowPageController)(fxmlLoader.getController())).tabpane.getSelectionModel().select(0); // Follower
        stage.show();
    }
    @FXML
    public void editProfilePressed() throws IOException {
        startEditProfile();
    }
    @FXML
    public void followingPressed() throws IOException {
        startFollowPage(true);
    }
    @FXML
    public void followersPressed() throws IOException {
        startFollowPage(false);
    }

    @FXML
    public void searchPressed(){
        String query = searchQuery.getText();
        if(query.equals("")){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Query is Empty");
            errorAlert.setContentText("Error : empty Query");
            errorAlert.showAndWait();
            return;
        }
        ArrayList<HashMap<String , Object>> users = Main.searchUser(query);
        ArrayList<HashMap<String , Object>> tweets = Main.searchTweet(query);
        setTweets(searchTweetVbox, tweets,true);
        setUsers(searchUserVbox, users);
    }

    public boolean isUserFollowed(String userId){
        for(HashMap<String , Object> user : following){
            if(userId.equals(user.get("userId")))
                return true;
        }
        return false;
    }

    public HBox setUser(HashMap<String,Object> user , boolean isFollowed){
        Button follow = new Button();
        if(isFollowed){
            follow.setText("Unfollow");
            follow.getStylesheets().addAll(this.getClass().getResource("styles.css").toExternalForm());
            follow.setId("button2");
        }
        else{
            follow.setText("Follow");
            follow.getStylesheets().addAll(this.getClass().getResource("styles.css").toExternalForm());
            follow.setId("button3");
        }
        HashMap<String , Boolean> tmp = new HashMap<>();
        tmp.put("isFollowed" , isFollowed);
        follow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(tmp.get("isFollowed")){
                    Main.unfollow((String) user.get("userId"));
                    tmp.put("isFollowed",false);
                    follow.setText("Follow");
                    follow.getStylesheets().addAll(this.getClass().getResource("styles.css").toExternalForm());
                    follow.setId("button3");
                }
                else{
                     Main.follow((String) user.get("userId"));
                     tmp.put("isFollowed",true);
                     follow.setText("Unfollow");
                    follow.getStylesheets().addAll(this.getClass().getResource("styles.css").toExternalForm());
                    follow.setId("button2");
                }
                refresh();
            }
        });
        follow.setPrefWidth(103);
        follow.setPrefHeight(27);

        Label name = new Label(user.get("userName")+" "+user.get("userLastName"));
        name.setStyle("-fx-font-size: 16px; -fx-text-fill: white; ");

        Label userId = new Label("@"+user.get("userId"));
        userId.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c7075");

        VBox userInfo = new VBox(name , userId);
        userInfo.setMargin(name , new Insets(-10 , 0 , 0 , 0));
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.setPrefWidth(308);
        userInfo.setPrefHeight(79);
        ImageView userProfile = new ImageView();
        MainPageController.setProfilePhoto(userProfile , user.get("avatar") , 50 , 50);

        HBox result = new HBox(follow , userInfo , userProfile);
        result.setMargin(userInfo ,new Insets(0 , 5, 0 , 0));
        result.setAlignment(Pos.CENTER_RIGHT);
        result.setStyle("-fx-border-color: grey;");
        result.setPadding(new Insets(10 , 30 , 10 , 20));
        result.setPrefWidth(515.2);
        result.setPrefHeight(78);
        return result;
    }
    public void setUsers(VBox father, ArrayList<HashMap<String , Object>> users){
        Iterator<Node> fatherIt = father.getChildren().iterator();
        while(fatherIt.hasNext()){
            Node node = fatherIt.next();
            fatherIt.remove();
        }
        Label defaultFollow= new Label("No user found");
        defaultFollow.setTextFill(Color.WHITE);
        defaultFollow.setStyle("-fx-font-size : 17;");
        defaultFollow.setPrefSize(515 , 200);
        defaultFollow.setAlignment(Pos.CENTER);
        if (users.size()==0){
            father.getChildren().add(defaultFollow);
            return;
        }
        for(HashMap<String , Object> user : users){
            HBox userHbox = setUser(user , isUserFollowed((String) user.get("userId")));
            father.getChildren().add(userHbox);
        }
    }
    @FXML
    public void searchKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            searchPressed();
    }

    public void startTweet(String userName) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("tweetPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tweet");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        ((TweetPageController)(fxmlLoader.getController())).stage = stage;
        ((TweetPageController)(fxmlLoader.getController())).userName = name.getText();
        ((TweetPageController)(fxmlLoader.getController())).mainPage = this;
        stage.show();
    }
    @FXML
    public void tweetPressed() throws IOException {
        startTweet(name.getText());
    }
}
