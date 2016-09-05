/**
 * Posicion.java 04-jul-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util.objetos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class Posicion.
 *
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class Posicion {

    /** The x. */
    protected int x;

    /** The y. */
    protected int y;

    /**
     * Instancia un nuevo posicion.
     *
     * @param x
     *            x
     * @param y
     *            y
     */
    public Posicion(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene x.
     *
     * @return x
     */
    public int getPosicionX() {
        return this.x;
    }

    /**
     * Gets the x.
     *
     * @return the x
     */
    public int getX() {
        return this.x;
    }

    /**
     * Establece x.
     *
     * @param x
     *            nuevo x
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Obtiene y.
     *
     * @return y
     */
    public int getPosicionY() {
        return this.y;
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    public int getY() {
        return this.y;
    }

    /**
     * Establece y.
     *
     * @param y
     *            nuevo y
     */
    public void setY(final int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("x", this.x).append("y", this.y).toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Posicion)) {
            return false;
        }
        final Posicion castOther = (Posicion) other;
        return new EqualsBuilder().append(this.x, castOther.x).append(this.y, castOther.y).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).toHashCode();
    }

}
