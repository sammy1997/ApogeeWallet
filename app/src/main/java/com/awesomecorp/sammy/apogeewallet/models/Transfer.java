package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class Transfer {
    Participant participant;
    boolean is_bitsian;
    Bitsian bitsian;

    public Transfer(Participant participant, boolean is_bitsian, Bitsian bitsian) {
        this.participant = participant;
        this.is_bitsian = is_bitsian;
        this.bitsian = bitsian;
    }

    public Transfer() {
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public boolean isIs_bitsian() {
        return is_bitsian;
    }

    public void setIs_bitsian(boolean is_bitsian) {
        this.is_bitsian = is_bitsian;
    }

    public Bitsian getBitsian() {
        return bitsian;
    }

    public void setBitsian(Bitsian bitsian) {
        this.bitsian = bitsian;
    }
}
