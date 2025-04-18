package com.adauction.group19.utils;

import java.io.*;

/**
 * Utility class for serialising and deserialising objects.
 * This class provides methods to convert objects to byte arrays and vice versa.
 */
public class SerializationUtil {
    /**
     * Serialises an object into a byte array.
     *
     * @param obj The object to serialise.
     * @return The serialised byte array.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] serialise(Serializable obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    /**
     * Deserialises a byte array into an object.
     *
     * @param data The byte array to deserialise.
     * @return The deserialised object.
     * @throws IOException            If an I/O error occurs.
     * @throws ClassNotFoundException If the class of a serialised object cannot be found.
     */
    public static Object deserialise(byte[] data)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
}
