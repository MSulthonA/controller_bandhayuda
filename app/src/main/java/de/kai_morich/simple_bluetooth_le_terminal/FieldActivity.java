package de.kai_morich.simple_bluetooth_le_terminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class FieldActivity extends AppCompatActivity {

    private TextView receiveText;
    public byte[] Rmotions = new byte[5];
    public byte[] Lmotions = new byte[5];
    public byte[] keys = new byte[5];
    FragmentManager fm = getSupportFragmentManager();
    FieldFragment field_fragment = new FieldFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resetKey();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_field);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();

        bundle.getString("devices");



//        getSupportFragmentManager().addOnBackStackChangedListener(this);
        field_fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, field_fragment, "devices").commit();
        resetKey();
        resetMotion();
//        if (savedInstanceState == null) {
//            field_fragment.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment, field_fragment, "devices").commit();
//        }else{
//            onBackStackChanged();
//        }

    }



//    @Override
//    public void onBackStackChanged() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
    public void resetKey(){
        keys[0]=0b0;
        keys[1]=0b0;
        keys[2]=0b0;
        keys[3]=0b0;
        keys[4]=0b0;

    }
    public void resetMotion(){
        Rmotions[0]=0b0010;
        Rmotions[1]=0b0;
        Rmotions[2]=0b0;
        Rmotions[3]=0b0;
        Rmotions[4]=0b0;
        Lmotions[0]=0b0110;
        Lmotions[1]=0b0;
        Lmotions[2]=0b0;
        Lmotions[3]=0b0;
        Lmotions[4]=0b0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        field_fragment =
                (FieldFragment) fm.findFragmentById(R.id.fragment);
        receiveText = findViewById(R.id.receiveText);
        String msg="";
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                if(keyCode==96){
                    keys[1] = (byte)(keys[0] | 0b00000001);
//                    Log.d("Button : ","Button A "+String.valueOf(key));
//                    msg = "Button A"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==97){
//                    msg = "Button B"+String.valueOf(key);
                    keys[1] |= 0b00000010;
//                    Log.d("Button : ","Button B "+String.valueOf(key));
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==99){
                    keys[1] |= 0b00000100;
//                    Log.d("Button : ","Button X "+String.valueOf(key));
//                    msg = "Button X"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if((keyCode==100) || (keyCode==4)){
                    keys[1] |= 0b00001000;
//                    Log.d("Button : ","Button Y " +String.valueOf(key));
//                    msg = "Button Y"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
//                if(keyCode==4){
//                    keys[1] |= 0b00001000;
////                    Log.d("Button : ","Button Back "+String.valueOf(key));
////                    msg = "Button Back"+String.valueOf(key);
//                    field_fragment.sendData(keys);
//                    handled = true;
//                }
                if(keyCode==107){
                    keys[1] |= 0b00010000;
//                    Log.d("Button : ","Button_THUMBR "+String.valueOf(key));
//                    msg = "Button_THUMBR"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==106){
                    keys[1] |= 0b00100000;
//                    Log.d("Button : ","Button_THUMBL "+String.valueOf(key));
//                    msg = "Button_THUMBL"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==109){
                    keys[1] |= 0b01000000;
//                    Log.d("Button : ","Button Select "+String.valueOf(key));
//                    msg = "Button Select"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==108){
                    keys[1] |= 0b10000000;
//                    Log.d("Button : ","Button Start "+String.valueOf(key));
//                    msg = "Button Start"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==23){
//                    Log.d("Button : ","DPAD_CENTER");
//                    msg = "DPAD_CENTER";
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==103){
                    keys[2] |= 0b00000001;
//                    Log.d("Button : ","Button R1 "+String.valueOf(key));
//                    msg = "Button R1"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==102){
                    keys[2] |= 0b00000010;
//                    Log.d("Button : ","Button L1 "+String.valueOf(key));
//                    msg = "Button L1"+String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==105){
                    keys[2] |= 0b00000100;
//                    Log.d("Button : ","Button R2 "+String.valueOf(key));
//                    msg = "Button R2" + String.valueOf(key);
                    field_fragment.sendData(keys);
                    handled = true;
                }
                if(keyCode==104){
                    keys[2] |= 0b00001000;
//                    Log.d("Button : ","Button L2 "+String.valueOf(key));
//                    msg = "Button L2";
                    field_fragment.sendData(keys);
                    handled = true;
                }
            }
            if (handled) {
//                SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
//                spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                receiveText.append(spn);
                resetKey();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        receiveText = findViewById(R.id.receiveText);
        // Check if this event is from a joystick movement and process accordingly.
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();


            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);

            return true;
        }

        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);
            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {return true;}


    private void processJoystickInput(MotionEvent event,
                                    int historyPos) {
        field_fragment =
                (FieldFragment) fm.findFragmentById(R.id.fragment);
        InputDevice inputDevice = event.getDevice();
        receiveText = findViewById(R.id.receiveText);
//        String msg="";
        float xr, yl, xl, yr;
        int Xl, Yl, Xr, Yr;
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.

        xr = getCenteredAxis(event, inputDevice,
                 MotionEvent.AXIS_X, historyPos)*100000;
        yr = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos)*100000;
        yl = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ, historyPos)*100000;
        xl = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Z, historyPos)*100000;



        if((xr != 0) || (yr != 0) || (xl != 0) || (yl != 0)) {
            if((xr != 0) || (yr != 0)){
                Xr = Math.round(xr) & 0x3FFFF; // konvert dan potong menjadi 18 bit
                Yr = Math.round(yr) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//            Rmotions = motionToByte(xx,yy, Rmotions[0]);
                field_fragment.sendData(motionToByte(Xr,Yr, Rmotions[0]));
//                msg = "R " + (String.valueOf(xr) + " and " +String.valueOf(yr));
            }
//        msg = "P " + (String.valueOf(x) + " and " +String.valueOf(y));
//        motion= motion | (int)x<<4 | (int)y << 24; // id axis = 0
            if((xl != 0) || (yl != 0)){
                Xl = Math.round(xl) & 0x3FFFF; // konvert dan potong menjadi 18 bit
                Yl = Math.round(yl) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//            Lmotions = ;
                field_fragment.sendData(motionToByte(Xl,Yl, Lmotions[0]));
//                msg = "L " + (String.valueOf(xl) + " and " +String.valueOf(yl));
            }
        }else{
            field_fragment.sendData(Rmotions);
            field_fragment.sendData(Lmotions);
//            msg = "0";
        }

        if(getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos)==-1 &&
                getCenteredAxis(event, inputDevice,MotionEvent.AXIS_HAT_X, historyPos)==0){
            //atas
            keys[2]|=0b00010000;
//            msg = "DPAD-UP";
        }else if(getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos)==1 &&
                getCenteredAxis(event, inputDevice,MotionEvent.AXIS_HAT_X, historyPos)==0){
            //bawah
            keys[2]|=0b00100000;
//            msg = "DPAD-BOTTOM";
        } else if(getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos)==0 &&
                getCenteredAxis(event, inputDevice,MotionEvent.AXIS_HAT_X, historyPos)==1){
            //kanan
            keys[2]|=0b01000000;
//            msg = "DPAD-RIGHT";
        }else if(getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos)==0 &&
                getCenteredAxis(event, inputDevice,MotionEvent.AXIS_HAT_X, historyPos)==-1){
            //kiri
            keys[2]|=0b10000000;
//            msg = "DPAD-LEFT";
        }
        if(keys[2]!=0){
            field_fragment.sendData(keys);
            resetKey();
        }


