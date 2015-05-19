package in.revealinghour.tahrgb;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;

import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import bleservice.TAHble;
import util.Constant;
import util.PreferenceHelper;

/**
 * Created by shail on 12/03/15.
 */

public class Selector extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private String mDeviceName;
    private String mDeviceAddress;
    View mConnectionStatus;
    private boolean mConnected = false;
    private FragmentManager fragmentManager;
    public static TAHble mBluetoothLeService;
    //action bar and drawer new changes
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    android.support.v7.app.ActionBar actionBar;
    TextView txtProfileName, txtDeviceAddress;
    ArrayList<String> arrayList;
    public static Context context;
    public static Activity activity;
    final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            activity = Selector.this;
            mBluetoothLeService = ((TAHble.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
//                    Log.e(TAG, "Unable to initialize Bluetooth");
//                    finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainact);
        arrayList = new ArrayList<String>();
        context = Selector.this;
        mConnectionStatus = (View) findViewById(R.id.connectionstatus);
        txtProfileName = (TextView) findViewById(R.id.userName);
        txtDeviceAddress = (TextView) findViewById(R.id.desc);
// new changes for action bar and drawer
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setHomeButtonEnabled(false);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(mTitle);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {
            replaceFragment(new TahRGB(), 0,"mac");
        }

        Intent intent = getIntent();
        if (intent != null) {
            mDeviceName = intent.getStringExtra(Constant.EXTRAS_DEVICE_NAME);
            mDeviceAddress = intent.getStringExtra(Constant.EXTRAS_DEVICE_ADDRESS);
        }
        if (mDeviceName != null && mDeviceAddress != null) {
            txtProfileName.setText(mDeviceName);
            txtDeviceAddress.setText(mDeviceAddress);
            PreferenceHelper.storeTahName(Selector.this, mDeviceName);
        }
        //connect to service
        Intent gattServiceIntent = new Intent(Selector.this, TAHble.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //Log.d(TAG, "Connect request result=" + result);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    //write data method of main activity
    public boolean writeData(String value, boolean notification) {
        if (true) {
            try {
                return mBluetoothLeService.writeCharacteristic(Constant.ServiceUid, Constant.CharaUid, value);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Toast.makeText(getApplicationContext(), "Please check device connection..", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //write data with response method of main activity
    public boolean writeDataRes(String value, boolean notification) {
        if (true) {
            try {
                return mBluetoothLeService.writeCharacteristicWithRes(Constant.ServiceUid, Constant.CharaUid, value, notification);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Toast.makeText(getApplicationContext(), "Please check device connection..", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TAHble.ACTION_GATT_CONNECTED);
        intentFilter.addAction(TAHble.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(TAHble.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(TAHble.ACTION_DATA_AVAILABLE);


        return intentFilter;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (TAHble.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(mConnected);
            } else if (TAHble.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                if (TAHble.sleepWakeup) {
                    updateConnectionState(mConnected);
                }
                //invalidateOptionsMenu();
            } else if (TAHble.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (TAHble.ACTION_DATA_AVAILABLE.equals(action)) {
               // final String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString();

            }
        }
    };

    //set status of connection using UI Thread
    private void updateConnectionState(final boolean connectStat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!connectStat) {
                    mConnectionStatus.setBackgroundColor(Color.parseColor("#FF530D"));
                    Toast.makeText(getApplicationContext(), "Disconnect  Tah", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "connected to Tah", Toast.LENGTH_SHORT).show();
                    mConnectionStatus.setBackgroundColor(Color.parseColor("#76ff03"));
                }
            }
        });
    }


    //set status of connection using UI Thread
    private void replaceFragment(final Fragment targetFragment, final int position, final String msg) {
        //close drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawers();
        //clear fragment
        clearFragmentBackStack();
        //start thread here for 1 sec
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 1 seconds
                    sleep(1 * 500);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  mTextHeader.setText(headerText);
                            if (targetFragment != null) {
                                fragmentManager = getSupportFragmentManager();
                                fragmentManager.addOnBackStackChangedListener(Selector.this);
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                String packageNameAsTag = ((Object) targetFragment).getClass().getCanonicalName();
                                System.out.println("package name is :: " + packageNameAsTag);

                                if (fragmentManager.findFragmentByTag(packageNameAsTag) == null) {
                                    fragmentTransaction.add(R.id.content_frame, targetFragment, packageNameAsTag);
                                    fragmentTransaction.addToBackStack(packageNameAsTag);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constant.EXTRAS_DEVICE_NAME, mDeviceName);
                                    bundle.putString(Constant.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                                    System.out.println("==================="+msg);
                                    bundle.putString("MAC", msg);
                                    targetFragment.setArguments(bundle);
                                    System.out.println(((Object) targetFragment).getClass().getSimpleName() + " added to backstack");
                                    fragmentTransaction.commit();
                                } else {
                                    System.out.println("this fragment is already in the backstack");
                                }
                            } else {
//            ToastUtil.displayToast(this, this.getString(R.string.toast_working));

                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();
        // start thread

    }

    //change fragment
    public void changeFragment(Fragment targetFragment) {

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        System.out.println("Count==================" + fragmentManager.getBackStackEntryCount());
        if (targetFragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.addOnBackStackChangedListener(this);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            String packageNameAsTag = ((Object) targetFragment).getClass().getCanonicalName();
            // System.out.println("package name is :: " + packageNameAsTag);

            if (fragmentManager.findFragmentByTag(packageNameAsTag) == null) {
                fragmentTransaction.add(R.id.content_frame, targetFragment, packageNameAsTag);
                fragmentTransaction.addToBackStack(packageNameAsTag);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.EXTRAS_DEVICE_NAME, mDeviceName);
                bundle.putString(Constant.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                targetFragment.setArguments(bundle);
                System.out.println(((Object) targetFragment).getClass().getSimpleName() + " added to backstack");
                fragmentTransaction.commit();
            } else {
                // System.out.println("this fragment is already in the backstack");
            }
        } else {
//            ToastUtil.displayToast(this, this.getString(R.string.toast_working));

        }

    }

    private void selectItem(int position) {

        // update the main content by replacing fragments
//        android.app.Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawers();

    }

    //actionbar home click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                Selector.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        // return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
         Selector.this.finish();
        super.onBackPressed();
    }

    //need to impliment logic here
    @Override
    public void onBackStackChanged() {

    }


    //method to clear fragment

    /**
     * Function to clear fragment backstack but one
     */
    public void clearFragmentBackStack() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                int i = fragmentManager.getBackStackEntryCount();
                for (int j = 0; j < i; j++) {
                    fragmentManager.popBackStackImmediate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // if this method not override action home icon not changed (i.e menu or ic_drawer icon instead of home(back) icon)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);


            switch (position) {
                case 0:
                    replaceFragment(new TahRGB(), position,"");
                    break;
                case 1:
                    replaceFragment(new SettingFragment(), position,"");
                    break;
                case 2:
                    replaceFragment(new Video(), position,"");
                    break;
                case 3:
                    replaceFragment(new ShareFragment(), position,"");
                    break;

            }

        }
    }

}
