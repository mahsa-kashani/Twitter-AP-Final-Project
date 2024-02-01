package com.clientgui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

public class FollowPageController implements Initializable {
    Stage stage;
    MainPageController mainPage;
    ArrayList<HashMap<String , Object>> following , followers = new ArrayList<>();
    @FXML
    TabPane tabpane;
    @FXML
    VBox followingVbox , followersVbox;
    @FXML
    Tab followingPane , followersPane;
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
                mainPage.refresh();
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void start(){
        followersPane.setText(followers.size()+ " Followers");
        followingPane.setText(following.size()+ " Following");
        setUsers(followersVbox , followers);
        setUsers(followingVbox , following);
    }
}

