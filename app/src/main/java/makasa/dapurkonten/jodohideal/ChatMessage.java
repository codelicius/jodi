package makasa.dapurkonten.jodohideal;

import android.util.Log;

public class ChatMessage {
    private static String INI = ChatMessage.class.getSimpleName();
    public String position;
    public String message;

    public ChatMessage(String position, String message) {
        super();
        Log.d(INI,"chat message "+position);
        this.position = position;
        this.message = message;
    }
}

