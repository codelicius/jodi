package makasa.dapurkonten.jodohideal.receiver;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * Created by abay on 24/01/16.
 */
public class parsePush {
    public void deletePush(final String deviceid){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Installation");
        query.whereEqualTo("deviceToken",deviceid);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                for (ParseObject object : objects) {
                    object.deleteEventually();
                    object.saveInBackground();
                }
            }
        });

    }
    public void insertPush(final String email,final String userid,final String deviceid){
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Jodi");
        query1.whereEqualTo("email", email);
        query1.whereEqualTo("userid",userid);
        query1.whereEqualTo("deviceid",deviceid);
        query1.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e != null) {
                    if (e.getCode() == com.parse.ParseException.OBJECT_NOT_FOUND) {
                        final ParseObject insert = new ParseObject("Jodi");

                        insert.put("email", email);
                        insert.put("userid", userid);
                        insert.put("deviceid", deviceid);
                        insert.saveInBackground();
                    } else {
                        //unknown error, debug
                    }
                }
            }
        });
    }
    public void sendPush(){

    }
}
