package makasa.dapurkonten.jodohideal.object;

/**
 * Created by pr1de on 14/01/16.
 */
public class RecentChat {
    private int partnerID;
    private String firstName, lastName, pic;

    public RecentChat(){

    }

    public RecentChat(int partnerID, String firstName, String lastName, String pic){
        this.partnerID = partnerID;
        this.firstName= firstName;
        this.lastName = lastName;
        this.pic = pic;
    }

    public int getPartnerID(){
        return partnerID;
    }

    public void setPartnerID(int partnerID){
        this.partnerID = partnerID;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName= firstName;
    }


    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName= lastName;
    }

    public String getPic(){
        return pic;
    }

    public void setPic(String pic){
        this.pic= pic;
    }
}
