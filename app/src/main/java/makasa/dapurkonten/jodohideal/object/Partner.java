package makasa.dapurkonten.jodohideal.object;

/**
 * Class untuk menyimpan object dari JSON
 * digunakan pada listview
 */

public class Partner {
    private String firstName, lastName, fullName, gender, suku, agama, urlFoto;
    private int pID, kecocokan, ketidakcocokan, umur;


    public Partner(){

    }

    public Partner(int pID,String firstName, String lastName, String gender,
                   String suku, String agama, String urlFoto, int kecocokan, int ketidakcocokan, int umur){
        this.pID = pID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.suku = suku;
        this.agama = agama;
        this.urlFoto = urlFoto;
        this.kecocokan = kecocokan;
        this.ketidakcocokan = ketidakcocokan;
        this.umur = umur;
    }

    public int getpID(){
        return pID;
    }

    public void setpID(int pID){
        this.pID = pID;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String firstName, String lastName){
        this.fullName = firstName + " " + lastName;
    }


    public String getGender(){
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String getSuku(){
        return suku;
    }

    public void setSuku(String suku){
        this.suku = suku;
    }

    public String getAgama(){
        return agama;
    }

    public void setAgama(String agama){
        this.agama = agama;
    }

    public String getUrlFoto(){
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto){
        this.urlFoto = urlFoto;
    }

    public int getKecocokan(){
        return kecocokan;
    }

    public void setKecocokan(int kecocokan){
        this.kecocokan = kecocokan;
    }

    public int getKetidakcocokan(){
        return ketidakcocokan;
    }

    public void setKetidakcocokan(int ketidakcocokan){
        this.ketidakcocokan = ketidakcocokan;
    }

    public int getUmur(){
        return umur;
    }

    public void setUmur(int umur){
        this.umur = umur;
    }

}
