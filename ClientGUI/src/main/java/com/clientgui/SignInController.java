package com.clientgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    Stage stage;
    @FXML
    ImageView signInImage , backGroundImage;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            signInImage.setImage(new Image(new FileInputStream("ClientGUI/loginImage.png")));
            backGroundImage.setImage(new Image(new FileInputStream("ClientGUI/background.png")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveTokenToFile(String token) throws IOException{
        Files.writeString(Paths.get("token.txt") , token);
    }
    @FXML
    public void signInButtonPressed() {
        String _username = username.getText();
        String _password = password.getText();
        Packet signInPacket = new Packet();
        signInPacket.request = "sign-in";
        signInPacket.parameters.put("userId" , _username);
        signInPacket.parameters.put("password" , _password);
        if(_username.equals("") || _password.equals("")){
             Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignIn Error");
            errorAlert.setContentText("Please fill in both username and password");
            errorAlert.showAndWait();
            return;
        }
        try{
            Main.output.writeObject(signInPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("SignIn Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
                return;
            }
            String token = result.parameters.get("token");
            saveTokenToFile(token);
            Main.userId = _username;
            Main.startTwitter();
            stage.close();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignIn Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }catch (ClassNotFoundException e){
            Platform.exit();
        }

    }
    @FXML
    public void signUpPressed() throws IOException {
        Main.startSignUp();
        stage.close();
    }
    @FXML
    public void usernameKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            password.requestFocus();
    }
    @FXML
    public void passwordKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            signInButtonPressed();
    }
    @FXML
    public void onPaneClick(MouseEvent actionEvent){
        ((Node)actionEvent.getSource()).requestFocus();
    }
}