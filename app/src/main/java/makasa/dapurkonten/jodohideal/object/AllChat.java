package makasa.dapurkonten.jodohideal.object;

/**
 * Created by pr1de on 15/01/16.
 */
public class AllChat {
    private int chatID;
    private String partnerName, partnerPic, messagePreview;

    public AllChat(){

    }

    public AllChat(int chatID, String partnerName, String partnerPic, String messagePreview){
        this.chatID = chatID;
        this.partnerName = partnerName;
        this.partnerPic = partnerPic;
        this.messagePreview = messagePreview;

    }

    public int getChatID(){
        return chatID;
    }

    public void setChatID(int chatID){
        this.chatID= chatID;
    }

    public String getPartnerName(){
        return partnerName;
    }

    public void setPartnerName(String partnerName){
        this.partnerName= partnerName;
    }

    public String getPartnerPic(){
        return partnerPic;
    }

    public void setPartnerPic(String partnerPic){
        this.partnerPic= partnerPic;
    }

    public String getMessagePreview(){
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview){
        this.messagePreview = messagePreview;
    }
}
