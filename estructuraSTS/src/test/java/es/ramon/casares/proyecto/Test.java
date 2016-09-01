package es.ramon.casares.proyecto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;

public class Test {

    public void probarGeneracionK2Tree(final ConfiguracionHelper configuracion) {
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
