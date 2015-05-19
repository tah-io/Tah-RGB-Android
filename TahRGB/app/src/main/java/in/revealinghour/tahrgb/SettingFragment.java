package in.revealinghour.tahrgb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import util.PreferenceHelper;

/**
 * Created by shail on 29/03/15.
 */
public class SettingFragment extends Fragment {
    EditText edtDeviceName, edtPassword;
    RadioGroup tgOpenSecure;
    Button btnUpdate;
    Context context;
    boolean openSecure=false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        context = getActivity();
        btnUpdate = (Button) view.findViewById(R.id.btnupdatesetting);
        tgOpenSecure = (RadioGroup) view.findViewById(R.id.radioSex);
        edtPassword = (EditText) view.findViewById(R.id.edtdevicepass);
        edtDeviceName = (EditText) view.findViewById(R.id.edtdvicenam);
        edtDeviceName.setText(PreferenceHelper.getTahName(Selector.activity).toString());
        edtPassword.setEnabled(false);

        tgOpenSecure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.radioMale){
                    edtPassword.setEnabled(false);
                    openSecure=false;
                    ((Selector) getActivity()).writeData("AT+TYPE0", false);
                }else{
                    edtPassword.setEnabled(true);
                    openSecure=true;
                    ((Selector) getActivity()).writeData("AT+TYPE2", false);
                }
            }
        });
////update button click listener
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!openSecure) {
                    updateSetting(edtDeviceName.getText().toString(), "", false);
                } else {
                    String pass = edtPassword.getText().toString();
                    String devicename = edtDeviceName.getText().toString();
                    if ((pass != null && !equals("")) && (pass.length() < 6)) {
                        Toast.makeText(getActivity(), "Please add 6 digit password...", Toast.LENGTH_SHORT).show();

                    } else {

                            if(pass.length()>6){
                                Toast.makeText(getActivity(), "Please add 6 digit password...", Toast.LENGTH_SHORT).show();
                            }else {
                                updateSetting(devicename, pass, true);
                            }
                        }
                }
            }
        });
        return view;
    }
    //method to update settings
    public void updateSetting(String deviceName, String password, boolean passornaot) {

        if (passornaot) {
            if(deviceName!=null && !deviceName.equals("")) {
                ((Selector) getActivity()).writeData("AT+NAME" + deviceName, false);
            }
            //1000 milliseconds is one second.
            try {
                Thread.sleep(100);
                ((Selector) getActivity()).writeData("AT+PASS"+password, false);
                Thread.sleep(100);
                ((Selector) getActivity()).writeData("AT+RESET", false);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } else {
            if(deviceName!=null && !deviceName.equals("")) {
                ((Selector) getActivity()).writeData("AT+NAME" + deviceName, false);
            }
            //1000 milliseconds is one second.
            try {
                Thread.sleep(100);
                ((Selector) getActivity()).writeData("AT+RESET", false);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        Toast.makeText(getActivity(), "Changes Updated...", Toast.LENGTH_SHORT).show();
        // getActivity().finish();
    }
}
