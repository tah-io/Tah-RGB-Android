package in.revealinghour.tahrgb;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by shail on 04/04/15.
 */
public class RgbBluetoothReceiver extends BroadcastReceiver {
    public static boolean pinavilable = false;
    Dialog mydialog;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                mydialog = new Dialog(Selector.activity);
                pinavilable = true;
                showDialog(intent);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showDialog(final Intent intent) {
        try {
            mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mydialog.setContentView(R.layout.pindialog);
            mydialog.setCancelable(true);
            Button btnok = (Button) mydialog.findViewById(R.id.btnok);
            Button btncancel = (Button) mydialog.findViewById(R.id.btncancel);
            final EditText edtpin = (EditText) mydialog.findViewById(R.id.edtpin);
            try {
                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pairDevice(edtpin.getText().toString(), intent);
                        mydialog.cancel();
                    }
                });

                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mydialog.cancel();
                        Selector.activity.finish();

                    }
                });
                mydialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pairDevice(String pinstrg, Intent intent) {

        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        device.createBond();
        device.setPairingConfirmation(true);


        try {
            byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, pinstrg);
            BluetoothDevice.class.getMethod("setPin", byte[].class).invoke(device, pin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(Selector.activity, "Please wait,it will take some time...", Toast.LENGTH_SHORT).show();


    }
}
