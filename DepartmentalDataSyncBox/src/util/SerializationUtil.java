package util;

import java.io.*;
import java.util.Base64;

public class SerializationUtil {

    public static String serialize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    public static <T> T deserialize(String base64String, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64String));
                ObjectInputStream ois = new ObjectInputStream(bais)) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}
