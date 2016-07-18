/**
 * PosicionInstante.java 04-jul-2016
 */
package es.ramon.casares.proyecto.modelo.matrix;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Class PosicionInstante.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class PosicionInstante extends Posicion {

    /** The instante. */
    protected final Integer instante;

    /**
     * Instancia un nuevo posicion instante.
     * 
     * @param x
     *            x
     * @param y
     *            y
     * @param instante
     *            instante
     */
    public PosicionInstante(final Integer x, final Integer y, final Integer instante) {
        super(x, y);
        this.instante = instante;
    }

    public Integer getInstante() {
        return this.instante;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PosicionInstante)) {
            return false;
        }
        final PosicionInstante castOther = (PosicionInstante) other;
        return new EqualsBuilder().append(this.x, castOther.x).append(this.y, castOther.y)
                .append(this.instante, castOther.instante).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).append(this.instante).toHashCode();
    }

}
