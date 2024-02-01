package com.clientgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class TweetPageController implements Initializable {
    String userName;
    Stage stage;
    MainPageController mainPage;
    @FXML
    TextField text;
    @FXML
    ImageView tweetPhoto;
    String selectedPhoto = "";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void tweetPressed() throws IOException {
        if(text.getText().equals("") && tweetPhoto.getImage() == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Tweet Error");
            errorAlert.setContentText("Error: You can't tweet an empty tweet!");
            errorAlert.showAndWait();
            stage.close();
            return;
        }
        if(selectedPhoto.equals(""))
            Main.tweet(text.getText() , null , "" , "" , userName);
        else
            Main.tweet(text.getText() , Files.readAllBytes(Path.of(selectedPhoto)) , "" , "" , userName);
        stage.close();
        mainPage.refresh();
    }
    Stage getMyStage(){
        return ((Stage)((Node)text).getScene().getWindow());
    }
    @FXML
    public void photoPressed() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files","*.jpg" , "*.png"));
        File chosen = fileChooser.showOpenDialog(getMyStage());
        if(chosen == null){
            //Cancel
            return;
        }
        String fileAddress = chosen.getAbsolutePath();
        selectedPhoto = fileAddress;
        tweetPhoto.setImage(new Image(new FileInputStream(fileAddress)));
    }
}
