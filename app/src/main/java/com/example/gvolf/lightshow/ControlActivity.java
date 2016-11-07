package com.example.gvolf.lightshow;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Created by gvolf on 8/6/15.
 */
public class ControlActivity extends AppCompatActivity {
    private final byte prog = 0x01;
    private final byte beat = 0x00;
    private final byte step = 0x02;
    private final byte running = 0x03;
    private final byte mode = 0x04;
    private final byte lights = 0x05;

    public static  LinkedList<Integer> stack = new LinkedList<>();

    public Button beatPlus, beatMinus, progPlus, progMinus, stop_start;
    public TextView labelProg, labelStep, labelBeat, labelMode;
    public ImageView green1, green2, blue1, blue2, yellow1, yellow2, red1, red2;
    public ImageView[] lightsArray;
    public int green_on, green_off, blue_on, blue_off, yellow_on, yellow_off, red_on, red_off;
    public int[] drawablesArray;
    private String label, data;


    private String decodeBytes(int[] bytes){
        switch (bytes[0]){
            case prog:
                return "prog " + String.valueOf(bytes[1]);
            case step:
                return "step " + String.valueOf(bytes[1]);
            case beat:
                return "beat " + String.valueOf(bytes[1]) + "0";
            case running:
                return "running " + String.valueOf(bytes[1]);
            case mode:
                String s_mode = String.valueOf(bytes[1]);

                if (s_mode.equals("0")) s_mode = "Manual";
                else if (s_mode.equals("1")) s_mode = "Beat";
                else if (s_mode.equals("2")) s_mode = "Mic";

                return "mode " + s_mode;
            case lights:
                return "lights " + String.valueOf(bytes[1]);
            default:
                return "damn " + Integer.toBinaryString(bytes[0]);
        }
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] bytes = bundle.getByteArray("msg");
            int size = bundle.getInt("size");
            //int b = bundle.getInt("msg");
            for (int i=0; i<size; i++) {
                stack.addLast((int)bytes[i]);
            }
            while (stack.size() > 1) {
                int[] temp = {stack.pop(), stack.pop()};
                String status = decodeBytes(temp);

                String[] split = status.split(" ");
                switch (split[0]) {
                    case "beat":
                        labelBeat.setText("Beat: " + split[1]);
                        break;
                    case "prog":
                        labelProg.setText("Prog: " + split[1]);
                        break;
                    case "step":
                        labelStep.setText("Step: " + split[1]);
                        break;
                    case "running":
                        stop_start.setText((split[1].equals("0")) ? "Start" : "Stop");
                        break;
                    case "mode":
                        labelMode.setText("Mode: " + split[1]);
                        break;
                    case "lights":
                        setLights(Integer.parseInt(split[1]));
                        break;
                    default:
                        //Toast.makeText(getApplicationContext(), split[1], Toast.LENGTH_SHORT).show();
                }
            }

        }

        private void setLights(int lights) {
            for (int i=lightsArray.length-1; i>=0; i--) {
                if (((lights >> i) & 1) == 0)
                    lightsArray[i].setImageResource(drawablesArray[(i % 4) * 2]);
                else
                    lightsArray[i].setImageResource(drawablesArray[((i % 4) * 2) + 1]);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // labels and buttons
        beatPlus = (Button) findViewById(R.id.beat_plus);
        beatMinus = (Button) findViewById(R.id.beat_minus);
        progPlus = (Button) findViewById(R.id.prog_plus);
        progMinus = (Button) findViewById(R.id.prog_minus);
        stop_start = (Button) findViewById(R.id.stop_start);
        labelBeat = (TextView) findViewById(R.id.label_beat);
        labelProg = (TextView) findViewById(R.id.label_prog);
        labelStep = (TextView) findViewById(R.id.label_step);
        labelMode = (TextView) findViewById(R.id.label_mode);

        // iamge views
        green1 = (ImageView) findViewById(R.id.green1);
        green2 = (ImageView) findViewById(R.id.green2);
        red1 = (ImageView) findViewById(R.id.red1);
        red2 = (ImageView) findViewById(R.id.red2);
        yellow1 = (ImageView) findViewById(R.id.yellow1);
        yellow2 = (ImageView) findViewById(R.id.yellow2);
        blue1 = (ImageView) findViewById(R.id.blue1);
        blue2 = (ImageView) findViewById(R.id.blue2);
        lightsArray = new ImageView[]{green1, red1, blue1, yellow1, green2, red2, blue2, yellow2};

        // drawables for images
        green_on = R.drawable.green_on;
        green_off = R.drawable.green_off;
        red_on = R.drawable.red_on;
        red_off = R.drawable.red_off;
        yellow_on = R.drawable.yellow_on;
        yellow_off = R.drawable.yellow_off;
        blue_on = R.drawable.blue_on;
        blue_off = R.drawable.blue_off;
        drawablesArray = new int[]{green_on, green_off, red_on, red_off, blue_on, blue_off, yellow_on, yellow_off};

        BtConnect.btManager.setHandler(this.handler);

        BtConnect.btManager.send(BtManage.get_all);
    }

    protected void onDestroy(){
        BtConnect.cancelConnection();
        super.onDestroy();
    }

    public void buttonListener(View view) {
        if (view.equals(beatMinus)) {
            BtConnect.btManager.send(BtManage.beat_minus);
        } else if (view.equals(beatPlus)) {
            BtConnect.btManager.send(BtManage.beat_plus);
        } else if (view.equals(progPlus)) {
            BtConnect.btManager.send(BtManage.prog_plus);
        } else if (view.equals(progMinus)) {
            BtConnect.btManager.send(BtManage.prog_minus);
        } else if (view.equals(stop_start)) {
            BtConnect.btManager.send(BtManage.stop_start);
        } else {
            Toast.makeText(getApplicationContext(),"somehow I'm stupid", Toast.LENGTH_SHORT).show();
        }
    }
}
