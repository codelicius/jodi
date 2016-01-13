package makasa.dapurkonten.jodohideal;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right_msg, parent, false);
        //}else{
        //    row = inflater.inflate(R.layout.left_msg, parent, false);
        //}


        /**
         * PS: disini nantinya dibuat kondisi dimana jika getSenderID() dari class ChatHistory
         * sama dengan userid penguna, maka layout inflaternya adalah R.id.right_msg
         * selain itu berarti message berasal dari lawan chat dan dibuat di sebelah kiri
         * dengan layout inflater R.id.left_msg
         *
         * saat ini isi dari listview masih mengambil dari class ChatMessage, nanti tolong diubah ke ChatHistory
         * atau jika ada logic lainnya silahkan diterapkan.
         */


        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.message);
        return row;
    }
}
