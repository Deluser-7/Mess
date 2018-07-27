package com.example.lenovo.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class Login extends AppCompatActivity {

    private Button Login;
    private String adminPass;
    private ImageButton btnAdmin;
    private EditText Email,Password;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView Signup,forgotpassword;

    LinearLayout linlay1;
    //For splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //time to appear all content


        setvar();
        getPass();  //getting admin section password
        create();   //Creating admin data for today & tomorrow

        progressDialog=new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user != null)
        {
            finish();
            startActivity(new Intent(com.example.lenovo.login.Login.this,Logged_In.class));
        }

        Login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(Email.getText().toString().isEmpty() || Password.getText().toString().isEmpty()){
                    Toast.makeText(com.example.lenovo.login.Login.this, "Please enter all details.", Toast.LENGTH_SHORT).show();
                }
                else
                    validate(Email.getText().toString().trim(),Password.getText().toString());
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.example.lenovo.login.Login.this, Register.class));
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.example.lenovo.login.Login.this,Password_Reset.class));
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alert Dialog for admin
                AlertDialog.Builder alert = new AlertDialog.Builder(com.example.lenovo.login.Login.this);
                final EditText editText = new EditText(com.example.lenovo.login.Login.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setTitle("Admin SECTION");
                alert.setMessage("Please enter your password");
                alert.setView(editText);
                editText.setHint("Password");
                alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText.getText().toString();
                        if(password.equals(adminPass)){
                            startActivity(new Intent(com.example.lenovo.login.Login.this,Admin.class));
                        }
                        else{
                            Toast.makeText(com.example.lenovo.login.Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("Cancel",null);
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

    }

    private void setvar(){
        Email = findViewById(R.id.etEmail);
        Login = findViewById(R.id.btnLogin);
        linlay1 = findViewById(R.id.linlay1);
        Signup = findViewById(R.id.tvNewUser);
        btnAdmin = findViewById(R.id.imgADMIN);
        Password = findViewById(R.id.etPassword);
        forgotpassword = findViewById(R.id.tvForgotPassword);
    }
    private void validate(String email, String Password){
        progressDialog.setMessage("Hey, You are looking AWESOME !!");
        progressDialog.show();
        progressDialog.setCancelable(false);
        firebaseAuth.signInWithEmailAndPassword(email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task)
          {
              if(task.isSuccessful()){
                  progressDialog.dismiss();
                  checkEmailVer();
              }
              else{
                  progressDialog.dismiss();
                  Toast.makeText(com.example.lenovo.login.Login.this, "Invalid Credentials !", Toast.LENGTH_SHORT).show();
              }
          }
      });
    }
    private void checkEmailVer(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){
            Toast.makeText(com.example.lenovo.login.Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(com.example.lenovo.login.Login.this, Logged_In.class));
        }
        else{
            Toast.makeText(com.example.lenovo.login.Login.this,"Verify your Email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void getPass(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Password");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminPass=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void create(){
        Calendar calendar = Calendar.getInstance();

        Date TODAY = calendar.getTime();
        calendar.add(Calendar.DATE,1);
        Date TOMORROW = calendar.getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String DateToday = dateFormat.format(TODAY);
        final String DateTomorrow = dateFormat.format(TOMORROW);

        final DatabaseReference databaseReference;
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
}
