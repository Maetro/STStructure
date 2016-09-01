/**
 * SCDenseCoder.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.encoder;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SCDenseCoder.
 */
public class SCDenseCoder {

    /** The parametro s. */
    private Integer parametroS;

    /** The parametro c. */
    private Integer parametroC;

    /**
     * Instantiates a new SC dense coder.
     * 
     * @param parametroSp
     *            the parametro sp
     * @param parametroCp
     *            the parametro cp
     */
    public SCDenseCoder(final Integer parametroSp, final Integer parametroCp) {
        super();
        this.parametroS = parametroSp;
        this.parametroC = parametroCp;
    }

    /**
     * Gets the parametro s.
     * 
     * @return the parametro s
     */
    public Integer getParametroS() {
        return this.parametroS;
    }

    /**
     * Sets the parametro s.
     * 
     * @param parametroS
     *            the new parametro s
     */
    public void setParametroS(final Integer parametroS) {
        this.parametroS = parametroS;
    }

    public Integer getParametroC() {
        return this.parametroC;
    }

    public void setParametroC(final Integer parametroC) {
        this.parametroC = parametroC;
    }

    /**
     * Encode.
     * 
     * @param i
     *            the i
     * @return the list
     */
    public final List<Integer> encode(final Integer i) {
        final List<Integer> word = new ArrayList<Integer>();
        Integer pos = i % this.parametroS;
        word.add(pos);
        int x = (int) Math.floor((double) i / (double) this.parametroS);
        while (x > 0) {
            x = x - 1;
            pos = (x % this.parametroC) + this.parametroS;
            word.add(pos);
            x = (int) Math.floor((double) x / (double) this.parametroC);
        }
        return word;
    }

    /**
     * Decode.
     * 
     * @param codeword
     *            the codeword
     * @return the integer
     */
    public final Integer decode(final List<Integer> codeword) {
        int i = 0;
        int k = 0;

        while (codeword.get(k) >= this.parametroS) {
            i = (i * this.parametroC) + (codeword.get(k) - this.parametroS);
            k++;
        }
        i = (i * this.parametroS) + codeword.get(k);
        i = i + base(k + 1);
        return i;

    }

    /**
     * Word complete.
     * 
     * @param codeword
     *            the codeword
     * @return true, if successful
     */
    public final boolean wordComplete(final List<Integer> codeword) {

        final int pos = codeword.size() - 1;

        if (codeword.get(pos) >= this.parametroS) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Base.
     * 
     * @param i
     *            the i
     * @return the integer
     */
    // base[1]=0, base[2]=s, base[3]=s + sc, base[4]= s + sc+ sc2, base[5]= s + sc+ sc2 + sc3
    private Integer base(final int i) {
        int result = 0;
        switch (i) {
        case 1:
            result = 0;
            break;
        case 2:
            result = this.parametroS;
            break;
        case 3:
            result = this.parametroS + (this.parametroS * this.parametroC);
            break;
        case 4:
            result = this.parametroS + (this.parametroS * this.parametroC)
                    + (this.parametroS * this.parametroC * this.parametroC);
            break;
        case 5:
            result = this.parametroS + (this.parametroS * this.parametroC)
                    + (this.parametroS * this.parametroC * this.parametroC)
                    + (this.parametroS * this.parametroC * this.parametroC * this.parametroC);
            break;
        }
        return result;
    }
}
