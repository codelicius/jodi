package makasa.dapurkonten.jodohideal.object;

/**
 * Created by pr1de on 14/01/16.
 */
public class PencocokanJawaban {

    private String pertanyaan, jawabanKamu, jawabanDia, namaDia;

    public PencocokanJawaban(){

    }

    public PencocokanJawaban(String pertanyaan, String jawabanKamu, String jawabanDia, String namaDia){
        this.pertanyaan = pertanyaan;
        this.jawabanKamu = jawabanKamu;
        this.jawabanDia = jawabanDia;
        this.namaDia = namaDia;
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
}
