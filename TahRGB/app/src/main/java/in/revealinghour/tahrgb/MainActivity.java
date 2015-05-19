package in.revealinghour.tahrgb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import social.Facebook;
import social.SessionStore;
import util.Constant;

public class MainActivity extends ActionBarActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    //bluetooth things
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private boolean mScanning;
    // Stops scanning after 10 seconds.10000;
    private static final long SCAN_PERIOD = 5000;
    private ListView mDeviceList;
    ProgressBar progressBar;
    boolean DeviceFound = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceList = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        //progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF530D"), PorterDuff.Mode.SRC_IN);
        mHandler = new Handler();
        //set action bar home button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        //init bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        final Dialog mydialog = getShareOptionDialog(MainActivity.this);
        FrameLayout relswipe = (FrameLayout) mydialog.findViewById(R.id.relativesipe);
        relswipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.cancel();
                progressBar.setVisibility(View.VISIBLE);
                mLeDeviceListAdapter = new LeDeviceListAdapter();
                mDeviceList.setAdapter(mLeDeviceListAdapter);
                scanLeDevice(true);
            }
        });
        mydialog.show();
        //first check bluetooth is enable or not
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            showAlert(MainActivity.this);
        } else {
//            mLeDeviceListAdapter = new LeDeviceListAdapter();
//            mDeviceList.setAdapter(mLeDeviceListAdapter);
//            scanLeDevice(true);
        }

        //swipe to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //start scan
                mLeDeviceListAdapter = new LeDeviceListAdapter();
                mDeviceList.setAdapter(mLeDeviceListAdapter);
                scanLeDevice(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//remove refreshing icon
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 6000);
            }
        });

        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                Toast.makeText(MainActivity.this, "" + device.getName(), Toast.LENGTH_LONG).show();

                if (device == null) return;
                //device.createBond();
                final Intent intent = new Intent(MainActivity.this, Selector.class);
                intent.putExtra(Constant.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(Constant.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                DeviceFound = true;
                startActivity(intent);
//
//                Intent i=new Intent(MainActivity.this,Selector.class);
//                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!DeviceFound) {
            DeviceFound = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//logout from fb
        Facebook facebook = new Facebook("823848531035527");
        try {
            facebook.logout(getApplicationContext());
            SessionStore.clear(getApplicationContext());
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
//scnan device method
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    if (!DeviceFound) {
                        Toast.makeText(getApplication(), "Device not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    //alert dialog if bluetooth disable
    public void showAlert(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Please enable bluetooth..");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //on activity result used after bluetooth start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "Please turn on bluetooth...", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        } else {
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            mDeviceList.setAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //holder class for adapter
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    // Adapter class  for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            DeviceFound = true;
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.devicelist, null);
                viewHolder = new ViewHolder();
//                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.dvname);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.dvname);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {

                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("Unknown Device");

            }
            //viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }


        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public static Dialog getShareOptionDialog(Context ctx) {
        Dialog helpDialog = new Dialog(ctx);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.setContentView(R.layout.scanhelp);
        helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        helpDialog.setCancelable(true);
        return helpDialog;
    }
}
