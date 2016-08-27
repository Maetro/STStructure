/**
 * LogHelper.java 25-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;

/**
 * The Class LogHelper.
 */
public class LogHelper {

    private static final Integer POSICION_REAPARICION_RELATIVA = 3;
    private static final Integer POSICION_REAPARICION_ABSOLUTA = 1;

    /**
     * Serializar k2 tree.
     *
     * @param log
     *            the log
     * @param S
     *            the s
     * @param C
     *            the c
     * @return the byte[]
     */
    public static byte[] serializarLog(final Log log, final int S, final int C) {

        final List<Byte> resultado = new ArrayList<Byte>();

        Integer reserva = null;
        // Hay un valor por cada movimiento. se guardan en
        final List<Integer> idObjetos = new ArrayList<Integer>(log.getObjetoMovimientoMap().keySet());
        final List<MovimientoComprimido> movimientos = new ArrayList<MovimientoComprimido>();
        final List<Short> objetosConMovimiento = new ArrayList<Short>();
        short numMovs = 0;
        for (final Integer idObjeto : idObjetos) {
            final MovimientoComprimido codedNumer = log.getObjetoMovimientoMap().get(idObjeto);
            if (codedNumer != null) {
                objetosConMovimiento.add(idObjeto.shortValue());
                numMovs++;
                movimientos.add(codedNumer);
            }
        }
        // Numero de movimientos del Log
        anadirShortAListaBytes(resultado, numMovs);
        // Los ids de los objetos que se mueven
        for (final Short objetoConMovimiento : objetosConMovimiento) {
            anadirShortAListaBytes(resultado, objetoConMovimiento);
        }
        // Los movimientos
        for (final MovimientoComprimido codedNumer : movimientos) {

            final List<Integer> movimiento = codedNumer.getMovimiento();
            if (movimiento.get(0) == POSICION_REAPARICION_ABSOLUTA) {
                // A id: 615 word: [1, 13562, 15786]
                reserva = convertirMovimientoABytes(resultado, reserva, Arrays.asList(movimiento.get(0)));
                reserva = convertirShortABytes(resultado, reserva, movimiento.get(1).shortValue());
                reserva = convertirShortABytes(resultado, reserva, movimiento.get(2).shortValue());
            } else {
                reserva = convertirMovimientoABytes(resultado, reserva, movimiento);
            }

        }
        // Si aun queda un dato en la reserva lo apuntamos
        if (reserva != null) {
            resultado.add(unirEnterosEnByte(reserva, 0));
        }
        reserva = null;

        final Byte[] bytes = resultado.toArray(new Byte[resultado.size()]);
        return ArrayUtils.toPrimitive(bytes);

    }

    /**
     * Convertir movimiento a bytes.
     *
     * @param resultado
     *            resultado
     * @param reserva
     *            reserva
     * @param movimiento
     *            movimiento
     * @return the integer
     */
    private static Integer convertirMovimientoABytes(final List<Byte> resultado, Integer reserva,
            final List<Integer> movimiento) {
        for (final Integer cod : movimiento) {

            if (reserva == null) {
                reserva = cod;
            } else {

                resultado.add(unirEnterosEnByte(reserva, cod));
                reserva = null;
            }
        }
        return reserva;
    }

    /**
     * Convertir movimiento a bytes.
     *
     * @param resultado
     *            resultado
     * @param reserva
     *            reserva
     * @param movimiento
     *            movimiento
     * @return the integer
     */
    private static Integer convertirShortABytes(final List<Byte> resultado, Integer reserva,
            final Short pos) {

        if (reserva == null) {
            anadirShortAListaBytes(resultado, pos);
        } else {
            // Los primeros 4 bits del Short
            // A id: 615 word: [1, 13562, 15786]
            final int cod = pos >> 12;
            resultado.add(unirEnterosEnByte(reserva, cod));
            int medio = pos << 20;
            medio = medio >> 24;
            anadirByteAListaBytes(resultado, new Byte((byte) medio));
            // En la reserva quedan los 4 ultimos
            reserva = pos & 0x0f;

        }

        return reserva;
    }

