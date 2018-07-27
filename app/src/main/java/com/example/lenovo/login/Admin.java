package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Admin extends AppCompatActivity {

    private String DateT,DateTm;
    private Button reset,refresh;
    private TextView tDate,tmDate;
    private TextView bf,ln,dn,ht,all;
    private TextView bf1,ln1,dn1,ht1,all1;
    private Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setVar();
        getCoupons();

        Date Today = c.getTime();
        c.add(Calendar.DATE,1);
        Date Tomorrow = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formaT =new SimpleDateFormat("dd-MM-yyyy");
        DateT = formaT.format(Today);
        DateTm = formaT.format(Tomorrow);
        tDate.setText(DateT);
        tmDate.setText(DateTm);

        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS);

        //Deleting admin data
        DatabaseReference admin = FirebaseDatabase.getInstance().getReference("Admin");
        Query oldItems1 = admin.orderByChild("timestamp").endAt(cutoff);
        oldItems1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    itemSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoupons();
                Toast.makeText(Admin.this, "Done", Toast.LENGTH_SHORT).show();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin.this,Change_Pass_Admin.class));
            }
        });
    }

    private void setVar(){
        tDate=findViewById(R.id.tvaTdate);
        tmDate=findViewById(R.id.tvaTmDate);
        bf=findViewById(R.id.tvaBreakfast);
        ln=findViewById(R.id.tvaLunch);
        ht=findViewById(R.id.tvaHighTea);
        dn=findViewById(R.id.tvaDinner);
        all=findViewById(R.id.tvaAll);
        reset=findViewById(R.id.btnaResetPass);
        refresh=findViewById(R.id.btn_refresh);
        bf1=findViewById(R.id.tvaBreakfastN);
        ln1=findViewById(R.id.tvaLunchN);
        ht1=findViewById(R.id.tvaHighTeaN);
        dn1=findViewById(R.id.tvaDinnerN);
        all1=findViewById(R.id.tvaAllN);
    }
    private void getCoupons(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String tb=dataSnapshot.child("Admin").child(DateT).child("Breakfast").getValue().toString();
                String tl=dataSnapshot.child("Admin").child(DateT).child("Lunch").getValue().toString();
                String th=dataSnapshot.child("Admin").child(DateT).child("HighTea").getValue().toString();
                String td=dataSnapshot.child("Admin").child(DateT).child("Dinner").getValue().toString();
                String tall=dataSnapshot.child("Admin").child(DateT).child("ALL").getValue().toString();

                String tmb=dataSnapshot.child("Admin").child(DateTm).child("Breakfast").getValue().toString();
                String tml=dataSnapshot.child("Admin").child(DateTm).child("Lunch").getValue().toString();
                String tmh=dataSnapshot.child("Admin").child(DateTm).child("HighTea").getValue().toString();
                String tmd=dataSnapshot.child("Admin").child(DateTm).child("Dinner").getValue().toString();
                String tmall=dataSnapshot.child("Admin").child(DateTm).child("ALL").getValue().toString();

                bf.setText("Breakfast -                 "+tb);
                ln.setText("Lunch -                       "+tl);
                ht.setText("High Tea -                  "+th);
                dn.setText("Dinner -                      "+td);
                all.setText("ALL -                           "+tall);

                bf1.setText("Breakfast -                 "+tmb);
                ln1.setText("Lunch -                       "+tml);
                ht1.setText("High Tea -                  "+tmh);
                dn1.setText("Dinner -                      "+tmd);
                all1.setText("ALL -                           "+tmall);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
