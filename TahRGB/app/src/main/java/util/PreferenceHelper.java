package util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shail on 29/03/15.
 */
public class PreferenceHelper {
    Context con;

    public PreferenceHelper(Context con)
    {
        this.con = con;
    }


    public static  SharedPreferences getSharedSettings(Context con)
    {
        return con.getSharedPreferences(Constant.SHARED_PREFERENCE_KEY,0);
    }



//store tah device name
    public static void storeTahName( Context ctx, String name)
    {
        SharedPreferences.Editor prefEditor = getSharedSettings(ctx).edit();
        prefEditor.putString(Constant.DEVICE_NAME, name);
        prefEditor.commit();
    }
//get tah device name
    public static String getTahName(Context ctx)
    {
        SharedPreferences sp = getSharedSettings(ctx);
        return sp.getString(Constant.DEVICE_NAME,null);
    }


}
