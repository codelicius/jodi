package makasa.dapurkonten.jodohideal.object;

import java.util.HashMap;

import makasa.dapurkonten.jodohideal.app.SQLiteController;

/**
 * Created by pr1de on 14/01/16.
 */

public class PencocokanJawaban {

    private String pertanyaan, jawabanKamu, jawabanDia, namaDia, fotoKamu, fotoDia;

    public PencocokanJawaban(){

    }

    public PencocokanJawaban(String pertanyaan, String jawabanKamu, String jawabanDia, String namaDia, String fotoKamu, String fotoDia){
        this.pertanyaan = pertanyaan;
        this.jawabanKamu = jawabanKamu;
        this.jawabanDia = jawabanDia;
        this.namaDia = namaDia;
        this.fotoKamu = fotoKamu;
        this.fotoDia = fotoDia;
    }

    public String getPertanyaan(){
        return pertanyaan;
    }

    public void setPertanyaan(String pertanyaan){
        this.pertanyaan = pertanyaan;
    }

    public String getJawabanKamu(){
        return jawabanKamu;
    }

    public void setJawabanKamu(String jawabanKamu){
        this.jawabanKamu = jawabanKamu;
    }

    public String getNamaDia(){
        return namaDia;
    }

    public void setNamaDia(String namaDia){
        this.namaDia = namaDia;
    }

    public String getJawabanDia(){
        return jawabanDia;
    }

    public void setJawabanDia(String jawabanDia){
        this.jawabanDia = jawabanDia;
    }

    public String getFotoKamu(){
        return fotoKamu;
    }

    public void setFotoKamu(String fotoKamu){
        this.fotoKamu = fotoKamu;
    }

    public String getFotoDia(){
        return fotoDia;
    }

    public void setFotoDia(String fotoDia){
        this.fotoDia = fotoDia;
    }

}
