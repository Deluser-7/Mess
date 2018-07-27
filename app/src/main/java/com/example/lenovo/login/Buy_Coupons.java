package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.lang.String;

public class Buy_Coupons extends AppCompatActivity implements PaymentResultListener{

    private CheckBox tdAll,tmwAll,cbtdBreakfast, cbtdLunch,cbtdHightea,cbtdDinner,cbtmwBreakfast, cbtmwLunch,cbtmwHightea,cbtmwDinner;
    String bf,ln,ht,dn,tmbf,tmln,tmht,tmdn,roll_no,DateToday,DateTomorrow;
    private DatabaseReference databaseReference;
    private TextView totPrice,tdDAY,tmwDAY;
    private ProgressDialog progressDialog;
    Calendar c = Calendar.getInstance();
    private Button confirm,btnCheckout;
    private FirebaseAuth firebaseAuth;
    private int tPrice;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coupons);
        setVar();
        setDay();
        retrieveData();
        retrieveCoupons();
        disableCheckBoxes();
        btnCheckout.setEnabled(false);
        totPrice.setText("Total Price = Rs. 0");
        progressDialog = new ProgressDialog(Buy_Coupons.this);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price();
                if(tPrice > 0){
                    selectedCoupons();
                    btnCheckout.setEnabled(true);
                    Toast.makeText(Buy_Coupons.this,"Confirmed",Toast.LENGTH_SHORT).show();
                }
                else{
                    btnCheckout.setEnabled(false);
                    Toast.makeText(Buy_Coupons.this,"Please select meal coupon(s).",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price();
                if(tPrice>0){
                    selectedCoupons();
                    payment(tPrice);    //setting price for Razorpay
                    progressDialog.setMessage("Redirecting...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                }
                else{
                    btnCheckout.setEnabled(false);
                    Toast.makeText(Buy_Coupons.this,"Please select meal coupon(s).",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setVar(){
        cbtdBreakfast= findViewById(R.id.cbTdBreakfast);
        cbtdHightea= findViewById(R.id.cbTdHighTea);
        cbtdLunch=findViewById(R.id.cbTdLunch);
        cbtdDinner= findViewById(R.id.cbTdDinner);
        cbtmwBreakfast= findViewById(R.id.cbTmWBreakfast);
        cbtmwLunch= findViewById(R.id.cbTmWLunch);
        cbtmwHightea= findViewById(R.id.cbTmwHighTea);
        cbtmwDinner= findViewById(R.id.cbTmWDinner);
        tdAll= findViewById(R.id.cbTdALL);
        tmwAll= findViewById(R.id.cbTmWALL);
        totPrice= findViewById(R.id.tvTP);
        tmwDAY= findViewById(R.id.tmwDAY);
        tdDAY= findViewById(R.id.tdDAY);
        btnCheckout= findViewById(R.id.btnCheckout);
        confirm= findViewById(R.id.btnDONE);
    }
    private void setDay(){
        int day = c.get(Calendar.DAY_OF_WEEK);
        if(day ==1){
            tdDAY.setText("SUNDAY");
            tmwDAY.setText("MONDAY");
        }
        if(day ==2){
            tdDAY.setText("MONDAY");
            tmwDAY.setText("TUESDAY");
        }
        if(day ==3){
            tdDAY.setText("TUESDAY");
            tmwDAY.setText("WEDNESDAY");
        }
        if(day ==4){
            tdDAY.setText("WEDNESDAY");
            tmwDAY.setText("THURSDAY");
        }
        if(day ==5){
            tdDAY.setText("THURSDAY");
            tmwDAY.setText("FRIDAY");
        }
        if(day ==6){
            tdDAY.setText("FRIDAY");
            tmwDAY.setText("SATURDAY");
        }
        if(day ==7){
            tdDAY.setText("SATURDAY");
            tmwDAY.setText("SUNDAY");
        }
        //TODAY's &TOMORROW's DATE
        Date TODAY = c.getTime();
        c.add(Calendar.DATE,1);
        Date TOMORROW = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateToday = dateFormat.format(TODAY).toString();
        DateTomorrow = dateFormat.format(TOMORROW).toString();


    }
    private void price(){
        int bp=40,lp=50,hp=30,dp=60,allp=140;
        tPrice=0;
        //For Today:-
        if(tdAll.isChecked()){
            tPrice+=allp;
            totPrice.setText("Total Price = Rs. "+tPrice);
        }
        else if(cbtdBreakfast.isChecked() && cbtdLunch.isChecked() && cbtdHightea.isChecked() && cbtdDinner.isChecked()){
            tdAll.setChecked(true);
            cbtdBreakfast.setChecked(false);
            cbtdDinner.setChecked(false);
            cbtdHightea.setChecked(false);
            cbtdLunch.setChecked(false);
            tPrice+=allp;
            totPrice.setText("Total Price = Rs. "+tPrice);
        }
        else{
            if(cbtdBreakfast.isChecked()){
                tPrice+=bp;
                totPrice.setText("Total Price = Rs. "+tPrice);
            }
            if(cbtdLunch.isChecked()){
                tPrice+=lp;
                totPrice.setText("Total Price = Rs. "+tPrice);
            }
            if(cbtdHightea.isChecked()){
                tPrice+=hp;
                totPrice.setText("Total Price = Rs. "+tPrice);
            }
            if(cbtdDinner.isChecked()){
                tPrice+=dp;
                totPrice.setText("Total Price = Rs. "+tPrice);
            }
        }
        //For Tomorrow:-
        if(tmwAll.isChecked()){
            tPrice+=allp;
            totPrice.setText("Total Price = Rs. "+tPrice);
        }
        else if(cbtmwBreakfast.isChecked() && cbtmwLunch.isChecked() && cbtmwHightea.isChecked() && cbtmwDinner.isChecked()){
            tmwAll.setChecked(true);
            cbtmwBreakfast.setChecked(false);
            cbtmwDinner.setChecked(false);
            cbtmwHightea.setChecked(false);
            cbtmwLunch.setChecked(false);
            tPrice+=allp;
            totPrice.setText("Total Price = Rs. "+tPrice);
        }
        else {
            if (cbtmwBreakfast.isChecked()) {
                tPrice += bp;
                totPrice.setText("Total Price = Rs. "+ tPrice);
            }
            if (cbtmwLunch.isChecked()) {
                tPrice += lp;
                totPrice.setText("Total Price = Rs. "+ tPrice);
            }
            if (cbtmwHightea.isChecked()) {
                tPrice += hp;
                totPrice.setText("Total Price = Rs. "+ tPrice);
            }
            if (cbtmwDinner.isChecked()) {
                tPrice += dp;
                totPrice.setText("Total Price = Rs. " + tPrice);
            }
        }
        if(tdAll.isChecked()){
            cbtdBreakfast.setChecked(false);
            cbtdDinner.setChecked(false);
            cbtdHightea.setChecked(false);
            cbtdLunch.setChecked(false);
        }
        if(tmwAll.isChecked()){
            cbtmwBreakfast.setChecked(false);
            cbtmwDinner.setChecked(false);
            cbtmwHightea.setChecked(false);
            cbtmwLunch.setChecked(false);
        }
        totPrice.setText("Total Price = Rs. " + tPrice);
    }
    private void selectedCoupons(){
        setNull();
        if(tdAll.isChecked()){
            bf = "Y";
            ln = "Y";
            ht = "Y";
            dn = "Y";
        }
        else{
            if(cbtdBreakfast.isChecked()){
                bf="Y";
            }
            if(cbtdLunch.isChecked()){
                ln="Y";
            }
            if(cbtdHightea.isChecked()){
               ht="Y";
            }
            if(cbtdDinner.isChecked()){
                dn="Y";
            }
        }
        //For Tomorrow:-
        if(tmwAll.isChecked()){
            tmbf = "Y";
            tmln = "Y";
            tmht = "Y";
            tmdn = "Y";
        }
        else {
            if (cbtmwBreakfast.isChecked()) {
                tmbf="Y";
            }
            if (cbtmwLunch.isChecked()) {
                tmln="Y";
            }
            if (cbtmwHightea.isChecked()) {
                tmht="Y";
            }
            if (cbtmwDinner.isChecked()) {
                tmdn="Y";
            }
        }
    }
    private void setNull(){
        if(cbtdBreakfast.isEnabled())
            bf=null;
        if(cbtdLunch.isEnabled())
            ln=null;
        if(cbtdHightea.isEnabled())
            ht=null;
        if(cbtdDinner.isEnabled())
            dn=null;
        if(cbtmwBreakfast.isEnabled())
            tmbf=null;
        if(cbtmwLunch.isEnabled())
            tmln=null;
        if(cbtmwHightea.isEnabled())
            tmht=null;
        if(cbtmwDinner.isEnabled())
            tmdn=null;
    }
    private void retrieveData(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        roll_no = user.getDisplayName();
    }       // getting roll no.
    private void retrieveCoupons(){
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(roll_no).child(DateToday).child("Breakfast").exists()){
                    bf=dataSnapshot.child(roll_no).child(DateToday).child("Breakfast").getValue().toString();
                    cbtdBreakfast.setChecked(false);
                    cbtdBreakfast.setEnabled(false);
                    tdAll.setChecked(false);
                    tdAll.setEnabled(false);
                }
                if(dataSnapshot.child(roll_no).child(DateToday).child("Lunch").exists()){
                    ln=dataSnapshot.child(roll_no).child(DateToday).child("Lunch").getValue().toString();
                    cbtdLunch.setEnabled(false);
                    cbtdLunch.setChecked(false);
                    tdAll.setChecked(false);
                    tdAll.setEnabled(false);
                }
                if(dataSnapshot.child(roll_no).child(DateToday).child("HighTea").exists()){
                    ht=dataSnapshot.child(roll_no).child(DateToday).child("HighTea").getValue().toString();
                    cbtdHightea.setEnabled(false);
                    cbtdHightea.setChecked(false);
                    tdAll.setChecked(false);
                    tdAll.setEnabled(false);
                }
                if(dataSnapshot.child(roll_no).child(DateToday).child("Dinner").exists()){
                    dn=dataSnapshot.child(roll_no).child(DateToday).child("Dinner").getValue().toString();
                    cbtdDinner.setEnabled(false);
                    cbtdDinner.setChecked(false);
                    tdAll.setChecked(false);
                    tdAll.setEnabled(false);
                }


                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Breakfast").exists()){
                    tmbf=dataSnapshot.child(roll_no).child(DateTomorrow).child("Breakfast").getValue().toString();
                    cbtmwBreakfast.setEnabled(false);
                    cbtmwBreakfast.setChecked(false);
                    tmwAll.setEnabled(false);
                    tmwAll.setChecked(false);
                }
                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Lunch").exists()){
                    tmln=dataSnapshot.child(roll_no).child(DateTomorrow).child("Lunch").getValue().toString();
                    cbtmwLunch.setEnabled(false);
                    cbtmwLunch.setChecked(false);
                    tmwAll.setEnabled(false);
                    tmwAll.setChecked(false);
                }
                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("HighTea").exists()){
                    tmht=dataSnapshot.child(roll_no).child(DateTomorrow).child("HighTea").getValue().toString();
                    cbtmwHightea.setEnabled(false);
                    cbtmwHightea.setChecked(false);
                    tmwAll.setEnabled(false);
                    tmwAll.setChecked(false);
                }
                if(dataSnapshot.child(roll_no).child(DateTomorrow).child("Dinner").exists()){
                    tmdn=dataSnapshot.child(roll_no).child(DateTomorrow).child("Dinner").getValue().toString();
                    cbtmwDinner.setEnabled(false);
                    cbtmwDinner.setChecked(false);
                    tmwAll.setEnabled(false);
                    tmwAll.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Buy_Coupons.this,"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
    }    // getting purchased coupon data and checkbox disabling
    private void sendCouponData(){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Helper_class today = new Helper_class(bf,ln,ht,dn);
        databaseReference.child(roll_no).child(DateToday).setValue(today);
        databaseReference.child(roll_no).child(DateToday).child("timestamp").setValue(ServerValue.TIMESTAMP);

        Helper_class tomorrow = new Helper_class(tmbf,tmln,tmht,tmdn);
        databaseReference.child(roll_no).child(DateTomorrow).setValue(tomorrow);
        databaseReference.child(roll_no).child(DateTomorrow).child("timestamp").setValue(ServerValue.TIMESTAMP);

        adminData();
    }
    private void adminData(){

        if(cbtdBreakfast.isChecked()) databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Breakfast").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Breakfast").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        if(cbtmwBreakfast.isChecked()) databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Breakfast").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Breakfast").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        if(cbtdLunch.isChecked()) databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("Lunch").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("Lunch").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(cbtmwLunch.isChecked()) databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("Lunch").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("Lunch").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(cbtdHightea.isChecked()) databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("HighTea").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("HighTea").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(cbtmwHightea.isChecked()) databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("HighTea").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("HighTea").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(cbtdDinner.isChecked()) databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("Dinner").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("Dinner").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(cbtmwDinner.isChecked()) databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.child("Dinner").getValue();
                Long aftValue = value + 1;
                dataSnapshot.child("Dinner").getRef().setValue(aftValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(tdAll.isChecked()){
            databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Breakfast").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Breakfast").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Lunch").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Lunch").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("HighTea").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("HighTea").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Dinner").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Dinner").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateToday).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("ALL").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("ALL").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        if(tmwAll.isChecked()){
            databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Breakfast").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Breakfast").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Lunch").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Lunch").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("HighTea").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("HighTea").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("Dinner").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("Dinner").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("Admin").child(DateTomorrow).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long value = (Long) dataSnapshot.child("ALL").getValue();
                    Long aftValue = value + 1;
                    dataSnapshot.child("ALL").getRef().setValue(aftValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }          // updating coupons data in admin section
    private void disableCheckBoxes(){
        int hr = c.get(Calendar.HOUR_OF_DAY);

        if(hr>=10){
            cbtdBreakfast.setEnabled(false);
            tdAll.setEnabled(false);
        }
        if(hr>=14){
            cbtdLunch.setEnabled(false);
        }
        if(hr>=18){
            cbtdHightea.setEnabled(false);
        }
        if(hr>=22){
            cbtdDinner.setEnabled(false);
        }

        if(!cbtmwBreakfast.isEnabled() || !cbtmwLunch.isEnabled() || !cbtmwHightea.isEnabled() || !cbtmwDinner.isEnabled())
            tmwAll.setEnabled(false);

        if(!cbtdBreakfast.isEnabled() || !cbtdLunch.isEnabled() || !cbtdHightea.isEnabled() || !cbtdDinner.isEnabled())
            tdAll.setEnabled(false);
    }

    private void payment(int price){
        progressDialog.dismiss();
        String OrderId = "" + System.currentTimeMillis();
        price = price*100;

        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        final Activity activity = this;

        try{
            JSONObject options = new JSONObject();
            options.put("description",OrderId);
            options.put("currency","INR");
            options.put("amount",price);
            checkout.open(activity,options);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        progressDialog.dismiss();
        Toast.makeText(this, "Payment is successful", Toast.LENGTH_SHORT).show();
        sendCouponData();
        finish();
        startActivity(new Intent(Buy_Coupons.this,My_Coupons.class));
    }

    @Override
    public void onPaymentError(int i, String s) {
        progressDialog.dismiss();
        Toast.makeText(this, "Payment Failed !", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Buy_Coupons.this,Logged_In.class));
        finish();
    }
}
