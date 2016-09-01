/**
 * CompresorEstructuraHelper.java 23-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.log.LogHelper;
import es.ramon.casares.proyecto.modelo.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;

/**
 * The Class CompresorEstructuraHelper.
 *
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public final class CompresorEstructuraHelper {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(CompresorEstructuraHelper.class);

    private CompresorEstructuraHelper() {
        // Clase helper no instanciable
    }

    /**
     * Comprimir estructura. La estructura comprimida se compone de la siguiente manera. 1er byte - Numero de Snapshots
     * 2 byte - Separacion entre Snapshots Por cada SnapShot - Numero de bytes que ocupa Y el propio Snapshot en los
     * bytes indicados. A Continuacion iran los Logs uno tras otro seguidos. Para que funcione necesitamos indicar el
     * numero de objetos. Por lo que antes de empezar a escribir los Logs y después de los Snapshots escribiremos el
     * numero de objetos que contiene la estructura.
     *
     * @param parametros
     *
     * @param estructura
     *            estructura
     * @param parametros
     *            the parametros
     * @return the EstructuraComprimidaBean
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void comprimirEstructura(final Resource ficherofrecuencias, final Resource ficheroCuerpo,
            final List<Integer> punteros, final ComprimirEstructuraParametersBean parametros)
            throws FileNotFoundException, IOException {
        int puntero = 0;

        final File fileFinal = new File("src/main/resources/estructuraComprimida");

        // if file doesnt exists, then create it
        if (!fileFinal.exists()) {
            fileFinal.createNewFile();
        }

        final List<Byte> cabecera = new ArrayList<Byte>();

        final RandomAccessFile datareader = new RandomAccessFile(ficherofrecuencias.getFile(), "r");

        final List<Byte> movimientosPorFrecuencia = new ArrayList<Byte>();
        String currentLine;
        while ((currentLine = datareader.readLine()) != null) {
            ByteFileHelper.anadirEnteroAListaBytes(movimientosPorFrecuencia, Integer.valueOf(currentLine));
        }
        puntero += movimientosPorFrecuencia.size();

        final int bytesFicheroFrecuencias = movimientosPorFrecuencia.size();

        crearCabecerasEstructuraComprimida(punteros, parametros, cabecera, bytesFicheroFrecuencias);

        puntero += cabecera.size();
        FileUtils.writeByteArrayToFile(fileFinal, adaptListaBytesToArray(cabecera));

        InputStream in = new FileInputStream(ficherofrecuencias.getFile());

        FileUtils.writeByteArrayToFile(fileFinal, adaptListaBytesToArray(movimientosPorFrecuencia), true);

        appendFileToFile(fileFinal, in);
        final List<Byte> punterosBytes = new ArrayList<Byte>();
        for (final Integer punteroSnapshot : punteros) {
            ByteFileHelper.anadirEnteroAListaBytes(punterosBytes, punteroSnapshot + puntero);
        }

        FileUtils.writeByteArrayToFile(fileFinal, adaptListaBytesToArray(punterosBytes), true);
        in = new FileInputStream(ficheroCuerpo.getFile());

        appendFileToFile(fileFinal, in);

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
     * Crear cabeceras estructura comprimida.
     *
     * @param punteros
     *
     * @param estructura
     *            the estructura
     * @param parametros
     *            the parametros
     * @param cabecera
     *            the cabecera
     * @param bytesFicheroFrecuencias
     */
    private static void crearCabecerasEstructuraComprimida(final List<Integer> punteros,
            final ComprimirEstructuraParametersBean parametros,
            final List<Byte> cabecera, final int bytesFicheroFrecuencias) {
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, punteros.size());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, parametros.getSeparacionSnapshots());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, parametros.getNumeroObjetos());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, parametros.getParametroS());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, parametros.getParametroC());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, parametros.getCodigoReaparicionAbsoluta());
        ByteFileHelper.anadirEnteroAListaBytes(cabecera, bytesFicheroFrecuencias);
    }

    /**
     * Adapt lista bytes to array.
     *
     * @param cabecera
     *            the cabecera
     * @return the byte[]
     */
    private static byte[] adaptListaBytesToArray(final List<Byte> cabecera) {
        return ArrayUtils.toPrimitive(cabecera.toArray(new Byte[cabecera.size()]));
    }

    public static Integer comprimirBloqueSnapshotLog(final Snapshot snapshot, final List<Log> logs,
            final FileOutputStream tempFile, final int numeroBloques, final int parametroS, final int parametroC,
            int puntero) throws IOException {
        final List<Byte> resultado = new ArrayList<Byte>();
        final K2Tree k2Tree = (K2Tree) snapshot;
        final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
        ByteFileHelper.anadirEnteroAListaBytes(resultado, bytes.length);
        for (final byte b : bytes) {
            resultado.add(b);
        }
        for (int j = 0; j < logs.size(); j++) {
            final int lognumber = (numeroBloques * logs.size()) + j + 1;
            logger.info("Log Number: " + lognumber);

            final Log log = logs.get(j);
            final byte[] bytesLog = LogHelper.serializarLog(log, parametroS,
                    parametroC);

            for (final byte b : bytesLog) {
                resultado.add(b);
            }
        }
        puntero = puntero + resultado.size();

        IOUtils.write(adaptListaBytesToArray(resultado), tempFile);
        tempFile.flush();
        return puntero;
    }

    /**
     * Descomprimir estructura.
     *
     * @param estructuraComprimida
     *            the estructura comprimida
     * @param parametroS
     *            the s
     * @param parametroC
     *            the c
     * @return the estructura
     * @throws IOException
     */
    public static Estructura descomprimirEstructura(final File estructuraComprimida, final int chunkSize)
            throws IOException {
        final long posicion = 0;
        final RandomAccessFile estructura = new RandomAccessFile(estructuraComprimida, "r");
        estructura.seek(0);
        // Leer cabeceras

        final int numeroSnapshots = estructura.readInt();
        final int separacionSnapshots = estructura.readInt();
        final int numeroObjetos = estructura.readInt();
        final int parametroS = estructura.readInt();
        final int parametroC = estructura.readInt();
        final int codigoReaparicionAbsoluta = estructura.readInt();
        final int bytesFrecuencias = estructura.readInt();
        final ComprimirEstructuraParametersBean cabecera = new ComprimirEstructuraParametersBean();
        cabecera.setSeparacionSnapshots(separacionSnapshots);

        // byte[] slice = Arrays.copyOfRange(estructuraComprimida, posicion, posicion + chunkSize);
        //
        // posicion = posicion + chunkSize;
        //
        // final int numSnapshots = ByteBuffer.wrap(slice).getInt();
        //
        // slice = Arrays.copyOfRange(estructuraComprimida, posicion, posicion + chunkSize);
        //
        // posicion = posicion + chunkSize;
        //
        // final int separacionSnapshots = ByteBuffer.wrap(slice).getInt();
        //
        // System.out.println("NUM : " + numSnapshots + " SEP: " + separacionSnapshots);
        //
        // final Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();
        //
        // int posicionFinal = K2TreeHelper.descomprimirSnapshots(estructuraComprimida, snapshots, numSnapshots,
        // separacionSnapshots);
        //
        // slice = Arrays.copyOfRange(estructuraComprimida, posicionFinal, posicionFinal + chunkSize);
        //
        // posicionFinal = posicionFinal + chunkSize;
        //
        // final int numObjetos = ByteBuffer.wrap(slice).getInt();
        //
        // System.out.println("OBJETOS : " + numObjetos);
        //
        // final Map<Integer, Log> logs = new HashMap<Integer, Log>();
        //
        // LogHelper.descomprimirLogs(estructuraComprimida, logs, numObjetos, posicionFinal, parametroS, parametroC);
        //
        final Estructura resultado = new Estructura(null, null);
        return resultado;

    }

}