import java.io.*;

public class FileHandler {
    public static void saveToFile(Serializable obj, String path) {
        new File("data").mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(obj);
        } catch (IOException e) { System.out.println("Save failed: " + e.getMessage()); }
    }

    public static Object loadFromFile(String path) {
        File f = new File(path);
        if (!f.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load failed: " + e.getMessage());
            return null;
        }
    }
}
