/**
 * K2TreeHelper.java 16-jun-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.snapshot.k2tree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;

import es.ramon.casares.proyecto.modelo.matrix.InformacionInstanteObjeto;
import es.ramon.casares.proyecto.modelo.matrix.MatrixOfPositions;
import es.ramon.casares.proyecto.modelo.matrix.Posicion;
import es.ramon.casares.proyecto.modelo.matrix.RegionAnalizarBean;
import no.uib.cipr.matrix.MatrixEntry;

/**
 * The Class K2TreeHelper.
 */
public class K2TreeHelper {

    /**
     * Generar k2 tree.
     * 
     * @param listaInfo
     *            the lista info
     * @return the k2 tree
     */
    public static K2Tree generarK2Tree(final List<InformacionInstanteObjeto> listaInfo, final Integer limites,
            final Integer minimumSquare) {
        regionesPendientesAnalizar = new LinkedList<RegionAnalizarBean>();
        final K2Tree snapshot = new K2Tree();
        T = new ArrayList<Integer>();
        L = new ArrayList<Integer>();
        final ArrayList<Short> idsObjetos = new ArrayList<Short>();
        final MatrixOfPositions matrizPosiciones = new MatrixOfPositions(limites, minimumSquare);
        final Integer numeroCeldas = matrizPosiciones.getNumCeldas();
        for (final InformacionInstanteObjeto informacionInstanteObjeto : listaInfo) {
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

    private static void analizarRegion(final MatrixOfPositions matriz, final Integer puntoXinferior,
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

    private static Integer analisisCuadradoFinal(final MatrixOfPositions matriz, final Integer puntoXinferior,
            final Integer puntoYinferior,
            final Integer puntoXsuperior, final Integer puntoYsuperior, Integer datos,
            final ArrayList<Short> idsObjetos) {

        System.out.println("Analisis cuadrado: [" + puntoXsuperior + "," + puntoYsuperior + "]");
        System.out.println("                 : [" + puntoXinferior + "," + puntoYinferior + "]");

        // Esquina SuperiorIzquierda
        Double valor = matriz.getMatriz().get(puntoYsuperior, puntoXinferior);
        if (valor != 0d) {
            datos += 8;
            idsObjetos.add(valor.shortValue());
        }

        // Esquina SuperiorDerecha
        valor = matriz.getMatriz().get(puntoYsuperior, puntoXsuperior);
        if (valor != 0d) {
            datos += 4;
            idsObjetos.add(valor.shortValue());
        }
        // Esquina InferiorIzquierda
        valor = matriz.getMatriz().get(puntoYinferior, puntoXinferior);
        if (valor != 0d) {

            datos += 2;
            idsObjetos.add(valor.shortValue());
        }
        // Esquina InferiorDerecha
        valor = matriz.getMatriz().get(puntoYinferior, puntoXsuperior);
        if (valor != 0d) {

            datos += 1;
            idsObjetos.add(valor.shortValue());
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
    private static Integer analisisCuadrado(final MatrixOfPositions matriz, final Integer puntoXinferior,
            final Integer puntoYinferior,
            final Integer puntoXsuperior, final Integer puntoYsuperior, Integer datos, final Integer puntoMedioY,
            final Integer puntoMedioX, final Integer nivel) {

        System.out.println("Analisis cuadrado: [" + puntoXsuperior + "," + puntoYsuperior + "]");
        System.out.println("                 : [" + puntoXinferior + "," + puntoYinferior + "]");
        // Esquina SuperiorIzquierda

        if (hayValoresEnRegion(matriz, puntoXinferior, puntoMedioY + 1, puntoMedioX, puntoYsuperior)) {
            datos += 8;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior, puntoMedioY + 1, puntoMedioX,
                    puntoYsuperior, nivel));
            System.out.print("[*,");
        } else {
            System.out.print("[ ,");
        }
        // Esquina SuperiorDerecha
        if (hayValoresEnRegion(matriz, puntoMedioX + 1, puntoMedioY + 1, puntoXsuperior, puntoYsuperior)) {
            datos += 4;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1, puntoMedioY + 1, puntoXsuperior,
                    puntoYsuperior, nivel));
            System.out.println("*]");
        } else {
            System.out.println(" ]");
        }
        // Esquina InferiorIzquierda
        if (hayValoresEnRegion(matriz, puntoXinferior, puntoYinferior, puntoMedioX, puntoMedioY)) {
            datos += 2;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoXinferior, puntoYinferior, puntoMedioX,
                    puntoMedioY, nivel));
            System.out.print("[*,");
        } else {
            System.out.print("[ ,");
        }
        // Esquina InferiorDerecha
        if (hayValoresEnRegion(matriz, puntoMedioX + 1, puntoYinferior, puntoXsuperior, puntoMedioY)) {
            datos += 1;
            regionesPendientesAnalizar.add(new RegionAnalizarBean(puntoMedioX + 1, puntoYinferior, puntoXsuperior,
                    puntoMedioY, nivel));
            System.out.println("*]");
        } else {
            System.out.println(" ]");
        }
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
    public static boolean hayValoresEnRegion(final MatrixOfPositions matriz, final Integer puntoXinferior,
            final Integer puntoYinferior, final Integer puntoXsuperior, final Integer puntoYsuperior) {
        final Iterator<MatrixEntry> iterador = matriz.getMatriz().iterator();
        while (iterador.hasNext()) {
            final MatrixEntry sig = iterador.next();
            if ((sig.row() >= puntoYinferior) && (sig.row() <= puntoYsuperior)) {
                if ((sig.column() >= puntoXinferior) && (sig.column() <= puntoXsuperior)) {
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

    public static Posicion obtenerPosicionEnSnapshot(final K2Tree snapshot, final Integer idObjeto) {
        // reconstruirMatriz(snapshot);

        return new Posicion(0, 0);
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

        anadirEnteroAListaBytes(resultado, bytesT);
        anadirEnteroAListaBytes(resultado, bytesL);
        anadirEnteroAListaBytes(resultado, bytesPerm);

        for (final Byte t : k2Tree.getT()) {
            resultado.add(t);
        }
        for (final Byte l : k2Tree.getL()) {
            resultado.add(l);
        }
        for (final Short perm : k2Tree.getIdsObjetos()) {
            anadirShortAListaBytes(resultado, perm);
        }

        final Byte[] bytes = resultado.toArray(new Byte[resultado.size()]);
        return ArrayUtils.toPrimitive(bytes);

    }

    private static void anadirEnteroAListaBytes(final List<Byte> resultado, final Integer num) {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

    private static void anadirShortAListaBytes(final List<Byte> resultado, final Short num) {
        final byte[] bytes = ByteBuffer.allocate(2).putShort(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

}
