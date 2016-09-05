/**
 * K2TreeHelper.java 16-jun-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import es.ramon.casares.proyecto.modelo.estructura.snapshot.Snapshot;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.estructura.snapshot.k2tree.Permutation;
import es.ramon.casares.proyecto.modelo.matrix.MatrizDePosiciones;
import es.ramon.casares.proyecto.parametros.RegionAnalizarBean;
import es.ramon.casares.proyecto.util.ByteFileUtil;
import es.ramon.casares.proyecto.util.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.util.objetos.Posicion;
import no.uib.cipr.matrix.MatrixEntry;

/**
 * The Class K2TreeHelper.
 */
public class K2TreeHelper {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(K2TreeHelper.class);

    /**
     * Generar k2 tree.
     *
     * @param listaInfo
     *            the lista info
     * @return the k2 tree
     */
    public static K2Tree generarK2Tree(final List<ObjetoMovil> listaInfo, final Integer limites) {
        regionesPendientesAnalizar = new LinkedList<RegionAnalizarBean>();
        final K2Tree snapshot = new K2Tree();
        T = new ArrayList<Integer>();
        L = new ArrayList<Integer>();
        final ArrayList<Short> idsObjetos = new ArrayList<Short>();
        final MatrizDePosiciones matrizPosiciones = new MatrizDePosiciones(limites);
        final Integer numeroCeldas = matrizPosiciones.getNumCeldas();
        for (final ObjetoMovil informacionInstanteObjeto : listaInfo) {
            matrizPosiciones.anadirObjetoAPosicion(informacionInstanteObjeto);
        }

        analizarRegion(matrizPosiciones, 0, 0, numeroCeldas - 1, numeroCeldas - 1, 1, idsObjetos);

        while (!CollectionUtils.isEmpty(regionesPendientesAnalizar)) {
            final RegionAnalizarBean siguienteRegion = regionesPendientesAnalizar.poll();
            analizarRegion(matrizPosiciones, siguienteRegion.getPuntoXinferior(), siguienteRegion.getPuntoYinferior(),
                    siguienteRegion.getPuntoXsuperior(), siguienteRegion.getPuntoYsuperior(),
                    siguienteRegion.getNivel(), idsObjetos);
        }

        final List<Byte> tBits = comprimir(T);
        if (L.isEmpty()) {
            L.add(0);
        }
        final List<Byte> lBits = comprimir(L);

        snapshot.setIdsObjetos(idsObjetos);
        snapshot.setT(tBits);
        snapshot.setL(lBits);
        return snapshot;

    }

    /**
     * Generar permutacion.
     *
     * @param idsObjetos
     *            ids objetos
     * @return the permutation
     */
    private static Permutation generarPermutacion(final List<Integer> idsObjetos) {
        final Permutation perm = new Permutation();
        final Integer longitud = idsObjetos.size();
        final List<Integer> sampled = new ArrayList<Integer>();
        for (int i = 0; i < longitud; i++) {
            sampled.add(0);
        }
        final Integer t = longitud / 2;
        for (int i = 1; i <= longitud; i++) {

            Integer idEnPosicion = idsObjetos.get(i - 1);
            perm.getPerm().add(idEnPosicion);
            Integer idAnteriorAnterior = -1;
            Integer idAnterior = -1;
            int recorrido = 0;
            boolean atajoTomado = false;
            while (idEnPosicion != i) {
                recorrido++;
                idAnteriorAnterior = idAnterior;
                idAnterior = idEnPosicion;
                if (atajoTomado || (sampled.get(idEnPosicion - 1) == 0)) {
                    idEnPosicion = idsObjetos.get(idEnPosicion - 1);
                } else {
                    atajoTomado = true;
                    final int position = rank1(sampled, idEnPosicion - 1);
                    idEnPosicion = idsObjetos.get(perm.getRev_links().get(position - 1) - 1);
                }

            }
            if (recorrido > t) {
                sampled.set(i - 1, 1);
                perm.getRev_links().add(idAnteriorAnterior);
            } else {
                sampled.set(i - 1, 0);
            }
        }
        perm.setSampled(transformarListaEnBytes(sampled));
        return perm;
    }

