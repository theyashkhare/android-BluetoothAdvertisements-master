

package com.example.android.bluetoothadvertisements;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Setup display fragments and ensure the device supports Bluetooth.
 */
public class MainActivity extends FragmentActivity {

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_main_title);

        if (savedInstanceState == null) {

            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter();

            // Is Bluetooth supported on this device?
            if (mBluetoothAdapter != null) {

                // Is Bluetooth turned on?
                if (mBluetoothAdapter.isEnabled()) {

                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                        setupFragments();

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }
            } else {

                // Bluetooth is not supported.
                showErrorText(R.string.bt_not_supported);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {

                    // Bluetooth is now Enabled, are Bluetooth Advertisements supported on
                    // this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                        setupFragments();

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ScannerFragment scannerFragment = new ScannerFragment();
        // Fragments can't access system services directly, so pass it the BluetoothAdapter
        scannerFragment.setBluetoothAdapter(mBluetoothAdapter);
        transaction.replace(R.id.scanner_fragment_container, scannerFragment);

        AdvertiserFragment advertiserFragment = new AdvertiserFragment();
        transaction.replace(R.id.advertiser_fragment_container, advertiserFragment);

        transaction.commit();
    }

    private void showErrorText(int messageId) {

        TextView view = (TextView) findViewById(R.id.error_textview);
        view.setText(getString(messageId));
    }
}