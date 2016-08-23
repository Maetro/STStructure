/**
 * Permutation.java 01-jul-2016
 *
 */
package es.ramon.casares.proyecto.modelo.snapshot.k2tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Permutation.
 */
public class Permutation implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3230444669239923767L;

    /** The perm. */
    private ArrayList<Integer> perm = new ArrayList<Integer>();

    /** The sampled. */
    private List<Byte> sampled = new ArrayList<Byte>();

    /** The rev_links. */
    private ArrayList<Integer> rev_links = new ArrayList<Integer>();

    /**
     * Gets the perm.
     * 
     * @return the perm
     */
    public ArrayList<Integer> getPerm() {
        return this.perm;
    }

    /**
     * Sets the perm.
     * 
     * @param perm
     *            the new perm
     */
    public void setPerm(final ArrayList<Integer> perm) {
        this.perm = perm;
    }

    /**
     * Gets the sampled.
     * 
     * @return the sampled
     */
    public List<Byte> getSampled() {
        return this.sampled;
    }

    /**
     * Sets the sampled.
     * 
     * @param sampled
     *            the new sampled
     */
    public void setSampled(final List<Byte> sampled) {
        this.sampled = sampled;
    }

    /**
     * Gets the rev_links.
     * 
     * @return the rev_links
     */
    public ArrayList<Integer> getRev_links() {
        return this.rev_links;
    }

    /**
     * Sets the rev_links.
     * 
     * @param rev_links
     *            the new rev_links
     */
    public void setRev_links(final ArrayList<Integer> rev_links) {
        this.rev_links = rev_links;
    }

    /**
     * Obtiene number of bytes.
     * 
     * @return number of bytes
     */
    public int getNumberOfBytes() {
        int numBytes = 0;
        numBytes += this.rev_links.size() * 4;
        numBytes += this.getSampled().size();
        numBytes += this.getPerm().size() * 4;
        return numBytes;
    }

}