    /**
     * Transformar lista en bytes.
     *
     * @param sampled
     *            sampled
     * @return the list
     */
    private static List<Byte> transformarListaEnBytes(final List<Integer> sampled) {
        final List<Byte> resultado = new ArrayList<Byte>();
        final int numbytes = sampled.size() / 8;
        final int hayResto = sampled.size() % 8;
        int contBytes = 0;
        for (int i = 0; i < numbytes; i++) {
            Integer byteEntero = 0;
            for (int pos = 7; pos >= 0; pos--) {
                if (sampled.get((contBytes * 8) + (7 - pos)) != 0) {
                    byteEntero += (int) Math.pow(2, pos);
                }
            }
            resultado.add(byteEntero.byteValue());
            contBytes++;
        }
        if (hayResto != 0) {
            Integer byteEntero = 0;

            final int posfinal = 7 - hayResto;
            for (int pos = 7; pos > (posfinal); pos--) {
                if (sampled.get((contBytes * 8) + (7 - pos)) != 0) {
                    byteEntero += (int) Math.pow(2, pos);
                }
            }
            resultado.add(byteEntero.byteValue());
        }

        return resultado;
    }

    private static Integer rank1(final List<Integer> sampled, final int i) {

        int cont = 0;
        int numberOfOnes = 0;
        for (final Integer integer : sampled) {
            if (integer == 1) {
                numberOfOnes++;
            }
            if (numberOfOnes == i) {
                break;
            }
            cont++;
        }
        return numberOfOnes;

    }

    // Cada numero de la lista representa un numero de 4 bits. podemos unir dos numeros en uno solo
    /**
     * Comprimir.
     *
     * @param listanumeros
     *            listanumeros
     * @return the list
     */
    private static List<Byte> comprimir(final List<Integer> listanumeros) {
        final List<Byte> bytes = new ArrayList<Byte>();
        if (listanumeros.size() > 1) {
            if ((listanumeros.size() % 2) == 0) {
                for (int j = 0; j < listanumeros.size(); j += 2) {
                    bytes.add(transformarEnterosEnByte(listanumeros, j));
                }

            } else {
                for (int j = 0; j < (listanumeros.size() - 1); j += 2) {
                    bytes.add(transformarEnterosEnByte(listanumeros, j));
                }
                bytes.add(unirEnterosEnByte(listanumeros.get(listanumeros.size() - 1), 0));
            }
        } else {
            bytes.add(unirEnterosEnByte(listanumeros.get(0), 0));
        }
        return bytes;
    }

    /**
     * Transformar enteros en byte.
     *
     * @param listanumeros
     *            listanumeros
     * @param position
     *            j
     */
    private static byte transformarEnterosEnByte(final List<Integer> listanumeros, final int position) {
        final Integer a = listanumeros.get(position);
        final Integer b = listanumeros.get(position + 1);
        return unirEnterosEnByte(a, b);
    }

    /**
     * Unir enteros en byte. Ambos enteros entre 0 y 15
     *
     * @param a
     *            a
     * @param b
     *            b
     * @return the byte
     */
    private static byte unirEnterosEnByte(final Integer a, final Integer b) {
        byte b1 = Integer.valueOf((a << 4)).byteValue();
        final byte b2 = b.byteValue();
        b1 += b2;
        return b1;
    }

    private static Queue<RegionAnalizarBean> regionesPendientesAnalizar = new LinkedList<RegionAnalizarBean>();
    private static List<Integer> T = new ArrayList<Integer>();
    private static List<Integer> L = new ArrayList<Integer>();

