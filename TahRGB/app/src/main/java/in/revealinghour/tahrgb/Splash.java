package in.revealinghour.tahrgb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by shail on 06/04/15.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 2 seconds
                    sleep(2 * 1000);

                    // After 2 seconds redirect to another intent
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                }
            }
        };

        // start thread
        background.start();
    }
}
