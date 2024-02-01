import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;

public class Packet implements Serializable {
    public String request;
    public HashMap<String,String> parameters = new HashMap<>();
    public byte[] fileData;
    public ArrayList<HashMap<String, Object>> maps = new ArrayList<>();
}