    private static void analizarRegion(final MatrizDePosiciones matriz, final Integer puntoXinferior,
            final Integer puntoYinferior, final Integer puntoXsuperior, final Integer puntoYsuperior,
            final Integer nivel, final ArrayList<Short> idsObjetos) {

        Integer datos = 0;
        if ((puntoXsuperior - puntoXinferior) <= 1) {
            // Último cuadrado
            datos = analisisCuadradoFinal(matriz, puntoXinferior, puntoYinferior, puntoXsuperior, puntoYsuperior,
                    datos, idsObjetos);
            L.add(datos);
        } else {
            // Por cada ciclo hay que analizar 4 posibilidades.
            final Integer puntoMedioY = (puntoYsuperior + puntoYinferior) / 2;
            final Integer puntoMedioX = (puntoXsuperior + puntoXinferior) / 2;
            datos = analisisCuadrado(matriz, puntoXinferior, puntoYinferior, puntoXsuperior, puntoYsuperior, datos,
                    puntoMedioY, puntoMedioX, nivel + 1);
            T.add(datos);
        }
    }

    private static Integer analisisCuadradoFinal(final MatrizDePosiciones matriz, final Integer puntoXinferior,
            final Integer puntoYinferior, final Integer puntoXsuperior, final Integer puntoYsuperior, Integer datos,
            final ArrayList<Short> idsObjetos) {

        logger.debug("Analisis cuadrado: [" + puntoXsuperior + "," + puntoYsuperior + "]");
        logger.debug(" : [" + puntoXinferior + "," + puntoYinferior + "]");

        // Esquina SuperiorIzquierda
        Double valor = matriz.getMatriz().get(puntoXinferior, puntoYsuperior);
        if (valor != 0d) {
            datos += 8;
            idsObjetos.add(valor.shortValue());
            logger.debug("[" + valor.shortValue() + ",");
        } else {
            logger.debug("[ ,");
        }

        // Esquina SuperiorDerecha
        valor = matriz.getMatriz().get(puntoXsuperior, puntoYsuperior);
        if (valor != 0d) {
            datos += 4;
            idsObjetos.add(valor.shortValue());
            logger.debug(valor.shortValue() + "]");
        } else {
            logger.debug(" ]");
        }
        // Esquina InferiorIzquierda
        valor = matriz.getMatriz().get(puntoXinferior, puntoYinferior);
        if (valor != 0d) {

            datos += 2;
            idsObjetos.add(valor.shortValue());
            logger.debug("[" + valor.shortValue() + ",");
        } else {
            logger.debug("[ ,");
        }
        // Esquina InferiorDerecha
        valor = matriz.getMatriz().get(puntoXsuperior, puntoYinferior);
        if (valor != 0d) {

            datos += 1;
            idsObjetos.add(valor.shortValue());
            logger.debug(valor.shortValue() + "]");
        } else {
            logger.debug(" ]");
        }
        return datos;
    }

