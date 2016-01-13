package makasa.dapurkonten.jodohideal.object;

/**
 * Created by pr1de on 13/01/16.
 */
public class ChatHistory {

    private int senderID, recipientID;
    private String chatMessage;

    public ChatHistory(){

    }

    public ChatHistory(int senderID,int recipientID, String chatMessage){
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.chatMessage = chatMessage;

    }

    public int getpSenderID(){
        return senderID;
    }

    public void setSenderID(int senderID){
        this.senderID = senderID;
    }

    public int getRecipientID(){
        return recipientID;
    }

    public void setRecipientID(int recipientID){
        this.recipientID = recipientID;
    }


    public String getChatMessage(){
        return chatMessage;
    }

    public void setChatMessage(String chatMessage){
        this.chatMessage = chatMessage;
    }


}