//                msg = "R " + (String.valueOf(x) + " and " +String.valueOf(y));
//            motion = motion | 0b0100 | (int)x<<4 | (int)y << 24; // id axis = 1


//        if((xr == 0 && yr == 0)) {
//            // Do Nothing or handle DPAD input
//            field_fragment.sendData(Rmotions);
//        }else{
//            field_fragment.sendData(Rmotions);
//        }
//

//        else{
//
////            msg = String.valueOf(xx) + String.valueOf(yy);
//            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
//            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            receiveText.append(spn);
//            if((xr != 0) || (yr != 0) || (xl != 0) || (yl != 0)) {
//                if((xr != 0) || (yr != 0)){
//                    int xx = Math.round(xr) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//                    int yy = Math.round(yr) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//                    Rmotions = motionToByte(xx,yy, Rmotions[0]);
//                    field_fragment.sendData(Rmotions);
//                }
//                if((xl != 0) || (yl != 0)){
//                    int xx = Math.round(xl) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//                    int yy = Math.round(yl) & 0x3FFFF; // konvert dan potong menjadi 18 bit
//                    Lmotions = motionToByte(xx,yy, Lmotions[0]);
//                    field_fragment.sendData(Lmotions);
//                }
//
//            }else{
//                field_fragment.sendData(Rmotions);
//                field_fragment.sendData(Lmotions);
//            }
//        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.

        // Update the ship object based on the new x and y values
        resetMotion();
    }

    private byte[] motionToByte(int x, int y, byte axis){
        return new byte[]{
                (byte) (axis | ((x << 4) & 0xff)), // byte 0
                (byte) ((x >> 4) & 0xff), // byte 1
                (byte) (((x >> 12) & 0xff)| ((y<<6)& 0xff)), // byte 2
                (byte) ((y >> 2) & 0xff),   // byte 3
                (byte) ((y >> 10) & 0xff), // byte 4
        };
    }


}