    /**
     * Analisis cuadrado.
     *
     * @param matriz
     *            the matriz
     * @param puntoXinferior
     *            the punto xinferior
     * @param puntoYinferior
     *            the punto yinferior
     * @param puntoXsuperior
     *            the punto xsuperior
     * @param puntoYsuperior
     *            the punto ysuperior
     * @param datos
     *            the datos
     * @param puntoMedioY
     *            the punto medio y
     * @param puntoMedioX
     *            the punto medio x
     * @param nivel
     * @return the integer
     */
    private static Integer analisisCuadrado(final MatrizDePosiciones matriz, final Integer puntoXinferior,
            final Integer puntoYinferior,
            final Integer puntoXsuperior, final Integer puntoYsuperior, Integer datos, final Integer puntoMedioY,
            final Integer puntoMedioX, final Integer nivel) {

        logger.debug("Analisis cuadrado: [" + puntoXsuperior + "," + puntoYsuperior + "]");
        logger.debug(" : [" + puntoXinferior + "," + puntoYinferior + "]");
        // Esquina SuperiorIzquierda

        if (hayValoresEnRegion(matriz, puntoXinferior, puntoMedioY + 1, puntoMedioX, puntoYsuperior)) {
            datos += 8;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior, puntoMedioY + 1, puntoMedioX,
                    puntoYsuperior, nivel));

        }
        // Esquina SuperiorDerecha
        if (hayValoresEnRegion(matriz, puntoMedioX + 1, puntoMedioY + 1, puntoXsuperior, puntoYsuperior)) {
            datos += 4;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1, puntoMedioY + 1, puntoXsuperior,
                    puntoYsuperior, nivel));

        }
        // Esquina InferiorIzquierda
        if (hayValoresEnRegion(matriz, puntoXinferior, puntoYinferior, puntoMedioX, puntoMedioY)) {
            datos += 2;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior, puntoYinferior, puntoMedioX,
                    puntoMedioY, nivel));

        }
        // Esquina InferiorDerecha
        if (hayValoresEnRegion(matriz, puntoMedioX + 1, puntoYinferior, puntoXsuperior, puntoMedioY)) {
            datos += 1;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1, puntoYinferior, puntoXsuperior,
                    puntoMedioY, nivel));

        }
        logger.debug("Datos: " + datos);
        return datos;
    }

    /**
     * Hay valores en region.
     *
     * @param matriz
     *            the matriz
     * @param puntoXinferior
     *            the punto xinferior
     * @param puntoYinferior
     *            the punto yinferior
     * @param puntoXsuperior
     *            the punto xsuperior
     * @param puntoYsuperior
     *            the punto ysuperior
     * @return true, if successful
     */
    public static boolean hayValoresEnRegion(final MatrizDePosiciones matriz, final Integer puntoXinferior,
            final Integer puntoYinferior, final Integer puntoXsuperior, final Integer puntoYsuperior) {
        final Iterator<MatrixEntry> iterador = matriz.getMatriz().iterator();
        while (iterador.hasNext()) {
            final MatrixEntry sig = iterador.next();
            if ((sig.column() >= puntoYinferior) && (sig.column() <= puntoYsuperior)) {
                if ((sig.row() >= puntoXinferior) && (sig.row() <= puntoXsuperior)) {
                    if (sig.get() != 0d) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Obtener tamano k2 tree.
     *
     * @param snapshot
     *            snapshot
     * @return the int
     */
    public static int obtenerTamanoK2Tree(final K2Tree snapshot) {
        int numbytes = 0;
        numbytes += 12; // Tamano cabeceras (3 enteros)
        numbytes += snapshot.getL().size();
        numbytes += snapshot.getT().size();
        numbytes += snapshot.getIdsObjetos().size() * 2;
        return numbytes;
    }

    /**
     * Obtener posicion en snapshot.
     *
     * @param snapshot
     *            the snapshot
     * @param idObjeto
     *            the id objeto
     * @param numeroCuadrados
     *            the numero cuadrados
     * @return the posicion
     */
    public static Posicion obtenerPosicionEnSnapshot(final K2Tree snapshot, final Integer idObjeto,
            final Integer numeroCuadrados) {
        int x1 = 0;
        int y1 = 0;
        final int k = 2;
        int x2 = numeroCuadrados - 1;
        int y2 = numeroCuadrados - 1;
        final Stack<Integer> pila = obtenerCamino(snapshot, idObjeto);
        while (!pila.isEmpty()) {
            final Integer posicion = pila.pop();
            final Integer column = posicion % k;
            final Integer row = posicion / k;
            final Integer pivX = ((x2 - x1) + 1) / k;
            final Integer pivY = ((y2 - y1) + 1) / k;
            x2 = ((pivX * (column + 1)) + x1) - 1;
            x1 = (x2 - pivX) + 1;
            y2 = ((pivY * (row + 1)) + y1) - 1;
            y1 = (y2 - pivY) + 1;

        }
        return new Posicion(x1, (numeroCuadrados - 1) - y1);
    }

    /**
     * Serializar k2 tree.
     *
     * @param k2Tree
     *            k2 tree
     * @return the byte[]
     */
    public static byte[] serializarK2Tree(final K2Tree k2Tree) {

        final List<Byte> resultado = new ArrayList<Byte>();
        final Integer bytesT = k2Tree.getT().size();
        final Integer bytesL = k2Tree.getL().size();
        final Integer bytesPerm = k2Tree.getIdsObjetos().size();

        ByteFileUtil.anadirEnteroAListaBytes(resultado, bytesT);
        ByteFileUtil.anadirEnteroAListaBytes(resultado, bytesL);
        ByteFileUtil.anadirEnteroAListaBytes(resultado, bytesPerm);

        for (final Byte t : k2Tree.getT()) {
            resultado.add(t);
        }
        for (final Byte l : k2Tree.getL()) {
            resultado.add(l);
        }
        for (final Short perm : k2Tree.getIdsObjetos()) {
            ByteFileUtil.anadirShortAListaBytes(resultado, perm);
        }

        final Byte[] bytes = resultado.toArray(new Byte[resultado.size()]);
        return ArrayUtils.toPrimitive(bytes);

    }

    public static K2Tree generarK2Tree(final HashMap<Posicion, ObjetoMovil> posicionIds, final Integer limite) {
        final Collection<ObjetoMovil> listaInfoCollection = posicionIds.values();
        final List<ObjetoMovil> listaInfo = new ArrayList<ObjetoMovil>(listaInfoCollection);
        return generarK2Tree(listaInfo, limite);
    }

    /**
     * Descomprimir snapshots. Se empieza en el byte 8 ya que los dos primeros enteros estan reservados.
     *
     * @param estructuraComprimida
     *            estructura comprimida
     * @param snapshots
     *            snapshots
     * @param numSnapshots
     *            num snapshots
     * @param separacionSnapshots
     *            separacion snapshots
     * @return the int
     */
    public static int descomprimirSnapshots(final byte[] estructuraComprimida, final Map<Integer, Snapshot> snapshots,
            final int numSnapshots, final int separacionSnapshots) {

        int pos = 8;
        int snapshotsDescomprimidas = 0;
        byte[] slice;
        while (snapshotsDescomprimidas < numSnapshots) {
            final K2Tree snapshot = new K2Tree();
            slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + ByteFileUtil.TAMANO_INTEGER);
            pos = pos + ByteFileUtil.TAMANO_INTEGER;
            final Integer tamano = ByteBuffer.wrap(slice).getInt();
            slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + ByteFileUtil.TAMANO_INTEGER);
            pos = pos + ByteFileUtil.TAMANO_INTEGER;
            final Integer bytesT = ByteBuffer.wrap(slice).getInt();
            slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + ByteFileUtil.TAMANO_INTEGER);
            pos = pos + ByteFileUtil.TAMANO_INTEGER;
            final Integer bytesL = ByteBuffer.wrap(slice).getInt();
            slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + ByteFileUtil.TAMANO_INTEGER);
            pos = pos + ByteFileUtil.TAMANO_INTEGER;
            final Integer bytesPerm = ByteBuffer.wrap(slice).getInt();
            final List<Byte> T = new ArrayList<Byte>();
            int i = 0;

            for (i = 0; i < bytesT; i++) {
                slice = Arrays.copyOfRange(estructuraComprimida, pos + i, pos + i + 1);
                T.add(ByteBuffer.wrap(slice).get());
            }
            pos = pos + i;
            final List<Byte> L = new ArrayList<Byte>();
            i = 0;
            for (i = 0; i < bytesL; i++) {
                slice = Arrays.copyOfRange(estructuraComprimida, pos + i, pos + i + 1);
                L.add(ByteBuffer.wrap(slice).get());
            }
            pos = pos + i;
            final List<Short> idsObjetos = new ArrayList<Short>();
            i = 0;
            for (i = 0; i < (bytesPerm * 2); i += 2) {
                slice = Arrays.copyOfRange(estructuraComprimida, pos + i, pos + i + 2);
                idsObjetos.add(ByteBuffer.wrap(slice).getShort());
            }
            pos = pos + i;
            snapshot.setT(T);
            snapshot.setL(L);
            snapshot.setIdsObjetos(idsObjetos);
            snapshots.put(snapshotsDescomprimidas * separacionSnapshots, snapshot);
            snapshotsDescomprimidas++;
        }
        return pos;
    }

    /**
     * Descomprimir snapshot.
     *
     * @param fichero
     *            the fichero
     * @return the k2 tree
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static K2Tree descomprimirSnapshot(final RandomAccessFile fichero) throws IOException {

        final K2Tree snapshot = new K2Tree();

        final Integer tamano = fichero.readInt();
        final Integer bytesT = fichero.readInt();
        final Integer bytesL = fichero.readInt();
        final Integer bytesPerm = fichero.readInt();
        final List<Byte> T = new ArrayList<Byte>();
        int i = 0;

        for (i = 0; i < bytesT; i++) {

            T.add(fichero.readByte());
        }

        final List<Byte> L = new ArrayList<Byte>();
        i = 0;
        for (i = 0; i < bytesL; i++) {
            L.add(fichero.readByte());
        }
        final List<Short> idsObjetos = new ArrayList<Short>();
        i = 0;
        for (i = 0; i < (bytesPerm * 2); i += 2) {
            idsObjetos.add(fichero.readShort());
        }

        snapshot.setT(T);
        snapshot.setL(L);
        snapshot.setIdsObjetos(idsObjetos);
        return snapshot;
    }

    /**
     * Obtener camino.
     *
     * @param snapshot
     *            the snapshot
     * @param idObjeto
     *            the id objeto
     * @return the stack
     */
    private static Stack obtenerCamino(final K2Tree snapshot, final int idObjeto) {
        final Stack path = new Stack<>();
        final int p = snapshot.getIdsObjetos().indexOf(new Short((short) idObjeto)) + 1;
        // Posicion en bits

        int x = select(snapshot.getL(), p) + (snapshot.getT().size() * 8);

        if (obtenerChunk4bits(1, snapshot.getT().get(snapshot.getT().size() - 1)) == 0) {
            x = x - 4;
        }
        while (x > 0) {
            final int i = x % 4;
            path.push(i);
            if (x >= 4) {
                x = select(snapshot.getT(), (x / 4));
                if (x == 0) {
                    path.push(x);
                }
            } else {
                x = 0;
            }

        }
        return path;
    }

    private static int obtenerChunk4bits(final int pos, final byte byteActual) {
        if ((pos % 2) == 0) {
            // logger.debug(pos + " " + ((array[pos / 2] & 0xF0) >> 4));
            return (byteActual & 0xF0) >> 4; // unsigned bit shift
        } else {
            // logger.debug(pos + " " + (array[pos / 2] & 0x0F));
            return byteActual & 0x0F;
        }
    }

    private static int select(final List<Byte> l2, int p) {
        int pos = 0;
        for (final Byte byteActual : l2) {
            final int temp = Integer.valueOf(byteActual);
            int numberOfOnes = Integer.bitCount(temp);
            if (temp < 0) {
                numberOfOnes = numberOfOnes - 24;
            }
            if (numberOfOnes < p) {
                pos += 8;
                p = p - numberOfOnes;
            } else {
                final byte b = (byte) temp;
                final int i2 = b & 0xFF;
                final String representation = Integer.toBinaryString(i2);
                Integer number = Integer.valueOf(representation);

                final List<Integer> separados = new ArrayList<Integer>();
                while (number > 0) {
                    separados.add(number % 10);
                    number = number / 10;
                }
                Collections.reverse(separados);
                pos += 8 - separados.size();
                for (final Integer integer : separados) {
                    if (integer == 1) {
                        p--;
                        if (p <= 0) {
                            break;
                        }
                    }
                    pos++;
                }
                break;
            }
        }
        // TODO Auto-generated method stub
        return pos;
    }

}
