package es.ramon.casares.proyecto.modelo.estructura.log;

import java.util.List;

public class LogPositionBean {

    int part2;

    boolean usado;

    int pos;

    List<Integer> word;

    public LogPositionBean(final int part2, final boolean usado, final int pos, final List<Integer> word) {
        super();
        this.part2 = part2;
        this.usado = usado;
        this.pos = pos;
        this.word = word;
    }

    public int getPart2() {
        return this.part2;
    }

    public void setPart2(final int part2) {
        this.part2 = part2;
    }

    public boolean isUsado() {
        return this.usado;
    }

    public void setUsado(final boolean usado) {
        this.usado = usado;
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(final int pos) {
        this.pos = pos;
    }

    public List<Integer> getWord() {
        return this.word;
    }

    public void setWord(final List<Integer> word) {
        this.word = word;
    }

}
