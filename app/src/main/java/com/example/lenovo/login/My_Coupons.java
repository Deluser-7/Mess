package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class My_Coupons extends AppCompatActivity {

    private CardView tdbf,tdln,tdht,tddn;
    private CardView tmbf,tmln,tmht,tmdn;
    protected FirebaseAuth firebaseAuth;
    Calendar c = Calendar.getInstance();
    private TextView today,tomorrow;
    String DateToday,DateTomorrow;
    String roll_no;
    int hr;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);
        setVar();
        getRoll_setDate();
        retrieveCoupons();

        Calendar calendar = Calendar.getInstance();
        hr = calendar.get(Calendar.HOUR_OF_DAY);
        tdbf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Breakfast timing
                if(hr>=7 && hr<11){
                    Intent bfact = new Intent(My_Coupons.this,Use_Coupon.class);
                    String value = "Breakfast";
                    bfact.putExtra("value",value);
                    startActivity(bfact);
                }
                else
                    Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });

        tdln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lunch timing
                if(hr>=12 && hr<15){
                    Intent lnact = new Intent(My_Coupons.this,Use_Coupon.class);
                    String value = "Lunch";
                    lnact.putExtra("value",value);
                    startActivity(lnact);
                }
                else
                    Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });

        tdht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //High tea timing
                if(hr>=16 && hr<19){
                    Intent htact = new Intent(My_Coupons.this,Use_Coupon.class);
                    String value = "HighTea";
                    htact.putExtra("value",value);
                    startActivity(htact);
                }
                else
                    Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });

        tddn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dinner timing
                if(hr>=19 && hr<23){
                    Intent dnact = new Intent(My_Coupons.this,Use_Coupon.class);
                    String value = "Dinner";
                    dnact.putExtra("value",value);
                    startActivity(dnact);
                }
                else
                    Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });


        tmbf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });
        tmln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });
        tmht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });
        tmdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(My_Coupons.this, "Can't use this now !", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setVar(){
        tdbf= findViewById(R.id.btntdBreakfast);
        tdln= findViewById(R.id.btntdLunch);
        tdht= findViewById(R.id.btntdHighTea);
        tddn= findViewById(R.id.btntdDinner);
        tmbf= findViewById(R.id.btntmBreakfast);
        tmln= findViewById(R.id.btntmLunch);
        tmht= findViewById(R.id.btntmHighTea);
        tmdn= findViewById(R.id.btntmDinner);
        today= findViewById(R.id.tvToday);
        tomorrow= findViewById(R.id.tvTomorrow);
    }
    private void getRoll_setDate(){

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        roll_no = user.getDisplayName();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date TODAY = c.getTime();
        DateToday = simpleDateFormat.format(TODAY);
        today.setText(DateToday);

        c.add(Calendar.DATE,1);
        Date TOMORROW = c.getTime();
        DateTomorrow = simpleDateFormat.format(TOMORROW);
        tomorrow.setText(DateTomorrow);
    }
    private void retrieveCoupons(){
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(roll_no).child(DateToday).child("Breakfast").exists()){
                    tdbf.setVisibility(View.VISIBLE);
                }
                else
                    tdbf.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateToday).child("Lunch").exists()){
                    tdln.setVisibility(View.VISIBLE);
                }
                else
                    tdln.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateToday).child("HighTea").exists()){
                    tdht.setVisibility(View.VISIBLE);
                }
                else
                    tdht.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateToday).child("Dinner").exists()){
                    tddn.setVisibility(View.VISIBLE);
                }
                else
                    tddn.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Breakfast").exists()){
                    tmbf.setVisibility(View.VISIBLE);
                }
                else
                    tmbf.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Lunch").exists()){
                    tmln.setVisibility(View.VISIBLE);
                }
                else
                    tmln.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("HighTea").exists()){
                    tmht.setVisibility(View.VISIBLE);
                }
                else
                    tmht.setVisibility(View.GONE);

                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Dinner").exists()){
                    tmdn.setVisibility(View.VISIBLE);
                }
                else
                    tmdn.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(My_Coupons.this,"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
