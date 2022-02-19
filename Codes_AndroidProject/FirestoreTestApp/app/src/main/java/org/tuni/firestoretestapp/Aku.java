package org.tuni.firestoretestapp;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Aku implements Serializable {

    private String kirjanNumero;
    private String kirjanNimi;
    private String kirjanVuosi;
    private String kirjanSivu;

    private String docId;

    public Aku(String kirjanNumero, String kirjanNimi, String kirjanVuosi, String kirjanSivu) {
        this.kirjanNumero = kirjanNumero;
        this.kirjanNimi = kirjanNimi;
        this.kirjanVuosi = kirjanVuosi;
        this.kirjanSivu = kirjanSivu;
    }

    public Aku() {

    }

    public String getKirjanVuosi() {
        return kirjanVuosi;
    }

    public void setKirjanVuosi(String kirjanVuosi) {
        this.kirjanVuosi = kirjanVuosi;
    }

    public String getKirjanSivu() {
        return kirjanSivu;
    }

    public void setKirjanSivu(String kirjanSivu) {
        this.kirjanSivu = kirjanSivu;
    }

    public String getKirjanNumero() {
        return kirjanNumero;
    }

    public void setKirjanNumero(String kirjanNumero) {
        this.kirjanNumero = kirjanNumero;
    }

    public String getKirjanNimi() {
        return kirjanNimi;
    }

    public void setKirjanNimi(String kirjanNimi) {
        this.kirjanNimi = kirjanNimi;
    }
    public void setDocId(String id) {
        this.docId = id;
    }
    public String getDocId() {
        return this.docId;
    }

    @NonNull
    @Override
    public String toString() {
        return kirjanNumero + ". " + kirjanNimi + " (v" + kirjanVuosi + ") s" + kirjanSivu;
    }
}
