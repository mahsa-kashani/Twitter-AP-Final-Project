import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ClientSignUp {
    public static boolean signUp(String userId , String name , String lastName , String email , String  phoneNumber
            , String password , String country , String birthDate) throws IOException, ClassNotFoundException {
        Packet signUpPacket = new Packet();
        signUpPacket.request = "sign-up";
        signUpPacket.parameters.put("userId" , userId);
        signUpPacket.parameters.put("name" , name);
        signUpPacket.parameters.put("lastName" , lastName);
        signUpPacket.parameters.put("email" , email);
        signUpPacket.parameters.put("phoneNumber" , phoneNumber);
        signUpPacket.parameters.put("password" , password);
        signUpPacket.parameters.put("country" , country);
        signUpPacket.parameters.put("birthDate" , birthDate);
        Client.output.writeObject(signUpPacket);
        Packet result = (Packet) Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Error : "+ result.parameters.get("result"));
            return false;
        }
        String token = result.parameters.get("token");
        ClientSignIn.saveTokenToFile(token);
        return true;
    }

    public static boolean signUp() throws IOException, ClassNotFoundException {
        System.out.print("Please Enter userId: ");
        String userId = Client.scanner.nextLine();

        System.out.print("Please Enter name: ");
        String name = Client.scanner.nextLine();

        System.out.print("Please Enter lastName: ");
        String lastName = Client.scanner.nextLine();

        System.out.print("Please Enter email: ");
        String email = Client.scanner.nextLine();

        System.out.print("Please Enter phoneNumber: ");
        String phoneNumber = Client.scanner.nextLine();

        System.out.print("Please Enter password: ");
        String password = Client.scanner.nextLine();

        System.out.print("Please Enter country: ");
        String country = Client.scanner.nextLine();

        System.out.print("Please Enter birthDate: ");
        String birthDate = Client.scanner.nextLine();

        return signUp(userId, name, lastName, email, phoneNumber, password, country, birthDate);
    }
}
