/**
 * ControladorHelper.java 11-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ramon.casares.proyecto.modelo.estructura.log.Movimiento;
import es.ramon.casares.proyecto.parametros.LimitesBean;

public class FunctionUtils {

    /** The logger. */
    private final static Logger logger = LoggerFactory.getLogger(FunctionUtils.class);

    // calculate haversine distance for linear distance
    /**
     * Haversine_km.
     * 
     * @param lat1
     *            lat1
     * @param long1
     *            long1
     * @param lat2
     *            lat2
     * @param long2
     *            long2
     * @return the double
     */
    public static double haversine_km(final double lat1, final double long1, final double lat2, final double long2) {
        final double d2r = Math.PI / 180.0;
        final double dlong = (long2 - long1) * d2r;
        final double dlat = (lat2 - lat1) * d2r;
        final double a = Math.pow(Math.sin(dlat / 2.0), 2)
                + (Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlong / 2.0), 2));
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final double d = 6367 * c;

        return d;
    }

    /* La clave debe ser X:Y */
    /**
     * Unidimensionar.
     * 
     * @param x
     *            x
     * @param y
     *            y
     * @return the int
     */
    public static int unidimensionar(final Integer x, final Integer y) {

        final int max = Math.max(Math.abs(x), Math.abs(y));
        final int max2 = max * 2;
        // max2 siempre sera par por lo que se tratará de la esquina
        // inferior izquierda de la espiral
        if ((x == -max) || (y == -max)) {
            // Esta en uno de los lados de la esquina inferior izquierda
            final int deltaX = x - (-max);
            final int deltaY = y - (-max);
            if (deltaX == 0) {
                // Lado izquierdo
                return (int) Math.pow(max2, 2) + deltaY;
            } else {
                // Lado abajo
                return (int) Math.pow(max2, 2) - deltaX;
            }
        } else {
            // Esta en uno de los lados de la esquina superior derecha
            if ((x == max) && (x != y)) {
                // Lado derecho
                final int temp = max2 - 1;
                return ((int) Math.pow(temp, 2) + ((max - 1) - y));

            } else {
                // Lado superior
                final int temp = max2 + 1;
                return ((int) Math.pow(temp, 2) - ((max + 1) - x));
            }

        }

    }

    public static Movimiento obtenerMovimiento(final int mov) {
        final int temp = (int) Math.round(Math.sqrt(mov));
        final int temp2 = (int) Math.pow(temp, 2);
        if ((temp % 2) == 0) {
            // Par
            final int dividido = temp / 2;

            if (temp2 > mov) {
                // Lado Abajo
                return new Movimiento(((-dividido) + (temp2 - mov)), -dividido);
            } else {
                // Lado Izquierdo
                return new Movimiento(-dividido, ((-dividido) + (mov - temp2)));

            }
        } else {
            // Impar
            final int dividido = (temp - 1) / 2;
            final int x = dividido + 1;
            final int y = dividido;
            if (temp2 >= mov) {
                // Lado arriba
                return new Movimiento((x - (temp2 - mov)), y);

            } else {
                // Lado derecha
                return new Movimiento(x, (y - (mov - temp2)));
            }
        }

    }

    /**
     * Sort by value.
     * 
     * @param <K>
     *            tipo de clave
     * @param <V>
     *            tipo de valor
     * @param map
     *            map
     * @return the map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        final Map<K, V> result = new LinkedHashMap<K, V>();
        for (final Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static LimitesBean analizadorDeLimites(final File dataSet) throws IOException {
        int limitesCuadrado = 0;
        int idObjetoMayo = 0;
        final RandomAccessFile datareader = new RandomAccessFile(dataSet, "r");
        String currentLine;
        while ((currentLine = datareader.readLine()) != null) {
            final String[] result = currentLine.trim().split("\\s");
            if (result.length >= 3) {
                logger.debug("Limites: " + result[0]);
                final int id = Integer.valueOf(result[1]);
                final int x = Integer.valueOf(result[2]); // Longitud
                final int y = Integer.valueOf(result[3]); // Latitud
                if (id > idObjetoMayo) {
                    idObjetoMayo = id;
                }
                if (x > limitesCuadrado) {
                    limitesCuadrado = x;
                }
                if (y > limitesCuadrado) {
                    limitesCuadrado = y;
                }
            }
        }
        final LimitesBean limites = new LimitesBean(limitesCuadrado, idObjetoMayo);
        return limites;

    }

    public static Integer numeroCuadradosSegunLimite(final int limit) {
        int i = 1;
        while (Math.pow(2, i) < limit) {
            i++;
        }
        final Integer limite = (int) Math.pow(2, i);
        return limite;
    }

    /**
     * Son diferencias dentro de limites.
     * 
     * @param diferenciaInstantes
     *            the diferencia instantes
     * @param diferenciaX
     *            the diferencia x
     * @param diferenciaY
     *            the diferencia y
     * @return true, if successful
     */
    public static boolean sonDiferenciasDentroDeLimites(final int diferenciaX,
            final int diferenciaY, final int limite) {
        return (Math.abs(diferenciaX) <= (limite))
                && (Math.abs(diferenciaY) <= (limite))
                && ((Math.abs(diferenciaX) + Math.abs(diferenciaY)) <= ((limite * 2) - 1));
    }

    public static List<Integer> obtenerMovimientoInterno(final List<Integer> movimientoDoble,
            final int S) {
        int posicion = 0;
        for (final Integer entero : movimientoDoble) {
            posicion++;
            if (entero < S) {
                break;
            }

        }
        return movimientoDoble.subList(posicion, movimientoDoble.size());
    }

}