    /**
     * Anadir entero a lista bytes.
     *
     * @param resultado
     *            the resultado
     * @param num
     *            the num
     */
    private static void anadirEnteroAListaBytes(final List<Byte> resultado, final Integer num) {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

    private static void anadirByteAListaBytes(final List<Byte> resultado, final Byte num) {

        resultado.add(num);

    }

    /**
     * Anadir short a lista bytes.
     *
     * @param resultado
     *            the resultado
     * @param num
     *            the num
     */
    private static void anadirShortAListaBytes(final List<Byte> resultado, final Short num) {
        final byte[] bytes = ByteBuffer.allocate(2).putShort(num).array();
        for (final byte b : bytes) {
            resultado.add(new Byte(b));
        }
    }

    /**
     * Unir enteros en byte.
     *
     * @param a
     *            the a
     * @param b
     *            the b
     * @return the byte
     */
    private static byte unirEnterosEnByte(final Integer a, final Integer b) {
        byte b1 = Integer.valueOf((a << 4)).byteValue();
        final byte b2 = b.byteValue();
        b1 += b2;
        return b1;
    }

    /**
     * Descomprimir logs.
     *
     * @param estructuraComprimida
     *            the estructura comprimida
     * @param logs
     *            the logs
     * @param numObjetos
     *            the num objetos
     * @param pos
     *            the pos
     * @param S
     *            the s
     * @param C
     *            the c
     */
    public static void descomprimirLogs(final byte[] estructuraComprimida, final Map<Integer, Log> logs,
            final int numObjetos,
            final int pos, final int S, final int C) {

        final SCDenseCoder encoder = new SCDenseCoder(S, C);
        LogPositionBean datos = new LogPositionBean(0, true, pos, null);
        byte[] slice;
        int instant = 1;

        while (datos.getPos() < estructuraComprimida.length) {
            datos.setPart2(0);
            datos.setUsado(true);
            datos.setWord(null);
            System.out.println("LOG: " + instant);

            List<Integer> word = new ArrayList<Integer>();
            slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
            // ByteBuffer.wrap(Arrays.copyOfRange(estructuraComprimida, 216468, 216470)).getShort()
            datos.setPos(datos.getPos() + 2);
            final Short numeroMovimientos = ByteBuffer.wrap(slice).getShort();
            final List<Short> objetos = new ArrayList<Short>();
            for (int i = 0; i < numeroMovimientos; i++) {
                slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
                datos.setPos(datos.getPos() + 2);
                final Short idObjeto = ByteBuffer.wrap(slice).getShort();
                objetos.add(idObjeto);
                System.out.println(idObjeto);

            }
            final Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();

            for (int i = 0; i < numeroMovimientos; i++) {
                word = new ArrayList<Integer>();
                datos = obtenerPalabraMovimientoComprimido(estructuraComprimida, encoder, word, datos);

                final Integer posWord = encoder.decode(word);

                if (posWord == POSICION_REAPARICION_RELATIVA) {
                    System.out.println("idObjeto: " + objetos.get(i) + " R: ");
                    datos = obtenerPalabraMovimientoComprimido(estructuraComprimida, encoder, word, datos);
                }

                if (posWord == POSICION_REAPARICION_ABSOLUTA) {
                    if (!datos.isUsado()) {

                        datos = obtenerPalabraDeMovimientoAbsolutoImpar(estructuraComprimida, word, objetos,
                                datos, i);
                    } else {
                        datos = obtenerPalabraDeMovimientoAbsolutoPar(estructuraComprimida, word, datos);
                    }

                }
                objetoMovimientoMap.put(objetos.get(i).intValue(), new MovimientoComprimido(word));
                System.out.println("datos.getPos(): " + datos.getPos());
                System.out.println("idObjeto: " + objetos.get(i) + " Word: " + word + " Decode: "
                        + encoder.decode(word));

            }
            final Log log = new Log(objetoMovimientoMap);
            logs.put(instant, log);
            instant++;
        }

    }

    private static LogPositionBean obtenerPalabraMovimientoComprimido(final byte[] estructuraComprimida,
            final SCDenseCoder encoder, final List<Integer> word, LogPositionBean datos) {
        byte[] slice;
        int part1;
        if (!datos.isUsado()) {
            word.add(datos.getPart2());
            datos.setUsado(true);
            datos = completarWord(estructuraComprimida, encoder, word, datos);
        } else {
            // System.out.println("datos.getPos(): " + datos.getPos());
            slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 1);
            datos.setPos(datos.getPos() + 1);
            part1 = obtenerChunk4bits(0, slice);
            datos.setPart2(obtenerChunk4bits(1, slice));
            datos.setUsado(false);
            word.add(part1);
            datos = completarWord(estructuraComprimida, encoder, word, datos);
        }
        return datos;
    }

