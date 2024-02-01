import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientEditProfile {
    public static boolean setAvatar(String fileName) throws IOException{
        Packet setAvatarPacket = new Packet();
        setAvatarPacket.request = "avatar";
        setAvatarPacket.fileData = Files.readAllBytes(Path.of(fileName));
        Client.output.writeObject(setAvatarPacket);
        try {
            Packet result = (Packet)Client.input.readObject();
            if (!result.parameters.get("result").equals("successful")) {
                System.out.println("Server Error : "+result.parameters.get("result"));
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean setAvatar() throws IOException{
        System.out.println("Enter the file name of avatar : ");
        String filename = Client.scanner.nextLine();
        BufferedImage image = ImageIO.read(new File(filename));
        if (image.getWidth() != 400 || image.getHeight() != 400){
            System.out.println("The size of the picture invalid, try a 400*400 picture");
            return false;
        }
        if(Files.size(Path.of(filename)) > 1048576){
            System.out.println("The input file is large, try a file with size < 1mb");
            return false;
        }
        return setAvatar(filename);
    }

    public static boolean setHeader(String fileName) throws IOException{
        Packet setHeaderPacket = new Packet();
        setHeaderPacket.request = "header";
        setHeaderPacket.fileData = Files.readAllBytes(Path.of(fileName));
        Client.output.writeObject(setHeaderPacket);
        try {
            Packet result = (Packet)Client.input.readObject();
            if (!result.parameters.get("result").equals("successful")) {
                System.out.println("Server Error : "+result.parameters.get("result"));
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean setHeader() throws IOException{
        System.out.println("Enter the file name of header : ");
        String filename = Client.scanner.nextLine();
        BufferedImage image = ImageIO.read(new File(filename));
        if (image.getWidth() != 1500 || image.getHeight() != 500){
            System.out.println("The size of the picture invalid, try a 1500*500 picture");
            return false;
        }
        if(Files.size(Path.of(filename)) > 1048576*2){
            System.out.println("The input file is large, try a file with size < 2mb");
            return false;
        }
        return setHeader(filename);
    }

    public static boolean editProfileInfo(String bio, String location, String website) throws IOException, ClassNotFoundException {
        Packet setInfoPacket = new Packet();
        setInfoPacket.request = "edit-profile-info";
        setInfoPacket.parameters.put("bio" , bio);
        setInfoPacket.parameters.put("location" , location);
        setInfoPacket.parameters.put("website" , website);
        Client.output.writeObject(setInfoPacket);
        Packet result = (Packet) Client.input.readObject();
        if(!result.parameters.get("result").equals("successful")){
            System.out.println("Server error : " + result.parameters.get("result"));
            return false;
        }
        return true;
    }

    public static boolean editProfileInfo() throws IOException, ClassNotFoundException {
        System.out.print("Enter bio : ");
        String bio = Client.scanner.nextLine();
        if(bio.length() > 160){
            System.out.println("The bio is larger than 160 characters!");
            return false;
        }
        System.out.print("Enter location : ");
        String location = Client.scanner.nextLine();
        System.out.print("Enter website : ");
        String website = Client.scanner.nextLine();
        return editProfileInfo(bio, location, website);
    }

}
