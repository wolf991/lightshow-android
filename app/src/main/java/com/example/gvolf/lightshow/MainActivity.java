package com.example.gvolf.lightshow;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public ProgressDialog progress;

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String status = bundle.getString("msg");
            if (status.equals("STARTPROG")) {
                progress.setTitle("Loading ...");
                progress.setMessage("Connecting to LightShow");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
            } else if (status.equals("OK")) {
                progress.dismiss();
                Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
                startActivity(intent);
            } else {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connect(View view) {
        Toast.makeText(getApplicationContext(),"Just a test",Toast.LENGTH_SHORT).show();
    }

    public void BTconnect(View view) {

        if (!BtConnect.BtAdapter.isEnabled()) {
            Intent enableBt= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, BtConnect.REQUEST_ENABLE_BT);
        } else
            BtConnect.connectToLightShow(handler);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == BtConnect.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BTconnect(null);
            } else {
                Toast.makeText(getApplicationContext(),"You need to enable BT to continue",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
