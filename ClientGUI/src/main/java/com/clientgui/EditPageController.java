package com.clientgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.clientgui.MainPageController.getUser;

public class EditPageController implements Initializable {
    Stage stage;
    MainPageController mainPage;
    @FXML
    ImageView header , profilePhoto;
    @FXML
    TextField bio , location , website;
    @FXML
    Button headerCamera , profileCamera;

    String selectedHeader = "";
    String selectedAvatar = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Tooltip t = new Tooltip("Change header photo");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        headerCamera.setTooltip(t);
        t = new Tooltip("Change profile photo");
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        profileCamera.setTooltip(t);
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
        System.out.println("4p");
        MainPageController.setProfilePhoto(profilePhoto, userInfo.get("avatar"), 150,150);
        if(!userInfo.get("bio").equals(""))
            bio.setText((String) userInfo.get("bio"));
        if(!userInfo.get("location").equals(""))
            location.setText((String) userInfo.get("location"));
        if(!userInfo.get("website").equals(""))
            website.setText((String) userInfo.get("website"));
    }
    @FXML
    public void onPaneClick(MouseEvent actionEvent){
        ((Node)actionEvent.getSource()).requestFocus();
    }
    Stage getMyStage(){
        return ((Stage)((Node)header).getScene().getWindow());
    }
    @FXML
    public void headerPressed() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files","*.jpg" , "*.png"));
        File chosen = fileChooser.showOpenDialog(getMyStage());
        if(chosen == null){
            //Cancel
            return;
        }
        String fileAddress = chosen.getAbsolutePath();
        selectedHeader = fileAddress;
        header.setImage(new Image(new FileInputStream(fileAddress)));
    }
    @FXML
    public void profilePressed() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files","*.jpg" , "*.png"));
        File chosen = fileChooser.showOpenDialog(getMyStage());
        if(chosen == null){
            //Cancel
            return;
        }
        String fileAddress = chosen.getAbsolutePath();
        selectedAvatar = fileAddress;
        System.out.println("3p");
        MainPageController.setProfilePhoto(profilePhoto , Files.readAllBytes(Path.of(fileAddress)) ,150,150);
        System.out.println("3");
    }
    public void setAvatar(byte[] file) {
        Packet setAvatarPacket = new Packet();
        setAvatarPacket.request = "avatar";
        setAvatarPacket.fileData = file;
        try{
            Main.output.writeObject(setAvatarPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Edit Profile Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
            }
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Edit Profile Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }catch (ClassNotFoundException e){
            Platform.exit();
        }
    }
    public void setHeader(byte[] file) {
        Packet setHeaderPacket = new Packet();
        setHeaderPacket.request = "header";
        setHeaderPacket.fileData = file;
        try{
            Main.output.writeObject(setHeaderPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Edit Profile Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
            }
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Edit Profile Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }catch (ClassNotFoundException e){
            Platform.exit();
        }
    }
    public void editProfileInfo(String bio, String location, String website){
        Packet setInfoPacket = new Packet();
        setInfoPacket.request = "edit-profile-info";
        setInfoPacket.parameters.put("bio" , bio);
        setInfoPacket.parameters.put("location" , location);
        setInfoPacket.parameters.put("website" , website);
        try{
            Main.output.writeObject(setInfoPacket);
            Packet result = (Packet)Main.input.readObject();
            if(!result.parameters.get("result").equals("successful")){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Edit Profile Error");
                errorAlert.setContentText("Error : "+result.parameters.get("result"));
                errorAlert.showAndWait();
            }
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Edit Profile Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }catch (ClassNotFoundException e){
            Platform.exit();
        }
    }


    @FXML
    public void submitPressed() throws IOException {
        if(!selectedAvatar.equals(""))
            setAvatar(Files.readAllBytes(Path.of(selectedAvatar)));
        if(!selectedHeader.equals(""))
            setHeader(Files.readAllBytes(Path.of(selectedHeader)));
        editProfileInfo(bio.getText() , location.getText() , website.getText());
        stage.close();
        mainPage.refresh();
    }
}
