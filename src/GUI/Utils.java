package GUI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {
    // Serialization code

    static void serialize(SimManager Obj) {
        try {
        	FileOutputStream file = new FileOutputStream("data.obj");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(Obj);
            out.close();
            file.close();
    	} catch (IOException i) {

    		i.printStackTrace();
    	}  
    }
    // Deserialization
    static SimManager deserialize(){
        try {
        	FileInputStream file = new FileInputStream("data.obj");
            ObjectInputStream input = new ObjectInputStream(file);
            SimManager tmp = (SimManager) input.readObject();
            input.close();
            file.close();
            return tmp;
        } catch (FileNotFoundException c) {
            System.out.println("Simulation not found, please restart");
            c.printStackTrace();
            return null;
        } catch (IOException i) {
            System.out.println("TEST2");
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Space class not found");
            c.printStackTrace();
            return null;
        }
    }
}
