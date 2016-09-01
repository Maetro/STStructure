/**
 * LineaEntradaParseada.java 02-ago-2016
 *
 */
package es.ramon.casares.proyecto.util.parser;

/**
 * The Class LineaEntradaParseada.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class LineaEntradaParseada {

    private Integer idObjeto;
    private Integer instante;
    private Double posX;
    private Double posY;

    /**
     * Instancia un nuevo linea entrada parseada.
     */
    public LineaEntradaParseada() {
    }

    /**
     * Instancia un nuevo linea entrada parseada.
     * 
     * @param idObjeto
     *            id objeto
     * @param instante
     *            instante
     * @param posX
     *            pos x
     * @param posY
     *            pos y
     */
    public LineaEntradaParseada(final Integer idObjeto, final Integer instante, final Double posX, final Double posY) {
        super();
        this.idObjeto = idObjeto;
        this.instante = instante;
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Obtiene id objeto.
     * 
     * @return id objeto
     */
    public Integer getIdObjeto() {
        return this.idObjeto;
    }

    /**
     * Establece id objeto.
     * 
     * @param idObjeto
     *            nuevo id objeto
     */
    public void setIdObjeto(final Integer idObjeto) {
        this.idObjeto = idObjeto;
    }

    /**
     * Obtiene instante.
     * 
     * @return instante
     */
    public Integer getInstante() {
        return this.instante;
    }

    /**
     * Establece instante.
     * 
     * @param instante
     *            nuevo instante
     */
    public void setInstante(final Integer instante) {
        this.instante = instante;
    }

    /**
     * Obtiene pos x.
     * 
     * @return pos x
     */
    public Double getPosX() {
        return this.posX;
    }

    /**
     * Establece pos x.
     * 
     * @param posX
     *            nuevo pos x
     */
    public void setPosX(final Double posX) {
        this.posX = posX;
    }

    /**
     * Obtiene pos y.
     * 
     * @return pos y
     */
    public Double getPosY() {
        return this.posY;
    }

    /**
     * Establece pos y.
     * 
     * @param posY
     *            nuevo pos y
     */
    public void setPosY(final Double posY) {
        this.posY = posY;
    }

}