    private static LogPositionBean obtenerPalabraDeMovimientoAbsolutoPar(final byte[] estructuraComprimida,
            final List<Integer> word, final LogPositionBean datos) {
        byte[] slice;
        // X
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
        datos.setPos(datos.getPos() + 2);
        word.add((int) ByteBuffer.wrap(slice).getShort());
        // Y
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
        datos.setPos(datos.getPos() + 2);
        word.add((int) ByteBuffer.wrap(slice).getShort());

        return datos;
    }

    private static LogPositionBean obtenerPalabraDeMovimientoAbsolutoImpar(final byte[] estructuraComprimida,

            final List<Integer> word, final List<Short> objetos, final LogPositionBean datos, final int i) {
        byte[] slice;
        // A id: 740 word: [1, 15014, 16554]
        // X
        int x = datos.getPart2() << 12;
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
        short bloque = ByteBuffer.wrap(slice).getShort();
        // Nos sobran los ultimos 4 bits que almacenamos en part2
        bloque = (short) (bloque >>> 4);
        if (bloque < 0) {
            bloque = (short) (bloque + 4096);
        }
        datos.setPos(datos.getPos() + 1);

        x = x + bloque;
        // System.out.println("datos.getPos(): " + datos.getPos());
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 1);
        datos.setPos(datos.getPos() + 1);

        datos.setPart2(obtenerChunk4bits(1, slice));

        word.add(x);
        // Y
        int y = datos.getPart2() << 12;
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 2);
        bloque = ByteBuffer.wrap(slice).getShort();
        // Nos sobran los ultimos 4 bits que almacenamos en part2
        bloque = (short) (bloque >>> 4);
        if (bloque < 0) {
            bloque = (short) (bloque + 4096);
        }
        datos.setPos(datos.getPos() + 1);
        y = y + bloque;
        slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 1);
        datos.setPos(datos.getPos() + 1);

        datos.setPart2(obtenerChunk4bits(1, slice));

        word.add(y);
        System.out.println("idObjeto: " + objetos.get(i) + " Reap. ABS   X: " + x + " y: "
                + y);

        return datos;
    }

    private static LogPositionBean completarWord(final byte[] estructuraComprimida, final SCDenseCoder encoder,
            final List<Integer> word, final LogPositionBean datos) {
        byte[] slice;
        int part1;
        while (!encoder.wordComplete(word)) {
            if (datos.isUsado()) {
                System.out.println("datos.getPos(): " + datos.getPos());
                slice = Arrays.copyOfRange(estructuraComprimida, datos.getPos(), datos.getPos() + 1);
                datos.setPos(datos.getPos() + 1);
                part1 = obtenerChunk4bits(0, slice);
                datos.setPart2(obtenerChunk4bits(1, slice));
                datos.setUsado(false);
                word.add(part1);
            } else {
                word.add(datos.getPart2());
                datos.setUsado(true);
            }
        }
        return datos;
    }

    private static Integer obtenerChunk4bits(final int pos, final byte[] array) {

        if ((pos % 2) == 0) {
            // System.out.println(pos + " " + ((array[pos / 2] & 0xF0) >> 4));
            return (array[pos / 2] & 0xF0) >> 4; // unsigned bit shift
        } else {
            // System.out.println(pos + " " + (array[pos / 2] & 0x0F));
            return array[pos / 2] & 0x0F;
        }

    }
}
