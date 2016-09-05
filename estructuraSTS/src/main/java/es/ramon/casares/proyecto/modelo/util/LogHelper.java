/**
 * LogHelper.java 25-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.estructura.log.Log;
import es.ramon.casares.proyecto.modelo.estructura.log.LogPositionBean;
import es.ramon.casares.proyecto.modelo.estructura.log.MovimientoComprimido;
import es.ramon.casares.proyecto.parametros.ParametrosCompresionLogsBean;

/**
 * The Class LogHelper.
 */
public class LogHelper {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(LogHelper.class);

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
    public static byte[] serializarLog(final Log log, final ParametrosCompresionLogsBean parametros) {

        final List<Byte> resultado = new ArrayList<Byte>();
        final SCDenseCoder encoder = new SCDenseCoder(parametros.getParametroS(), parametros.getParametroC());

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
            final int decodificado = encoder.decode(movimiento);
            if ((decodificado == parametros.getPosicionReaparicionAbsoluta())
                    || (decodificado == parametros.getPosicionReaparicionAbsoluta())) {
                // A id: 615 word: [1, 13562, 15786]
                reserva = convertirMovimientoABytes(resultado, reserva, movimiento);
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

    public static void descomprimirLog(final RandomAccessFile estructura, final Map<Integer, Log> logs,
            final ParametrosCompresionLogsBean parametros, final int lognumber) throws IOException {
        final SCDenseCoder encoder = new SCDenseCoder(parametros.getParametroS(), parametros.getParametroC());
        LogPositionBean datos = new LogPositionBean(0, true, 0, null);

        datos.setPart2(0);
        datos.setUsado(true);
        datos.setWord(null);
        logger.info("LOG: " + (lognumber + 1));

        List<Integer> word = new ArrayList<Integer>();

        final Short numeroMovimientos = estructura.readShort();
        final List<Short> objetos = new ArrayList<Short>();
        for (int i = 0; i < numeroMovimientos; i++) {

            final Short idObjeto = estructura.readShort();
            objetos.add(idObjeto);
            logger.debug(String.valueOf(idObjeto));

        }
        final Map<Integer, MovimientoComprimido> objetoMovimientoMap = new HashMap<Integer, MovimientoComprimido>();

        for (int i = 0; i < numeroMovimientos; i++) {
            word = new ArrayList<Integer>();
            datos = obtenerPalabraMovimientoComprimido(estructura, encoder, word, datos);

            final Integer posWord = encoder.decode(word);

            if (posWord == parametros.getPosicionReaparicionRelativa()) {
                logger.debug("idObjeto: " + objetos.get(i) + " R: ");
                datos = obtenerPalabraMovimientoComprimido(estructura, encoder, word, datos);
            }
            if (posWord == parametros.getPosicionReaparicionFueraLimites()) {
                logger.debug("idObjeto: " + objetos.get(i) + " R F: ");
                if (!datos.isUsado()) {
                    datos = obtenerPalabraDeMovimientoAbsolutoImpar(estructura, word, objetos,
                            datos, i);
                } else {
                    datos = obtenerPalabraDeMovimientoAbsolutoPar(estructura, word, datos);
                }
            }
            if (posWord == parametros.getPosicionReaparicionAbsoluta()) {
                if (!datos.isUsado()) {

                    datos = obtenerPalabraDeMovimientoAbsolutoImpar(estructura, word, objetos,
                            datos, i);
                } else {
                    datos = obtenerPalabraDeMovimientoAbsolutoPar(estructura, word, datos);
                }

            }
            objetoMovimientoMap.put(objetos.get(i).intValue(), new MovimientoComprimido(word));
            logger.debug("datos.getPos(): " + datos.getPos());
            logger.debug("idObjeto: " + objetos.get(i) + " Word: " + word + " Decode: "
                    + encoder.decode(word));

        }
        final Log log = new Log(objetoMovimientoMap);
        logs.put((lognumber + 1), log);

    }

    private static LogPositionBean obtenerPalabraMovimientoComprimido(final RandomAccessFile estructura,
            final SCDenseCoder encoder, final List<Integer> word, LogPositionBean datos) throws IOException {
        final byte[] slice;
        int part1;
        if (!datos.isUsado()) {
            word.add(datos.getPart2());
            datos.setUsado(true);
            datos = completarWord(estructura, encoder, word, datos);
        } else {
            // System.out.println("datos.getPos(): " + datos.getPos());
            final byte byteActual = estructura.readByte();
            part1 = obtenerChunk4bits(0, byteActual);
            datos.setPart2(obtenerChunk4bits(1, byteActual));
            datos.setUsado(false);
            word.add(part1);
            datos = completarWord(estructura, encoder, word, datos);
        }
        return datos;
    }

    private static LogPositionBean obtenerPalabraDeMovimientoAbsolutoPar(final RandomAccessFile estructura,
            final List<Integer> word, final LogPositionBean datos) throws IOException {
        final byte[] slice;
        // X
        word.add((int) estructura.readShort());
        // Y
        word.add((int) estructura.readShort());

        return datos;
    }

    private static LogPositionBean obtenerPalabraDeMovimientoAbsolutoImpar(final RandomAccessFile estructura,
            final List<Integer> word, final List<Short> objetos, final LogPositionBean datos, final int i)
            throws IOException {
        final byte[] slice;

        // A id: 740 word: [1, 15014, 16554]
        // X
        long puntero = estructura.getFilePointer();
        int x = datos.getPart2() << 12;
        short bloque = estructura.readShort();
        // Nos sobran los ultimos 4 bits que almacenamos en part2
        bloque = (short) (bloque >>> 4);
        if (bloque < 0) {
            bloque = (short) (bloque + 4096);
        }
        estructura.seek(puntero + 1);
        x = x + bloque;
        // System.out.println("datos.getPos(): " + datos.getPos());

        byte byteActual = estructura.readByte();

        datos.setPart2(obtenerChunk4bits(1, byteActual));

        word.add(x);
        // Y

        puntero = estructura.getFilePointer();
        int y = datos.getPart2() << 12;

        bloque = estructura.readShort();
        // Nos sobran los ultimos 4 bits que almacenamos en part2
        bloque = (short) (bloque >>> 4);
        if (bloque < 0) {
            bloque = (short) (bloque + 4096);
        }
        estructura.seek(puntero + 1);
        y = y + bloque;

        byteActual = estructura.readByte();
        datos.setPos(datos.getPos() + 1);

        datos.setPart2(obtenerChunk4bits(1, byteActual));

        word.add(y);
        logger.debug("idObjeto: " + objetos.get(i) + " Reap. ABS   X: " + x + " y: "
                + y);

        return datos;
    }

    private static LogPositionBean completarWord(final RandomAccessFile estructura, final SCDenseCoder encoder,
            final List<Integer> word, final LogPositionBean datos) throws IOException {
        final byte[] slice;
        int part1;
        while (!encoder.wordComplete(word)) {
            if (datos.isUsado()) {
                // System.out.println("datos.getPos(): " + datos.getPos());
                final byte byteActual = estructura.readByte();
                part1 = obtenerChunk4bits(0, byteActual);
                datos.setPart2(obtenerChunk4bits(1, byteActual));
                datos.setUsado(false);
                word.add(part1);
            } else {
                word.add(datos.getPart2());
                datos.setUsado(true);
            }
        }
        return datos;
    }

    private static int obtenerChunk4bits(final int pos, final byte byteActual) {
        if ((pos % 2) == 0) {
            // System.out.println(pos + " " + ((array[pos / 2] & 0xF0) >> 4));
            return (byteActual & 0xF0) >> 4; // unsigned bit shift
        } else {
            // System.out.println(pos + " " + (array[pos / 2] & 0x0F));
            return byteActual & 0x0F;
        }
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
