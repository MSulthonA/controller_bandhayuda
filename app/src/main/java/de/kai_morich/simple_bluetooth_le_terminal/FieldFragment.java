package de.kai_morich.simple_bluetooth_le_terminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayDeque;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class FieldFragment extends Fragment implements ServiceConnection, SerialListener {

    private String deviceAddress;
    private enum Connected { False, Pending, True }

    private SerialService service;
    private boolean isLeftJoy=false, isLeftJoy_vib=false;
    private boolean isRightJoy=false, isRightJoy_vib=false;

    public byte[] Rmotions = new byte[5];
    public byte[] Lmotions = new byte[5];

    private TextView receiveText;
    private TextView sendText;
    private Vibrator vibrator;

    private TextView vLeftSpeed, vRightSpeed;

    private FieldFragment.Connected connected = FieldFragment.Connected.False;
    private boolean initialStart = true;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    JoystickView joystickLeft;


    public byte[] communication = new byte[5];
    //Variable data yang dikirim

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");

    }

    @Override
    public void onDestroy() {
        if (connected != FieldFragment.Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    private void resetCom(){
        communication[0]=1;
        communication[1]=0;
        communication[2]=0;
        communication[3]=0;
        communication[4]=0;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_field, container, false);
        receiveText = view.findViewById(R.id.receiveText);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        resetCom();

//        Toast.makeText(getActivity(),deviceAddress, Toast.LENGTH_SHORT).show();
        vibrator = (Vibrator) getActivity().getSystemService(getContext().VIBRATOR_SERVICE);

        JoystickView left_joystick = (JoystickView) view.findViewById(R.id.joystickView_left);
        left_joystick.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              receiveText.setText("left joystick");
              vibrator.vibrate(170);
          }
        });
        left_joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(isLeftJoy || (strength>10)){
                    int x = (int) (Math.cos(Math.toRadians((double)angle)) * strength *1000 );
                    int y = (int) (Math.sin(Math.toRadians((double)angle)) * strength *1000);
                    sendData(motionToByte(x,y,Lmotions[0]));
                    resetMotion();
                    isLeftJoy=true;
                    sendData(Lmotions);
                    if(strength==0) {
                        isLeftJoy = false;
                        isLeftJoy_vib = false;
                    }
                }
                if(!isLeftJoy_vib && strength > 10){
                    isLeftJoy_vib = true;
                    vibrator.vibrate(170);
                }
            }
        },50);
        JoystickView right_joystick = (JoystickView) view.findViewById(R.id.joystickView_right);
        right_joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(isRightJoy || (strength>30)){
                    int x = (int) (Math.cos(Math.toRadians((double)angle)) * strength *1000);
                    int y = (int) (Math.sin(Math.toRadians((double)angle)) * strength *1000);
                    sendData(motionToByte(x,y,Rmotions[0]));
                    resetMotion();
                    isRightJoy=true;
                    if(strength==0){
                        isRightJoy=false;
                        isRightJoy_vib=false;
                    }
                }
                if(!isRightJoy_vib&&strength>10){
                    isRightJoy_vib = true;
                    vibrator.vibrate(170);
                }
            }
        }, 50);


        // Tombol Samping
        // Kecepatan Pelempar Kiri(Left launcer speed) : Increment
        View sendA1 = view.findViewById(R.id.buttonA1);
        sendA1.setOnClickListener(v->{
            communication[0] = (byte)(communication[0] | 0b00000100);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        // Kecepatan Pelempar Kiri(Left launcer speed): Decrement
        View sendA2 = view.findViewById(R.id.buttonA2);
        sendA2.setOnClickListener(v -> {
            communication[0] = (byte)(communication[0] | 0b00001000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        //Kecepatan(+)
        View sendD1 = view.findViewById(R.id.buttonD1);
        sendD1.setOnClickListener(v -> {
            communication[0] = (byte)(communication[0] | 0b00010000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        //Kecepatan(-)
        View sendD2 = view.findViewById(R.id.buttonD2);
        sendD2.setOnClickListener(v -> {
            communication[0] = (byte)(communication[0] | 0b00100000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        //GRIP
        View sendA3 = view.findViewById(R.id.buttonA3);
        sendA3.setOnClickListener(v -> {
            communication[0] = (byte)(communication[0] | 0b01000000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        //NEXT
        View sendD3 = view.findViewById(R.id.buttonD3);
        sendD3.setOnClickListener(v -> {
            communication[0] = (byte)(communication[0] | 0b10000000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
//        //Posisi C
//        View sendB3 = view.findViewById(R.id.buttonB3);
//        sendB3.setOnClickListener(v -> {
//            communication[1] = (byte)(communication[1] | 0b00000001);
//            sendData(communication);
//            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
//            v.startAnimation(animation);
//            vibrator.vibrate(170);
//            resetCom();
//        });

        // Tombol Field
        View sendDo1 = view.findViewById(R.id.radio_domain1);
        sendDo1.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            communication[1] = (byte)(communication[1] | 0b00010000);
            sendData(communication);
            resetCom();
        });
        View sendDo2 = view.findViewById(R.id.radio_domain2);
        sendDo2.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            communication[1] = (byte)(communication[1] | 0b00100000);
            sendData(communication);
            resetCom();
        });
        View sendDo3 = view.findViewById(R.id.radio_domain3);
        sendDo3.setOnClickListener(v -> {
            communication[1] = (byte)(communication[1] | 0b01000000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        View sendAg1 = view.findViewById(R.id.radio_angkor1);
        sendAg1.setOnClickListener(v -> {
//            send("Ag1");
            communication[1] = (byte)(communication[1] | 0b10000000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });

        View sendAg2 = view.findViewById(R.id.radio_angkor2);
        sendAg2.setOnClickListener(v -> {
//            send("Ag2");
            communication[2] = (byte)(communication[2] | 0b00000001);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });

        View sendAg3 = view.findViewById(R.id.radio_angkor3);
        sendAg3.setOnClickListener(v -> {
//            send("Ag3");
            communication[2] = (byte)(communication[2] | 0b00000010);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        View sendAg4 = view.findViewById(R.id.radio_angkor4);
        sendAg4.setOnClickListener(v -> {
//            send("Ag4");
            communication[2] = (byte)(communication[2] | 0b00000100);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        View sendAg5 = view.findViewById(R.id.radio_angkor5);
        sendAg5.setOnClickListener(v -> {
//            send("Ag5");
            communication[2] = (byte)(communication[2] | 0b00001000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });

        View sendEm1 = view.findViewById(R.id.radio_enemy1);
        sendEm1.setOnClickListener(v -> {
//            send("Em1");
            communication[2] = (byte)(communication[2] | 0b00010000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        View sendEm2 = view.findViewById(R.id.radio_enemy2);
        sendEm2.setOnClickListener(v -> {
//            send("Em2");
            communication[2] = (byte)(communication[2] | 0b00100000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });
        View sendEm3 = view.findViewById(R.id.radio_enemy3);
        sendEm3.setOnClickListener(v -> {
//            send("Em3");
            communication[2] = (byte)(communication[2] | 0b01000000);
            sendData(communication);
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
            resetCom();
        });


        //State A
//        View sendC1 = view.findViewById(R.id.buttonC1);
//        sendC1.setOnClickListener(v -> {
//            communication[1] = (byte)(communication[1] | 0b00000010);
//            sendData(communication);
//            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
//            v.startAnimation(animation);
//            vibrator.vibrate(170);
//            resetCom();
////            send("C1");
//        });
//        //State B
//        View sendC2 = view.findViewById(R.id.buttonC2);
//        sendC2.setOnClickListener(v -> {
//            communication[1] = (byte)(communication[1] | 0b00000100);
//            sendData(communication);
//            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
//            v.startAnimation(animation);
//            vibrator.vibrate(170);
//            resetCom();
//        });
//        //State C
//        View sendC3 = view.findViewById(R.id.buttonC3);
//        sendC3.setOnClickListener(v -> {
//            communication[1] = (byte)(communication[1] | 0b00001000);
//            sendData(communication);
//            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
//            v.startAnimation(animation);
//            vibrator.vibrate(170);
//            resetCom();
//        });

        View socketConnection = view.findViewById(R.id.socketConnection);
        socketConnection.setOnClickListener(v -> {
            if(connected == Connected.True) {
                disconnect();
            }else if(connected == Connected.False){
                connect();
            }
            Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            v.startAnimation(animation);
            vibrator.vibrate(170);
        });

        return view;

    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//            status("connecting...");
            ((android.widget.Button)getView().findViewById(R.id.socketConnection)).setText("Connecting...");
            connected = FieldFragment.Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = FieldFragment.Connected.False;
        service.disconnect();
        ((android.widget.Button)getView().findViewById(R.id.socketConnection)).setText("C");
        (getView().findViewById(R.id.socketConnection)).setActivated(false);
    }

    public void sendData(byte[] data){
        if(connected != FieldFragment.Connected.True) {
//            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(ArrayDeque<byte[]> datas) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        for (byte[] data : datas) {
            String msg = new String(data);
//            spn.append(TextUtil.toHexString(data)).append('\n');
            if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg.charAt(0) == '\n') {
                    if(spn.length() >= 2) {
                        spn.delete(spn.length() - 2, spn.length());
                    } else {
                        Editable edt = receiveText.getEditableText();
                        if (edt != null && edt.length() >= 2)
                            edt.delete(edt.length() - 2, edt.length());
                    }
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }

//            spn.append(TextUtil.toCaretString(msg, newline.length() != 0));

        }
        receiveText.append(spn);
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
//        status("connected");
        connected = FieldFragment.Connected.True;
        ((android.widget.Button)getView().findViewById(R.id.socketConnection)).setText("Disconnect");
        (getView().findViewById(R.id.socketConnection)).setActivated(true);
    }


    @Override
    public void onSerialConnectError(Exception e) {
//        status("connection failed: " + e.getMessage());
        disconnect();
        ((android.widget.Button)getView().findViewById(R.id.socketConnection)).setText("Connect");
        (getView().findViewById(R.id.socketConnection)).setActivated(false);
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
        for (byte[] data : datas) {
            if ((data[0]&7) == 0) {
                vLeftSpeed = getView().findViewById(R.id.ViewLeftSpeed);
                vRightSpeed = getView().findViewById(R.id.ViewRightSpeed);
                View Do1 = getView().findViewById(R.id.radio_domain1);
                View Do2 = getView().findViewById(R.id.radio_domain2);
                View Do3 = getView().findViewById(R.id.radio_domain3);
                View Ang1 = getView().findViewById(R.id.radio_angkor1);
                View Ag2 = getView().findViewById(R.id.radio_angkor2);
                View Ang3 = getView().findViewById(R.id.radio_angkor3);
                View Ang4 = getView().findViewById(R.id.radio_angkor4);
                View Ang5 = getView().findViewById(R.id.radio_angkor5);
                View En1 = getView().findViewById(R.id.radio_enemy1);
                View En2 = getView().findViewById(R.id.radio_enemy2);
                View En3 = getView().findViewById(R.id.radio_enemy3);
//                if ((((data[3]&0xFF)<<1) | ((data[2]&0xFF)>>7)) != 0) vRightSpeed.setText(String.valueOf(((data[3]&0xFF)<<1) | ((data[2]&0xFF)>>7)));
//                else if ((((data[2]&0xFF)<<2) | ((data[1]&0xFF)>>6)) != 0)  vLeftSpeed.setText(String.valueOf(((data[2]&0xFF)<<2) | ((data[1]&0xFF)>>6)));
                if((data[1]&128) != 0) vRightSpeed.setText(String.valueOf(((data[3]&0xFF)) | ((data[4]&0xFF)<<8)));
                else if ((data[1]&64) != 0)  vLeftSpeed.setText(String.valueOf(((data[3]&0xFF)) | ((data[4]&0xFF)<<8)));
                if ((data[0] & 0b00001000)!=0 ){
                    if(!Do1.isActivated()) Do1.setActivated(true); else Do1.setActivated(false);
                }
                if ((data[0] &0b00010000)!=0){
                    if(!Do2.isActivated()) Do2.setActivated(true); else Do2.setActivated(false);
                }
                if ((data[0] & 0b00100000) !=0){
                    if(!Do3.isActivated()) Do3.setActivated(true); else Do3.setActivated(false);
                }
                if ((data[0] & 64) !=0 ){
                    if(!Ang1.isActivated()) Ang1.setActivated(true); else Ang1.setActivated(false);
                }
                if ((data[0] & 128) != 0){ // Ini di AND-kan karena menimbulkan error
                    if(!Ag2.isActivated()) Ag2.setActivated(true); else Ag2.setActivated(false);
                }
                if ((data[1] & 1) != 0){
                    if(!Ang3.isActivated()) Ang3.setActivated(true); else Ang3.setActivated(false);
                }
                if ((data[1] & 2) !=0){
                    if(!Ang4.isActivated()) Ang4.setActivated(true); else Ang4.setActivated(false);
                }
                if ((data[1] & 4)!=0){
                    if(!Ang5.isActivated()) Ang5.setActivated(true); else Ang5.setActivated(false);
                }
                if ((data[1] & 8)!=0){
                    if(!En1.isActivated()) En1.setActivated(true); else En1.setActivated(false);
                }
                if ((data[1] & 16)!=0){
                    if(!En2.isActivated()) En2.setActivated(true); else En2.setActivated(false);
                }
                if ((data[1] & 32)!=0){
                    if(!En3.isActivated()) En3.setActivated(true); else En3.setActivated(false);
                }
            }
        }
    }

    @Override
    public void onSerialIoError(Exception e) {
//        status("connection lost: " + e.getMessage());
        disconnect();
        ((android.widget.Button)getView().findViewById(R.id.socketConnection)).setText("connect");
        (getView().findViewById(R.id.socketConnection)).setActivated(false);
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
