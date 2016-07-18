/**
 * Serializer.java 04-jul-2016
 *
 */
package es.ramon.casares.proyecto.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The Class Serializer.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class Serializer {

    /**
     * Serialize.
     * 
     * @param obj
     *            obj
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static byte[] serialize(final Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    /**
     * Deserialize.
     * 
     * @param bytes
     *            bytes
     * @return the object
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             de class not found exception
     */
    public static Object deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }

}