package com.clientgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.io.ObjectInputStream;

import javafx.application.*;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class StartPageController implements Initializable{

    @FXML
    ImageView logo;
    @FXML
    Label connection;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            logo.setImage(new Image(new FileInputStream("ClientGUI/Logo_of_Twitter.svg.png")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void connectionStatus()  {
        connection.setText("Failed to connect to server!");
        connection.setTextFill(Color.RED);
    }
}
