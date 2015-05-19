package in.revealinghour.tahrgb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import social.AsyncFacebookRunner;
import social.DialogError;
import social.Facebook;
import social.FacebookError;
import social.SessionEvents;
import social.SessionStore;
import social.Util;
import util.Utils;

/**
 * Created by shail on 01/04/15.
 */
public class ShareFragment extends Fragment {
    Button btnFbShare, btnTwShare;
    private Facebook mFacebook;
    public static String facebook_mAPP_ID = "823848531035527";
    private static final String APP_ID = facebook_mAPP_ID;
    private Facebook mFb;
    private String[] mPermissions;
    private Activity mActivity;
    //A//nN4Gi0dwBfE0rY4gl9Sam+Fo=


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_fragment, container, false);
        btnFbShare = (Button) view.findViewById(R.id.fbshare);
        btnTwShare = (Button) view.findViewById(R.id.btnsharetwter);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //share on facebook
        btnFbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(getActivity())) {

                    try {
                        mFacebook = new Facebook(APP_ID);
                        if (!mFacebook.isSessionValid()) {

                            fbLogin();
                        } else {
                            postToFacebook();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{

                    Toast.makeText(Selector.activity,"Please check internet connection...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //share on twitter
        btnTwShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.haveNetworkConnection(getActivity())) {
                    shareTwitter();
                }else{
                    Toast.makeText(Selector.activity,"Please check internet connection...",Toast.LENGTH_SHORT).show();
                }
                //System.out.print("key key=="+printKeyHash(Selector.activity));
            }
        });
        return view;
    }


    public final class LoginDialogListener implements Facebook.DialogListener {
        public void onComplete(Bundle values) {
            try {
                //The user has logged in, so now you can query and use their Facebook info
                JSONObject json = Util.parseJson(mFacebook.request("me"));
                SessionStore.save(mFacebook, Selector.activity);
                postToFacebook();
            } catch (Exception error) {
                Toast.makeText(Selector.activity, error.toString(), Toast.LENGTH_SHORT).show();
            } catch (FacebookError error) {
                Toast.makeText(Selector.activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFacebookError(FacebookError e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(DialogError e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }
    }

    public void fbLogin() {
        init(Selector.activity, mFacebook, new String[]{"publish_stream", "email"});
    }

    public void init(final Activity activity, final Facebook fb) {
        init(activity, fb, new String[]{"publish_stream", "email"});
    }

    public void init(final Activity activity, final Facebook fb, final String[] permissions) {
        try {
            mActivity = activity;
            mFb = fb;
            mPermissions = permissions;
            SessionEvents.addAuthListener(new SampleAuthListener());
            SessionEvents.addLogoutListener(new SampleLogoutListener());
            if (mFb.isSessionValid()) {
                SessionEvents.onLogoutBegin();
                AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
                //	asyncRunner.logout(getBaseContext(), new LogoutRequestListener());
            } else {
                mFb.authorize(mActivity, mPermissions, new LoginDialogListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            mFacebook.authorizeCallback(requestCode, resultCode, data);
        } catch (Exception e) {
            //   showdialog();
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    public class SampleAuthListener implements SessionEvents.AuthListener {

        @Override
        public void onAuthSucceed() {
            try {
                mFacebook = new Facebook(APP_ID);
                postToFacebook();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }

        @Override
        public void onAuthFail(String error) {
            Toast.makeText(mActivity, "Login Failed:", Toast.LENGTH_LONG).show();
        }
    }

    public class SampleLogoutListener implements SessionEvents.LogoutListener {
        @Override
        public void onLogoutBegin() {
            Toast.makeText(mActivity, "Logging out...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLogoutFinish() {
        }
    }

    private class SessionListener implements SessionEvents.AuthListener, SessionEvents.LogoutListener {

        @Override
        public void onAuthSucceed() {
            SessionStore.save(mFb, Selector.activity);
        }

        @Override
        public void onAuthFail(String error) {
        }

        @Override
        public void onLogoutBegin() {
        }

        @Override
        public void onLogoutFinish() {
            SessionStore.clear(Selector.activity);
        }
    }

    private void postToFacebook() {


        try {
            Bundle params = new Bundle();
            params.putString("message", "Tah");
            params.putString("description", "Check out Tah, the Arduino-compatible BLE development board: https://play.google.com/store/apps/details?id=in.revealinghour.tahrgb");
            params.putString("link", "https://play.google.com/store/apps/details?id=in.revealinghour.tah");

            mFacebook.dialog(Selector.activity, "feed", params, new Facebook.DialogListener() {

                @Override
                public void onFacebookError(FacebookError arg0) {

                }

                @Override
                public void onError(DialogError arg0) {
                    //Display your message for error
                    Toast.makeText(Selector.activity, "Oops, something went wrong, can you please try again?", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(Bundle arg0) {
                    Toast.makeText(Selector.activity, "Shared on facebook...", Toast.LENGTH_SHORT).show();
                    //Display your message on share scuccessful
                    try {
                        //mProgress.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }

                }

                @Override
                public void onCancel() {
                    //Display your message on dialog cancel

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    //key hash

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            //  Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    //share on twitter
    public void shareTwitter() {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, "Check out Tah, the Arduino-compatible BLE development board: https://play.google.com/store/apps/details?id=in.revealinghour.tahrgb");
        tweetIntent.setType("text/plain");

        PackageManager packManager = Selector.activity.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, "Check out Tah, the Arduino-compatible BLE development board: https://play.google.com/store/apps/details?id=in.revealinghour.tahrgb");
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=message&via=profileName"));
            startActivity(i);
            Toast.makeText(getActivity(), "Twitter app isn't found", Toast.LENGTH_LONG).show();
        }
    }
}
