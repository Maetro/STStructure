package es.ramon.casares.proyecto.modelo.log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class LogHelper {

    /**
     * Serializar k2 tree.
     * 
     * @param k2Tree
     *            k2 tree
     * @return the byte[]
     */
    public static byte[] serializarLog(final Log log) {

        final List<Byte> resultado = new ArrayList<Byte>();

        Integer reserva = null;
        // Hay un valor por cada movimiento. se guardan en
        final List<Integer> idObjetos = new ArrayList<Integer>(log.getObjetoMovimientoMap().keySet());
        for (final Integer idObjeto : idObjetos) {
            final MovimientoComprimido codedNumer = log.getObjetoMovimientoMap().get(idObjeto);

            if (codedNumer != null) {
                anadirEnteroAListaBytes(resultado, idObjeto);
                for (final Integer cod : codedNumer.getMovimiento()) {
                    if (reserva == null) {
                        reserva = cod;
                    } else {

                        resultado.add(unirEnterosEnByte(reserva, cod));
                    }

                }
                if (reserva != null) {
                    resultado.add(unirEnterosEnByte(reserva, 0));
                }
            }
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

    private static byte unirEnterosEnByte(final Integer a, final Integer b) {
        byte b1 = Integer.valueOf((a << 4)).byteValue();
        final byte b2 = b.byteValue();
        b1 += b2;
        return b1;
    }

    public static void descomprimirLogs(final byte[] estructuraComprimida, final Map<Integer, Log> logs,
            final int numObjetos,
            int pos) {
        byte[] slice;

        int par = 0;
        final List<Integer> word = new ArrayList<Integer>();
        slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + 4);
        pos = pos + 4;
        final Integer idObjeto = ByteBuffer.wrap(slice).getInt();
        slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + 1);
        int e = obtenerChunk4bits(par, slice);
        if (par == 0) {
            par = 1;
        } else {
            par = 0;
            pos = pos + 1;
        }
        word.add(e);
        while (e <= 5) {
            slice = Arrays.copyOfRange(estructuraComprimida, pos, pos + 1);
            e = obtenerChunk4bits(par, slice);
            if (par == 0) {
                par = 1;
            } else {
                par = 0;
                pos = pos + 1;
            }
            word.add(e);
        }

    }

    private static Integer obtenerChunk4bits(final int pos, final byte[] array) {
        if ((pos % 2) == 0) {
            return array[pos / 2] >>> 4; // unsigned bit shift
        } else {
            return array[pos / 2] & 0x0F;
        }

    }
}
