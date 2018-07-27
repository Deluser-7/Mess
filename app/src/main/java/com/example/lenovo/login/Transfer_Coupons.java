package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Transfer_Coupons extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private DatabaseReference databaseReference;
    private Button Transfer;
    private EditText Roll_rec;
    private CheckBox Bf,Ln,Ht,Dn;
    Calendar c = Calendar.getInstance();
    String bf,ln,ht,dn,tmbf,tmln,tmht,tmdn,DateToday,DateTomorrow,receiver,day_of_coupon,roll_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_coupons);
        setvar();
        spinner_getroll_date();

        Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isConnected(Transfer_Coupons.this))
                    buildDialog(Transfer_Coupons.this).show();
                else{
                    receiver=Roll_rec.getText().toString();
                    databaseReference=FirebaseDatabase.getInstance().getReference();
                    if(!receiver.isEmpty()){
                        if(Bf.isChecked() || Ln.isChecked() || Dn.isChecked() || Ht.isChecked()){
                            if(receiver.equals(roll_no))
                                Toast.makeText(Transfer_Coupons.this, "Stupid Animal", Toast.LENGTH_SHORT).show();
                            else
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(receiver).exists()){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Coupons.this);
                                            builder.setMessage("Are you sure want to transfer?")
                                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            transferCoupons();
                                                        }
                                                    })
                                                    .setNegativeButton("NO",null);
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                        else
                                            Toast.makeText(Transfer_Coupons.this, "Receiver does not exist.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }
                        else
                            Toast.makeText(Transfer_Coupons.this, "Please select meal coupon(s)", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(Transfer_Coupons.this, "Please enter receiver's roll no.", Toast.LENGTH_SHORT).show();
                    retrieveCoupons();
                }
            }
        });
    }

    private void setvar(){
        Roll_rec= findViewById(R.id.etReceiverRoll);
        Bf= findViewById(R.id.cbBreakfast);
        Ln= findViewById(R.id.cbLunch);
        Ht= findViewById(R.id.cbHightea);
        Dn= findViewById(R.id.cbDinner);
        Transfer= findViewById(R.id.btnTransferC);
    }
    private void spinner_getroll_date(){
        Date TODAY = c.getTime();
        c.add(Calendar.DATE,1);
        Date TOMORROW = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateToday = dateFormat.format(TODAY);
        DateTomorrow = dateFormat.format(TOMORROW);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        roll_no = user.getDisplayName();

        //Spinner
        Spinner spinner=findViewById(R.id.spnDay);
        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,R.array.DAY,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
    private void retrieveCoupons(){
        FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(day_of_coupon.equals("Today")) {
                    if (dataSnapshot.child(roll_no).child(DateToday).child("Breakfast").exists()) {
                        bf = dataSnapshot.child(roll_no).child(DateToday).child("Breakfast").getValue().toString();
                        if(bf.equals("Y"))
                            Bf.setEnabled(true);
                    }
                    else
                        Bf.setEnabled(false);

                    if (dataSnapshot.child(roll_no).child(DateToday).child("Lunch").exists()) {
                        ln = dataSnapshot.child(roll_no).child(DateToday).child("Lunch").getValue().toString();
                        if(ln.equals("Y"))
                            Ln.setEnabled(true);
                    }
                    else
                        Ln.setEnabled(false);

                    if (dataSnapshot.child(roll_no).child(DateToday).child("HighTea").exists()) {
                        ht = dataSnapshot.child(roll_no).child(DateToday).child("HighTea").getValue().toString();
                        if(ht.equals("Y"))
                            Ht.setEnabled(true);
                    }
                    else
                        Ht.setEnabled(false);

                    if (dataSnapshot.child(roll_no).child(DateToday).child("Dinner").exists()) {
                        dn = dataSnapshot.child(roll_no).child(DateToday).child("Dinner").getValue().toString();
                        if(dn.equals("Y"))
                            Dn.setEnabled(true);
                    }
                    else
                        Dn.setEnabled(false);
                }

                else {
                    if (dataSnapshot.child(roll_no).child(DateTomorrow).child("Breakfast").exists()) {
                        tmbf = dataSnapshot.child(roll_no).child(DateTomorrow).child("Breakfast").getValue().toString();
                        if(tmbf.equals("Y"))
                            Bf.setEnabled(true);
                    }
                    else
                        Bf.setEnabled(false);
                    if (dataSnapshot.child(roll_no).child(DateTomorrow).child("Lunch").exists()) {
                        tmln = dataSnapshot.child(roll_no).child(DateTomorrow).child("Lunch").getValue().toString();
                        if(tmln.equals("Y"))
                            Ln.setEnabled(true);
                    }
                    else
                        Ln.setEnabled(false);
                    if (dataSnapshot.child(roll_no).child(DateTomorrow).child("HighTea").exists()) {
                        tmht = dataSnapshot.child(roll_no).child(DateTomorrow).child("HighTea").getValue().toString();
                        if(tmht.equals("Y"))
                            Ht.setEnabled(true);
                    }
                    else
                        Ht.setEnabled(false);
                    if (dataSnapshot.child(roll_no).child(DateTomorrow).child("Dinner").exists()) {
                        tmdn = dataSnapshot.child(roll_no).child(DateTomorrow).child("Dinner").getValue().toString();
                        if(tmdn.equals("Y"))
                            Dn.setEnabled(true);
                    }
                    else
                        Dn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Transfer_Coupons.this,"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void transferCoupons(){
        FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(receiver).exists()){
                    if(day_of_coupon.equals("Today")){
                        databaseReference.child(receiver).child(DateToday).child("timestamp").setValue(ServerValue.TIMESTAMP);
                        if(Bf.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateToday).child("Breakfast").exists()) {
                                        databaseReference.child(receiver).child(DateToday).child("Breakfast").setValue("Y");
                                        databaseReference.child(roll_no).child(DateToday).child("Breakfast").removeValue();
                                        Bf.setEnabled(false);
                                        Bf.setChecked(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Ln.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateToday).child("Lunch").exists()) {
                                        databaseReference.child(receiver).child(DateToday).child("Lunch").setValue("Y");
                                        databaseReference.child(roll_no).child(DateToday).child("Lunch").removeValue();
                                        Ln.setChecked(false);
                                        Ln.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Ht.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateToday).child("HighTea").exists()) {
                                        databaseReference.child(receiver).child(DateToday).child("HighTea").setValue("Y");
                                        databaseReference.child(roll_no).child(DateToday).child("HighTea").removeValue();
                                        Ht.setChecked(false);
                                        Ht.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Dn.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateToday).child("Dinner").exists()) {
                                        databaseReference.child(receiver).child(DateToday).child("Dinner").setValue("Y");
                                        databaseReference.child(roll_no).child(DateToday).child("Dinner").removeValue();
                                        Dn.setChecked(false);
                                        Dn.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Toast.makeText(Transfer_Coupons.this, "Transfer Successful", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        databaseReference.child(receiver).child(DateTomorrow).child("timestamp").setValue(ServerValue.TIMESTAMP);
                        if(Bf.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateTomorrow).child("Breakfast").exists()) {
                                        databaseReference.child(receiver).child(DateTomorrow).child("Breakfast").setValue("Y");
                                        databaseReference.child(roll_no).child(DateTomorrow).child("Breakfast").removeValue();
                                        Bf.setChecked(false);
                                        Bf.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Ln.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateTomorrow).child("Lunch").exists()) {
                                        databaseReference.child(receiver).child(DateTomorrow).child("Lunch").setValue("Y");
                                        databaseReference.child(roll_no).child(DateTomorrow).child("Lunch").removeValue();
                                        Ln.setChecked(false);
                                        Ln.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Ht.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateTomorrow).child("HighTea").exists()) {
                                        databaseReference.child(receiver).child(DateTomorrow).child("HighTea").setValue("Y");
                                        databaseReference.child(roll_no).child(DateTomorrow).child("HighTea").removeValue();
                                        Ht.setEnabled(false);
                                        Ht.setChecked(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(Dn.isChecked()){
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(receiver).child(DateTomorrow).child("Dinner").exists()) {
                                        databaseReference.child(receiver).child(DateTomorrow).child("Dinner").setValue("Y");
                                        databaseReference.child(roll_no).child(DateTomorrow).child("Dinner").removeValue();
                                        Dn.setChecked(false);
                                        Dn.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Transfer_Coupons.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Toast.makeText(Transfer_Coupons.this, "Transfer Successful", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                    Toast.makeText(Transfer_Coupons.this, "Receiver does not exist.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());

        }
        else
            return false;
    }
    private android.app.AlertDialog.Builder buildDialog(Context c) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Make sure you have an active internet connection to continue.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        return builder;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);

        day_of_coupon = parent.getItemAtPosition(position).toString();
        Bf.setEnabled(false);
        Ln.setEnabled(false);
        Ht.setEnabled(false);
        Dn.setEnabled(false);
        Bf.setChecked(false);
        Ht.setChecked(false);
        Ln.setChecked(false);
        Dn.setChecked(false);
        retrieveCoupons();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}