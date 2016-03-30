package makasa.dapurkonten.jodohideal.app;

import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.parse.ParseInstallation;

/**
 * Created by pr1de on 01/12/15.
 */
public class AppConfig {
    public static final String PARSE_CHANNEL = "Jodi";
    public static final String PARSE_APPLICATION_ID = "B4ZJi7CuMdX33hpwPWv5sLCcTRMNyb7gEtLHzvGE";
    public static final String PARSE_CLIENT_KEY = "UftgXzH0iSSKYJKiVPKSrN1bW0AmV14l2BbIk4yX";
    public static final int NOTIFICATION_ID = 100;
//    public static final String devid= ParseInstallation.getCurrentInstallation().get("deviceToken").toString();

        public static String urlAPI = "http://jodi.licious.id/api/";
    //    public static String urlAPI = "http://103.253.112.121/jodohideal/api/";
    public void fbLogout(Context c){
        FacebookSdk.sdkInitialize(c);
        LoginManager.getInstance().logOut();
    }

}
