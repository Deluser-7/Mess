package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Use_Coupon extends AppCompatActivity {

    String today;
    String userRoll;
    String mealValue="";
    private Button approve;
    Calendar c = Calendar.getInstance();
    private TextView roll,time,tdDAY,date,meal,msg;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_coupon);
        setVar();
        setDay();
        setRoll();

        Bundle extras = getIntent().getExtras();
        if(extras != null) mealValue = extras.getString("value");
        meal.setText(mealValue);

        Date d = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        today = dateFormat.format(d);
        date.setText(today);

        Thread myThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();


        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check for live internet connection by a ping to google
                //Can't use coupon without internet
                AlertDialog.Builder buildeR = new AlertDialog.Builder(Use_Coupon.this);
                buildeR.setMessage("Are you sure want to use this coupon?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                approve_delete();
                            }
                        })
                        .setNegativeButton("No",null);
                AlertDialog alert = buildeR.create();
                alert.show();
            }
        });
    }

    private void setVar(){
        roll= findViewById(R.id.tvUser_Roll);
        time= findViewById(R.id.tvTime);
        tdDAY= findViewById(R.id.tvDAY);
        date= findViewById(R.id.tvDATE);
        meal= findViewById(R.id.tvMEAL);
        msg=findViewById(R.id.tvMsg);
        approve= findViewById(R.id.btnApprove);
    }
    private void setDay(){
        int day = c.get(Calendar.DAY_OF_WEEK);
        if(day ==1) tdDAY.setText("SUNDAY");
        if(day ==2) tdDAY.setText("MONDAY");
        if(day ==3) tdDAY.setText("TUESDAY");
        if(day ==4) tdDAY.setText("WEDNESDAY");
        if(day ==5) tdDAY.setText("THURSDAY");
        if(day ==6) tdDAY.setText("FRIDAY");
        if(day ==7) tdDAY.setText("SATURDAY");
    }
    private void setRoll(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        userRoll = user.getDisplayName();
        roll.setText(userRoll);
    }
    private void approve_delete(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(userRoll).child(today).child(mealValue).removeValue();
                approve.setVisibility(View.INVISIBLE);
                msg.setText("Enjoy Your Meal");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
   /* public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }*/
    private android.app.AlertDialog.Builder buildDialog(Context c){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Make sure you have an active internet connection to continue. Press Ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

    //running timer
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date dt = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String curTime = dateFormat.format(dt);
                    time.setText(curTime);
                }catch (Exception ignored) {}
            }
        });
    }
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) try {
                doWork();
                Thread.sleep(1000); // Pause of 1 Second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
            }
        }
    }

}
