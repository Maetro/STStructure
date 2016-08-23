/**
 * CompresorEstructuraHelper.java 23-ago-2016
 *
 */
package es.ramon.casares.proyecto.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.log.LogHelper;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;

/**
 * The Class CompresorEstructuraHelper.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class CompresorEstructuraHelper {

    /**
     * Comprimir estructura. La estructura comprimida se compone de la siguiente manera. 1er byte - Numero de Snapshots
     * 2 byte - Separacion entre Snapshots Por cada SnapShot - Numero de bytes que ocupa Y el propio Snapshot en los
     * bytes indicados. A Continuacion iran los Logs uno tras otro seguidos. Para que funcione necesitamos indicar el
     * numero de objetos. Por lo que antes de empezar a escribir los Logs y después de los Snapshots escribiremos el
     * numero de objetos que contiene la estructura.
     * 
     * @param estructura
     *            estructura
     * @param separacionSnapshots
     *            separacion snapshots
     * @param numObjetos
     *            num objetos
     * @return the byte[]
     */
    public static byte[] comprimirEstructura(final Estructura estructura, final int separacionSnapshots,
            final int numObjetos) {

        final List<Byte> resultado = new ArrayList<Byte>();
        anadirEnteroAListaBytes(resultado, estructura.getSnapshots().size());
        anadirEnteroAListaBytes(resultado, separacionSnapshots);
        comprimirSnapshots(estructura, resultado);
        anadirEnteroAListaBytes(resultado, numObjetos);
        comprimirLogs(estructura, resultado);
        final Byte[] bytes = resultado.toArray(new Byte[resultado.size()]);
        return ArrayUtils.toPrimitive(bytes);

    }

    public static Estructura descomprimirEstructura(final byte[] estructuraComprimida) {

        byte[] slice = Arrays.copyOfRange(estructuraComprimida, 0, 4);

        final int numSnapshots = ByteBuffer.wrap(slice).getInt();

        slice = Arrays.copyOfRange(estructuraComprimida, 4, 8);

        final int separacionSnapshots = ByteBuffer.wrap(slice).getInt();

        System.out.println("NUM : " + numSnapshots + " SEP: " + separacionSnapshots);

        final Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();

        int posicionFinal = K2TreeHelper.descomprimirSnapshots(estructuraComprimida, snapshots, numSnapshots,
                separacionSnapshots);

        slice = Arrays.copyOfRange(estructuraComprimida, posicionFinal, posicionFinal + 4);

        posicionFinal = posicionFinal + 4;

        final int numObjetos = ByteBuffer.wrap(slice).getInt();

        System.out.println("OBJETOS : " + numObjetos);

        final Map<Integer, Log> logs = new HashMap<Integer, Log>();

        LogHelper.descomprimirLogs(estructuraComprimida, logs, numObjetos, posicionFinal);

        final Estructura resultado = new Estructura(snapshots, logs);
        return resultado;

    }

    /**
     * Comprimir logs.
     * 
     * @param estructura
     *            estructura
     * @param resultado
     *            resultado
     */
    private static void comprimirLogs(final Estructura estructura, final List<Byte> resultado) {
        for (final Log log : estructura.getLogs().values()) {

            final byte[] bytes = LogHelper.serializarLog(log);
            for (final byte b : bytes) {
                resultado.add(b);
            }
        }
    }

    /**
     * Comprimir snapshots.
     * 
     * @param estructura
     *            estructura
     * @param resultado
     *            resultado
     */
    private static void comprimirSnapshots(final Estructura estructura, final List<Byte> resultado) {
        for (final Snapshot k2Tree : estructura.getSnapshots().values()) {
            final byte[] bytes = K2TreeHelper.serializarK2Tree((K2Tree) k2Tree);
            anadirEnteroAListaBytes(resultado, bytes.length);
            for (final byte b : bytes) {
                resultado.add(b);
            }
        }
    }

    /**
     * Anadir entero a lista bytes.
     * 
     * @param resultado
     *            resultado
     * @param num
     *            num
     */
    private static void anadirEnteroAListaBytes(final List<Byte> resultado, final Integer num) {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

}
