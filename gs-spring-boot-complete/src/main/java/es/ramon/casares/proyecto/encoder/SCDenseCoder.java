package es.ramon.casares.proyecto.encoder;

import java.util.ArrayList;
import java.util.List;

public class SCDenseCoder {

    private Integer S;
    private Integer C;

    public SCDenseCoder(final Integer s, final Integer c) {
        super();
        this.S = s;
        this.C = c;
    }

    public Integer getS() {
        return this.S;
    }

    public void setS(final Integer s) {
        this.S = s;
    }

    public Integer getC() {
        return this.C;
    }

    public void setC(final Integer c) {
        this.C = c;
    }

    public List<Integer> encode(final Integer i) {
        final List<Integer> word = new ArrayList<Integer>();
        Integer pos = i % this.S;
        word.add(pos);
        int x = (int) Math.floor((double) i / (double) this.S);
        while (x > 0) {
            x = x - 1;
            pos = (x % this.C) + this.S;
            word.add(pos);
            x = (int) Math.floor((double) x / (double) this.C);
        }

        return word;
    }

    public Integer decode(final List<Integer> codeword) {
        int i = 0;
        int k = 0;

        while (codeword.get(k) >= this.S) {
            i = (i * this.C) + (codeword.get(k) - this.S);
            k++;
        }
        i = (i * this.S) + codeword.get(k);
        i = i + base(k + 1);
        return i;

    }

    public boolean wordComplete(final List<Integer> codeword) {

        final int pos = codeword.size() - 1;

        if (codeword.get(pos) >= this.S) {
            return false;
        } else {
            return true;
        }

    }

    // base[1]=0, base[2]=s, base[3]=s + sc, base[4]= s + sc+ sc2, base[5]= s + sc+ sc2 + sc3
    private Integer base(final int i) {
        int result = 0;
        switch (i) {
        case 1:
            result = 0;
            break;
        case 2:
            result = this.S;
            break;
        case 3:
            result = this.S + (this.S * this.C);
            break;
        case 4:
            result = this.S + (this.S * this.C) + (this.S * this.C * this.C);
            break;
        case 5:
            result = this.S + (this.S * this.C) + (this.S * this.C * this.C) + (this.S * this.C * this.C * this.C);
            break;
        }
        return result;
    }
}
