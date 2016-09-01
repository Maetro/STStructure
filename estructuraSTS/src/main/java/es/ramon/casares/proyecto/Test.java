package es.ramon.casares.proyecto;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.log.Log;
import es.ramon.casares.proyecto.modelo.log.Movimiento;
import es.ramon.casares.proyecto.modelo.log.MovimientoComprimido;
import es.ramon.casares.proyecto.modelo.matrix.Posicion;
import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;

public class Test {

    public void probarGeneracionK2TreeyLogs(final ConfiguracionHelper configuracion, final File ficheroFrecuencias)
            throws NumberFormatException, IOException {
        final List<ObjetoMovil> listaInfo = new ArrayList<ObjetoMovil>();
        listaInfo.add(new ObjetoMovil(1, 0, 4, 13));
        listaInfo.add(new ObjetoMovil(2, 0, 13, 9));
        listaInfo.add(new ObjetoMovil(3, 0, 3, 4));
        listaInfo.add(new ObjetoMovil(4, 0, 10, 13));
        listaInfo.add(new ObjetoMovil(5, 0, 6, 8));
        listaInfo.add(new ObjetoMovil(6, 0, 13, 1));
        listaInfo.add(new ObjetoMovil(7, 0, 14, 14));
        listaInfo.add(new ObjetoMovil(8, 0, 4, 0));
        listaInfo.add(new ObjetoMovil(9, 0, 9, 1));
        listaInfo.add(new ObjetoMovil(10, 0, 13, 6));

        final K2Tree tree = K2TreeHelper.generarK2Tree(listaInfo, ControladorHelper.numeroCuadradosSegunLimite(14),
                configuracion.getMinimumSquare());

        final RandomAccessFile datareader = new RandomAccessFile(ficheroFrecuencias, "r");
        String currentLine;
        final List<Integer> movimientosPorFrecuencia = new ArrayList<Integer>();
        while ((currentLine = datareader.readLine()) != null) {
            movimientosPorFrecuencia.add(Integer.valueOf(currentLine));
        }

        final SCDenseCoder encoder = new SCDenseCoder(configuracion.getS(), configuracion.getC());

        int numeroEspiral = ControladorHelper.unidimensionar(1, 1);
        int posicionNumero = movimientosPorFrecuencia.indexOf(numeroEspiral);
        final List<Integer> word1 = encoder.encode(posicionNumero);
        Collections.reverse(word1);
        final MovimientoComprimido mov1 = new MovimientoComprimido(word1);

        numeroEspiral = ControladorHelper.unidimensionar(-7, -9);
        posicionNumero = movimientosPorFrecuencia.indexOf(numeroEspiral);
        final List<Integer> word2 = encoder.encode(posicionNumero);
        Collections.reverse(word2);
        final MovimientoComprimido mov2 = new MovimientoComprimido(word2);

        final Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();
        objetoMovimientoMap.put(5, mov1);
        final Log log1 = new Log(objetoMovimientoMap);

        final Map<Integer, MovimientoComprimido> objetoMovimientoMap2 = new HashMap<Integer, MovimientoComprimido>();
        objetoMovimientoMap2.put(5, mov2);
        final Log log2 = new Log(objetoMovimientoMap2);

        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 1, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 2, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 3, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 4, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 5, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 6, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 7, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 8, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 9, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 10, 16));

        final Posicion pos = K2TreeHelper.obtenerPosicionEnSnapshot(tree, 5, 16);
        int movAntesEspiral = movimientosPorFrecuencia.get(encoder.decode(log1.getObjetoMovimientoMap().get(5)
                .getMovimiento()));
        Movimiento mov = ControladorHelper.obtenerMovimiento(movAntesEspiral);
        pos.setX(pos.getX() + mov.getX());
        pos.setY(pos.getY() + mov.getY());

        movAntesEspiral = movimientosPorFrecuencia.get(encoder.decode(log2.getObjetoMovimientoMap().get(5)
                .getMovimiento()));
        mov = ControladorHelper.obtenerMovimiento(movAntesEspiral);
        pos.setX(pos.getX() + mov.getX());
        pos.setY(pos.getY() + mov.getY());

        System.out.println("Posicion final: " + pos);

    }

    public void probarGeneracionK2TreeyBusqueda(final ConfiguracionHelper configuracion) {
        final List<ObjetoMovil> listaInfo = new ArrayList<ObjetoMovil>();
        listaInfo.add(new ObjetoMovil(1110, 0, 11897, 22982));

        final K2Tree tree = K2TreeHelper.generarK2Tree(listaInfo, ControladorHelper.numeroCuadradosSegunLimite(35000),
                configuracion.getMinimumSquare());

        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(tree, 1110,
                ControladorHelper.numeroCuadradosSegunLimite(35000)));

    }

    public static void probarLocalizacionObjetosEnK2Tree() {
        ControladorHelper.numeroCuadradosSegunLimite(33271);
        final ArrayList<Short> listaIds = new ArrayList<Short>();
        listaIds.add((short) 1);
        listaIds.add((short) 5);
        listaIds.add((short) 4);
        listaIds.add((short) 7);
        listaIds.add((short) 2);
        listaIds.add((short) 3);
        listaIds.add((short) 8);
        listaIds.add((short) 10);
        listaIds.add((short) 9);
        listaIds.add((short) 6);

        final byte b1 = -11;
        final byte b2 = -39;
        final byte b3 = 114;
        final byte b4 = 17;
        final byte b5 = 66;
        final byte b6 = 18;
        final byte b7 = -126;
        final byte b72 = 32;

        final byte b8 = -126;
        final byte b9 = -126;
        final byte b10 = 65;
        final byte b11 = 33;
        final byte b12 = 68;

        final K2Tree k = new K2Tree(Arrays.asList(b1, b2, b3, b4, b5, b6, b7, b72),
                Arrays.asList(b8, b9, b10, b11, b12),
                listaIds);

        K2TreeHelper.obtenerPosicionEnSnapshot(k, 1, 16);
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 1, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 2, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 3, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 4, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 5, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 6, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 7, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 8, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 9, 16));
        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot(k, 10, 16));
    }

}
