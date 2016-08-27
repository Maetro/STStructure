/**
 * PosicionKey.java 08-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.objetos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class PosicionKey.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class PosicionKey {

    private int x;

    private int y;

    /**
     * Instancia un nuevo posicion key.
     */
    public PosicionKey() {
    }

    /**
     * Instancia un nuevo posicion key.
     * 
     * @param x
     *            x
     * @param y
     *            y
     */
    public PosicionKey(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene x.
     * 
     * @return x
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PosicionKey)) {
            return false;
        }
        final PosicionKey castOther = (PosicionKey) other;
        return new EqualsBuilder().append(this.x, castOther.x).append(this.y, castOther.y).isEquals();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("x", this.x).append("y", this.y).toString();
    }

}
