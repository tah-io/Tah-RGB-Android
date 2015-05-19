package in.revealinghour.tahrgb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by shail on 5/5/15.
 */


public class TahRGB extends Fragment implements View.OnClickListener {
    Button btnaqua, btnsalmon, btnbanana, btngrass, btngrape, btntangerine, btnice, btnclover,btnstrawberry, btnhonwydew, btnsnown, btnplum, btnrainbow, btnoff;
    
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tah_rgb, container, false);
        context = getActivity();
        btnaqua = (Button) view.findViewById(R.id.imgaqua);
        btnaqua.setOnClickListener(this);
        btnsalmon = (Button) view.findViewById(R.id.imgsalmon);
        btnsalmon.setOnClickListener(this);
        btnbanana = (Button) view.findViewById(R.id.imgbanana);
        btnbanana.setOnClickListener(this);
        btngrass = (Button) view.findViewById(R.id.imggrass);
        btngrass.setOnClickListener(this);
        btngrape = (Button) view.findViewById(R.id.imggrape);
        btngrape.setOnClickListener(this);
        btntangerine = (Button) view.findViewById(R.id.imgtangerine);
        btntangerine.setOnClickListener(this);
        btnice = (Button) view.findViewById(R.id.imgice);
        btnice.setOnClickListener(this);
        btnclover = (Button) view.findViewById(R.id.imgclover);
        btnclover.setOnClickListener(this);
        btnstrawberry= (Button) view.findViewById(R.id.imgStrawberry);
        btnstrawberry.setOnClickListener(this);
        btnhonwydew = (Button) view.findViewById(R.id.imgHonwydew);
        btnhonwydew.setOnClickListener(this);
        btnsnown = (Button) view.findViewById(R.id.imgSnow);
        btnsnown.setOnClickListener(this);
        btnplum = (Button) view.findViewById(R.id.imgPlum);
        btnplum.setOnClickListener(this);
        btnrainbow = (Button) view.findViewById(R.id.imgranbow);
        btnrainbow.setOnClickListener(this);
        btnoff = (Button) view.findViewById(R.id.imgoff);
        btnoff.setOnClickListener(this);
        return view;
    }

    //image button click event here
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgaqua:
                Selector.mBluetoothLeService.TahRGB("0,131,246,1R");
                break;
            case R.id.imgsalmon:
                Selector.mBluetoothLeService.TahRGB("255,100,105,1R");
                break;
            case R.id.imgbanana:
                Selector.mBluetoothLeService.TahRGB("255,231,0,1R");
                break;
            case R.id.imggrass:
                Selector.mBluetoothLeService.TahRGB("83,254,125,1R");
                break;
            case R.id.imggrape:
                Selector.mBluetoothLeService.TahRGB("131,6,123,1R");
                break;
            case R.id.imgtangerine:
                Selector.mBluetoothLeService.TahRGB("255,126,46,1R");
                break;
            case R.id.imgice:
                Selector.mBluetoothLeService.TahRGB("84,255,254,1R");
                break;
            case R.id.imgclover:
                Selector.mBluetoothLeService.TahRGB("131,30,245,1R");
                break;
            case R.id.imgStrawberry:
                Selector.mBluetoothLeService.TahRGB("229,51,7,1R");
                break;
            case R.id.imgHonwydew:
                Selector.mBluetoothLeService.TahRGB("255,0,125,1R");
                break;
            case R.id.imgSnow:
                Selector.mBluetoothLeService.TahRGB("255,255,255,1R");
                break;
            case R.id.imgPlum:
                Selector.mBluetoothLeService.TahRGB("0,127,36,1R");
                break;
            case R.id.imgranbow:
                Selector.mBluetoothLeService.TahRGB("255,255,255,2R");
                break;
            case R.id.imgoff:
                Selector.mBluetoothLeService.TahRGB("0,0,0,1R");
                break;
        }

    }
}
