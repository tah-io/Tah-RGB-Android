package in.revealinghour.tahrgb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import util.Utils;

/**
 * Created by Shail on 03/05/15.
 */
//http://stackoverflow.com/questions/4448084/android-webview-goback-and-goforward-does-not-work-with-webviewclient
public class Video extends Fragment {
    WebView wbYoutube;
    ImageButton next, previous, reload;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tah_video, container, false);
        wbYoutube = (WebView) view.findViewById(R.id.youtube);
        next = (ImageButton) view.findViewById(R.id.wbforword);
        previous = (ImageButton) view.findViewById(R.id.wbackword);
        reload = (ImageButton) view.findViewById(R.id.wbreload);
        if (Utils.haveNetworkConnection(Selector.activity)) {
            try {
                wbYoutube.getSettings().setJavaScriptEnabled(true);
                wbYoutube.setWebViewClient(new WebViewClient());
                wbYoutube.loadUrl("https://www.youtube.com/channel/UCY7RNCi0S8jQDE3BZQw5Gwg/videos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Selector.activity, "Please check internet connection...", Toast.LENGTH_SHORT).show();
        }

        //
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbYoutube.goForward();

            }
        });
        //
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbYoutube.goBack();
            }
        });
        //
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbYoutube.reload();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (wbYoutube != null) {
                wbYoutube.clearHistory();
                wbYoutube.clearCache(true);
                wbYoutube.loadUrl("about:blank");
                wbYoutube.freeMemory();
                wbYoutube.pauseTimers();
                wbYoutube = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
