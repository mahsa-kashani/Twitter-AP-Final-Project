package com.clientgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

class FxDatePickerConverter extends StringConverter{
    private String pattern = "yyyy/MM/dd";
    private DateTimeFormatter dtFormatter;
    public FxDatePickerConverter() {
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }
    @Override
    public String toString(Object o) {
         String text = null;
        if (o instanceof LocalDate)
            text = dtFormatter.format((LocalDate)(o));
        return text;
    }
    public FxDatePickerConverter(String pattern)
    {
        this.pattern = pattern;
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }
    public LocalDate fromString(String text)
    {
        LocalDate date = null;
        if (text != null && !text.trim().isEmpty())
        {
            date = LocalDate.parse(text, dtFormatter);
        }
        return date;
    }

}

public class SignUpController implements Initializable, Back {
    Stage stage;
    @FXML
    ImageView logoImage;
    @FXML
    TextField username, name , lastName , phoneOrEmail;
    @FXML
    PasswordField password , repeatPassword;
    @FXML
    ChoiceBox country;
    @FXML
    DatePicker birthDate;

    String emailRegex = "[A-Za-z0-9._-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,4}";
    String phoneRegex ="\\+?[0-9]{10,16}";
    String passwordRegex = "(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9.-_#*@()+=!~,<>/\\[\\]{}]{8,}";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FxDatePickerConverter converter = new FxDatePickerConverter("yyyy/MM/dd");
        birthDate.setConverter(converter);
        try {
            logoImage.setImage(new Image(new FileInputStream("ClientGUI/bird.png")));
            country.getItems().addAll("Afghanistan","Albania","Algeria","Andorra","Angola","Antigua & Deps","Argentina",
                    "Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus",
                    "Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia Herzegovina","Botswana","Brazil","Brunei",
                    "Bulgaria","Burkina","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Central African Rep",
                    "Chad","Chile","China","Colombia","Comoros","Congo","Congo {Democratic Rep}","Costa Rica","Croatia"
                    ,"Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","East Timor"
                    ,"Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Fiji","Finland"
                    ,"France","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Grenada","Guatemala","Guinea"
                    ,"Guinea-Bissau","Guyana","Haiti","Honduras","Hungary","Iceland","India","Indonesia","Iran",
                    "Iraq","Ireland {Republic}","Israel","Italy","Ivory Coast","Jamaica","Japan","Jordan","Kazakhstan"
                    ,"Kenya","Kiribati","Korea North","Korea South","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia",
                    "Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macedonia",
                    "Madagascar","Malakhestan","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands",
                    "Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro",
                    "Morocco","Mozambique","Myanmar, {Burma}","Namibia","Nauru","Nepal","Netherlands","New Zealand"
                    ,"Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palau","Panama","Papua New Guinea",
                    "Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Qom","Romania","Russian Federation"
                    ,"Rwanda","St Kitts & Nevis","St Lucia","Saint Vincent & the Grenadines","Samoa","San Marino",
                    "Sao Tome & Principe","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia",
                    "Slovenia","Solomon Islands","Somalia","South Africa","South Sudan","Spain","Sri Lanka","Sudan"
                    ,"Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand"
                    ,"Togo","Tonga","Trinidad & Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine"
                    ,"United Arab Emirates","United Kingdom","United States","Uruguay","Uzbekistan","Vanuatu",
                    "Vatican City","Venezuela","Vietnam","Yemen","Zambia","Zimbabwe");
            country.getSelectionModel().select(0);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void usernameKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            name.requestFocus();
    }
    @FXML
    public void nameKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            lastName.requestFocus();
    }
    @FXML
    public void lastnameKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            phoneOrEmail.requestFocus();
    }
    @FXML
    public void phoneoremailKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            password.requestFocus();
    }
    @FXML
    public void passwordKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            repeatPassword.requestFocus();
    }
    @FXML
    public void repeatPasswordKeyPressed(KeyEvent actionEvent){
        if(actionEvent.getCode() == KeyCode.ENTER)
            birthDate.requestFocus();
    }
    @FXML
    public void birthDateKeyPressed(KeyEvent actionEvent) throws IOException, ClassNotFoundException {
        if(actionEvent.getCode() == KeyCode.ENTER)
            signUpPressed();
    }
    @FXML
    public void onPaneClick(MouseEvent actionEvent){
        ((Node)actionEvent.getSource()).requestFocus();
    }

    @FXML
    public void signUpPressed() throws IOException {
        String phoneNumber = "" , email = "";
        if(username.getText().equals("") || name.getText().equals("")
        || lastName.getText().equals("") || phoneOrEmail.getText().equals("")
        || password.getText().equals("") || repeatPassword.getText().equals("")
        || birthDate.getValue() == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Please fill in all information");
            errorAlert.showAndWait();
            return;
        }
        String temp = phoneOrEmail.getText();
        if (Main.regexChecker(phoneRegex, temp)){
            phoneNumber = temp;
        }
        else if(Main.regexChecker(emailRegex, temp)){
            email = temp;
        }
        else{
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Email or PhoneNumber format is not valid!");
            errorAlert.showAndWait();
            return;
        }
        if(!Main.regexChecker(passwordRegex, password.getText())){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Password format is incorrect! (Notice that it must contain both lower and upper case characters)");
            errorAlert.showAndWait();
            return;
        }
        if(!repeatPassword.getText().equals(password.getText())){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Password repeated must be the same as your password!");
            errorAlert.showAndWait();
            return;
        }
        Packet signUpPacket = new Packet();
        signUpPacket.request = "sign-up";
        signUpPacket.parameters.put("userId" , username.getText());
        signUpPacket.parameters.put("name" , name.getText());
        signUpPacket.parameters.put("lastName" , lastName.getText());
        signUpPacket.parameters.put("email" , email);
        signUpPacket.parameters.put("phoneNumber" , phoneNumber);
        signUpPacket.parameters.put("password" , password.getText());
        signUpPacket.parameters.put("country" , country.getValue().toString());
        signUpPacket.parameters.put("birthDate" , birthDate.getValue().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        Packet result = null;
        try{
            Main.output.writeObject(signUpPacket);
            result = (Packet) Main.input.readObject();
        }catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Error : Connection Interrupted!");
            errorAlert.showAndWait();
            Platform.exit();
        }catch (ClassNotFoundException e){
            Platform.exit();
        }
        if(!result.parameters.get("result").equals("successful")){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("SignUp Error");
            errorAlert.setContentText("Error : "+result.parameters.get("result"));
            errorAlert.showAndWait();
            return;
        }
        String token = result.parameters.get("token");
        SignInController.saveTokenToFile(token);
        Main.userId = username.getText();
        Main.startTwitter();
        stage.close();
    }


    @Override
    public void backPressed() throws IOException {
        Main.startSignIn();
        stage.close();
    }
}
