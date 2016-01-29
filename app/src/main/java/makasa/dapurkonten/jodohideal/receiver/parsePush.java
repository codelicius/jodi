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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * Created by abay on 24/01/16.
 */
public class parsePush {
    public void deletePush(){
        ParseInstallation delete= ParseInstallation.getCurrentInstallation();
        delete.remove("islogin");
        delete.saveInBackground();
        /**ParseQuery<ParseObject> query = ParseQuery.getQuery("Installation");
        query.whereEqualTo("deviceToken",deviceid);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                for (ParseObject object : objects) {
                    object.deleteEventually();
                    object.saveInBackground();
                }
            }
        });**/

    }
    public void insertPush(final String email,final String userid){
        ParseInstallation insert = ParseInstallation.getCurrentInstallation();
        insert.put("email", email);
        insert.put("userid", userid);
        insert.put("islogin", "yes");
        insert.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("successparse", "success");
                } else {
                    Log.d("errorparse", "error" + e.getMessage());
                }
            }
        });
        /**ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Jodi");
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
        });**/
    }
    public void sendPush(String userid,String pID){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("islogin","yes");
        pushQuery.whereEqualTo("userid",pID);

        JSONObject obj = new JSONObject();
        try {
            obj.put("title", "Jodoh Ideal");
            obj.put("alert", "You have new message");
            obj.put("type", "private");
            obj.put("partnerid",userid);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setData(obj);
        push.sendInBackground();

    }
}
