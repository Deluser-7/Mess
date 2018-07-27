package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Logged_In extends AppCompatActivity {

    Long reference;
    String roll_no,degree;
    private ImageView imageView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    Calendar c = Calendar.getInstance();
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private TextView Suggestions, status,textView;
    private CardView Transfer, MyCoupons, Menu, Buy, ContactUs, logout, deluser,AboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isConnected(Logged_In.this))
            builderDialog(Logged_In.this).show();
        else{
            setContentView(R.layout.activity_logged_in);
            setVar();
            create();
            disableBtns();
            status.setText("OFFLINE");
            retriveData();

            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            final String email=firebaseUser.getEmail();

            Menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog=new ProgressDialog(Logged_In.this);
                    progressDialog.setMessage("Opening menu...");
                    progressDialog.show();
                    FirebaseStorage.getInstance().getReferenceFromUrl("gs://login-e250c.appspot.com").child("Menu.pdf")
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent intent=new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(uri,"application/pdf");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Logged_In.this,"Couldn't Get URL",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            Buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Logged_In.this, Buy_Coupons.class));
                }
            });

            Transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Logged_In.this, Transfer_Coupons.class));
                }
            });

            MyCoupons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Logged_In.this, My_Coupons.class));
                }
            });

            ContactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Logged_In.this, Contact.class));
                }
            });
            AboutUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Logged_In.this, About.class));
                }
            });
            Suggestions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "ee170002001@iiti.ac.in"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestions/Complaints related to KANAKA app");
                    startActivity(emailIntent);

                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Logged_In.this);
                    alert.setMessage("Are you sure want to logout ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { Logoutfn();
                        }
                    })
                            .setNegativeButton("No",null);

                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            });

            deluser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AlertDialog.Builder alert = new AlertDialog.Builder(Logged_In.this);
                    final EditText editText = new EditText(Logged_In.this);
                    alert.setTitle("PASSWORD");
                    alert.setMessage("Please enter your current password");
                    alert.setView(editText);
                    editText.setHint("Password");

                    alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String password = editText.getText().toString();
                            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful()){
                                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                                        assert user != null;
                                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                databaseReference.child(roll_no).removeValue();
                                                firebaseAuth.signOut();
                                                user.delete();
                                                Toast.makeText(Logged_In.this, "Account deleted.", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(new Intent(Logged_In.this,Login.class));
                                            }
                                        });

                                    }
                                    else{
                                        Toast.makeText(Logged_In.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            });
        }
    }
    private void setVar(){
        Menu= findViewById(R.id.btnMenu);
        Buy= findViewById(R.id.btnBuy);
        ContactUs= findViewById(R.id.btnContactUs);
        AboutUs= findViewById(R.id.btnAboutUs);
        Suggestions= findViewById(R.id.tvSuggestions);
        Transfer= findViewById(R.id.btnTransfer);
        MyCoupons= findViewById(R.id.btnMyCoupons);
        status=findViewById(R.id.tvStatus);
        progressBar=findViewById(R.id.pb);
        imageView=findViewById(R.id.iv);
        textView=findViewById(R.id.tv);
        logout=findViewById(R.id.btnlogout);
        deluser=findViewById(R.id.btndeluser);
    }
    private void disableBtns(){
        Buy.setEnabled(false);
        Menu.setEnabled(false);
        logout.setEnabled(false);
        AboutUs.setEnabled(false);
        deluser.setEnabled(false);
        Transfer.setEnabled(false);
        ContactUs.setEnabled(false);
        MyCoupons.setEnabled(false);
        Suggestions.setEnabled(false);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void enable(){
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        Buy.setEnabled(true);
        Menu.setEnabled(true);
        logout.setEnabled(true);
        deluser.setEnabled(true);
        ContactUs.setEnabled(true);
        AboutUs.setEnabled(true);
        Suggestions.setEnabled(true);
        Transfer.setEnabled(true);
        MyCoupons.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(roll_no).child("Info").child("timestamp").setValue(ServerValue.TIMESTAMP);

    }
    private void retriveData(){
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Retrieving User Roll_No
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        roll_no=user.getDisplayName();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                degree = dataSnapshot.child(roll_no).child("Info").child("Degree").getValue().toString();
                deleteData();
                if(!dataSnapshot.child(roll_no).child("Info").exists()){
                    databaseReference.child(roll_no).child("Info").child("Degree").setValue(degree);
                    databaseReference.child(roll_no).child("Info").child("timestamp").setValue(ServerValue.TIMESTAMP);
                }
                status.setText("ONLINE");
                enable();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Logged_In.this,"Network Error:"+databaseError.getCode(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        });
    }
    private void deleteData(){

        //kal ka delete from user data
        c.add(Calendar.DATE,-1);
        Date Yesterday = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String DateYesterday = dateFormat.format(Yesterday);

        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child(roll_no).child(DateYesterday).removeValue();

        //parso ka delete from user data
        c.add(Calendar.DATE,-1);
        Date parso = c.getTime();
        String parsoo = dateFormat.format(parso);
        databaseReference.child(roll_no).child(parsoo).removeValue();

        Calendar calendar = Calendar.getInstance();
        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        Date Today = calendar.getTime();
        String dateToday = dateFormat.format(Today);

        //Deleting admin data
        databaseReference.child("Admin").child(DateYesterday).removeValue();

        if(hr >= 11)
            databaseReference.child(roll_no).child(dateToday).child("Breakfast").removeValue();
        if(hr >= 15)
            databaseReference.child(roll_no).child(dateToday).child("Lunch").removeValue();
        if(hr >= 19)
            databaseReference.child(roll_no).child(dateToday).child("HighTea").removeValue();
        if(hr >= 23)
            databaseReference.child(roll_no).child(dateToday).child("Dinner").removeValue();


        //setting limit to user data upto 3 days
        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS);

        //Deleteing user's coupon data
        DatabaseReference ttlRef = FirebaseDatabase.getInstance().getReference(roll_no);
        Query oldItems = ttlRef.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
    private void Logoutfn(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Logged_In.this,Login.class));
    }
    private void create(){
        Calendar calendar = Calendar.getInstance();

        Date TODAY = calendar.getTime();
        calendar.add(Calendar.DATE,1);
        Date TOMORROW = calendar.getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String DateToday = dateFormat.format(TODAY);
        final String DateTomorrow = dateFormat.format(TOMORROW);

        //Creating admin section coupon data to remove null pointer exception
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Admin").child("Alive").child("timestamp").setValue(ServerValue.TIMESTAMP);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Admin").child(DateToday).exists()){
                    databaseReference.child("Admin").child(DateToday).child("Breakfast").setValue(0);
                    databaseReference.child("Admin").child(DateToday).child("Lunch").setValue(0);
                    databaseReference.child("Admin").child(DateToday).child("HighTea").setValue(0);
                    databaseReference.child("Admin").child(DateToday).child("Dinner").setValue(0);
                    databaseReference.child("Admin").child(DateToday).child("ALL").setValue(0);
                    databaseReference.child("Admin").child(DateToday).child("timestamp").setValue(ServerValue.TIMESTAMP);
                }
                if(!dataSnapshot.child("Admin").child(DateTomorrow).exists()){
                    databaseReference.child("Admin").child(DateTomorrow).child("Breakfast").setValue(0);
                    databaseReference.child("Admin").child(DateTomorrow).child("Lunch").setValue(0);
                    databaseReference.child("Admin").child(DateTomorrow).child("HighTea").setValue(0);
                    databaseReference.child("Admin").child(DateTomorrow).child("Dinner").setValue(0);
                    databaseReference.child("Admin").child(DateTomorrow).child("ALL").setValue(0);
                    databaseReference.child("Admin").child(DateTomorrow).child("timestamp").setValue(ServerValue.TIMESTAMP);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        }
        else
            return false;
    }
    private AlertDialog.Builder builderDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Make sure you have an active internet connection to continue.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }





}


