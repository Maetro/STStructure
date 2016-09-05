/**
 * ByteFileHelper.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * The Class ByteFileUtil.
 */
public class ByteFileUtil {

    public static final Integer TAMANO_INTEGER = 4;
    public static final Integer TAMANO_SHORT = 2;

    /**
     * Anadir entero a lista bytes.
     *
     * @param resultado
     *            resultado
     * @param num
     *            num
     */
    public static void anadirEnteroAListaBytes(final List<Byte> resultado, final Integer num) {
        final byte[] bytes = ByteBuffer.allocate(TAMANO_INTEGER).putInt(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

    /**
     * Anadir short a lista bytes.
     *
     * @param resultado
     *            the resultado
     * @param num
     *            the num
     */
    public static void anadirShortAListaBytes(final List<Byte> resultado, final Short num) {
        final byte[] bytes = ByteBuffer.allocate(TAMANO_SHORT).putShort(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

    /**
     * Crear fichero si no existe.
     *
     * @param uri
     *            the uri
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static FileOutputStream crearFicheroEscrituraSiNoExiste(final String uri) throws IOException {
        final File tempFile = new File(uri);

        // if file doesnt exists, then create it
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        final FileOutputStream writer = new FileOutputStream(tempFile);
        return writer;
    }

    public static void appendFileToFile(final File file, final InputStream in)
            throws FileNotFoundException, IOException {
        final OutputStream out = new FileOutputStream(file, true); // appending output stream

        try {
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Adapt lista bytes to array.
     *
     * @param cabecera
     *            the cabecera
     * @return the byte[]
     */
    public static byte[] adaptListaBytesToArray(final List<Byte> cabecera) {
        return ArrayUtils.toPrimitive(cabecera.toArray(new Byte[cabecera.size()]));
    }
}
