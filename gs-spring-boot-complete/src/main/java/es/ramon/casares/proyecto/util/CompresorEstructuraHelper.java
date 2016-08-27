/**
 * CompresorEstructuraHelper.java 23-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
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
import es.ramon.casares.proyecto.modelo.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.modelo.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;

/**
 * The Class CompresorEstructuraHelper.
 *
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class CompresorEstructuraHelper {

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
     * @param estructura
     *            estructura
     * @param separacionSnapshots
     *            separacion snapshots
     * @param numObjetos
     *            num objetos
     * @return the byte[]
     */
    public static byte[] comprimirEstructura(final Estructura estructura,
            final ComprimirEstructuraParametersBean parametros) {

        final List<Byte> resultado = new ArrayList<Byte>();
        final List<Integer> punteros = new ArrayList<Integer>();
        ByteFileHelper.anadirEnteroAListaBytes(resultado, estructura.getSnapshots().size());
        ByteFileHelper.anadirEnteroAListaBytes(resultado, parametros.getSeparacionSnapshots());
        ByteFileHelper.anadirEnteroAListaBytes(resultado, parametros.getNumeroObjetos());
        ByteFileHelper.anadirEnteroAListaBytes(resultado, parametros.getParametroS());
        ByteFileHelper.anadirEnteroAListaBytes(resultado, parametros.getParametroC());
        ByteFileHelper.anadirEnteroAListaBytes(resultado, parametros.getCodigoReaparicionAbsoluta());
        comprimirEstructuraEnBloques(estructura, resultado, punteros, parametros);
        final Byte[] bytes = resultado.toArray(new Byte[resultado.size()]);
        return ArrayUtils.toPrimitive(bytes);

    }

    /**
     * Comprimir bloque estructura.
     *
     * @param estructura
     *            the estructura
     * @param resultado
     *            the resultado
     * @param separacionSnapshots
     *            the separacion snapshots
     * @param punteros
     * @param parametroC
     * @param parametroS
     */
    private static void comprimirEstructuraEnBloques(final Estructura estructura, final List<Byte> resultado,
            final List<Integer> punteros, final ComprimirEstructuraParametersBean parametros) {
        int numeroBloques = 0;
        final int end = estructura.getLogs().size();
        for (final Snapshot snapshot : estructura.getSnapshots().values()) {
            final Integer puntero = resultado.size();
            final K2Tree k2Tree = (K2Tree) snapshot;
            final byte[] bytes = K2TreeHelper.serializarK2Tree(k2Tree);
            ByteFileHelper.anadirEnteroAListaBytes(resultado, bytes.length);
            for (final byte b : bytes) {
                resultado.add(b);
            }
            for (int j = 0; j < parametros.getSeparacionSnapshots(); j++) {
                final int lognumber = (numeroBloques * parametros.getSeparacionSnapshots()) + j + 1;
                System.out.println("Log Number: " + lognumber);
                if (lognumber >= end) {
                    break;
                }
                final Log log = estructura.getLogs().get(lognumber);
                final byte[] bytesLog = LogHelper.serializarLog(log, parametros.getParametroS(),
                        parametros.getParametroC());

                for (final byte b : bytesLog) {
                    resultado.add(b);
                }
            }
            punteros.add(puntero);
            numeroBloques++;
        }

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
     */
    public static Estructura descomprimirEstructura(final byte[] estructuraComprimida, final int chunkSize,
            final int parametroS, final int parametroC) {

        int posicion = 0;

        byte[] slice = Arrays.copyOfRange(estructuraComprimida, posicion, posicion + chunkSize);

        posicion = posicion + chunkSize;

        final int numSnapshots = ByteBuffer.wrap(slice).getInt();

        slice = Arrays.copyOfRange(estructuraComprimida, posicion, posicion + chunkSize);

        posicion = posicion + chunkSize;

        final int separacionSnapshots = ByteBuffer.wrap(slice).getInt();

        System.out.println("NUM : " + numSnapshots + " SEP: " + separacionSnapshots);

        final Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();

        int posicionFinal = K2TreeHelper.descomprimirSnapshots(estructuraComprimida, snapshots, numSnapshots,
                separacionSnapshots);

        slice = Arrays.copyOfRange(estructuraComprimida, posicionFinal, posicionFinal + chunkSize);

        posicionFinal = posicionFinal + chunkSize;

        final int numObjetos = ByteBuffer.wrap(slice).getInt();

        System.out.println("OBJETOS : " + numObjetos);

        final Map<Integer, Log> logs = new HashMap<Integer, Log>();

        LogHelper.descomprimirLogs(estructuraComprimida, logs, numObjetos, posicionFinal, parametroS, parametroC);

        final Estructura resultado = new Estructura(snapshots, logs);
        return resultado;

    }

}
