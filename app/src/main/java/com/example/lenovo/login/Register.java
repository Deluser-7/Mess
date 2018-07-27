package com.example.lenovo.login;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String Degree;
    String roll_no;
    String numberOnly;
    private Button Signup;
    private TextView UserLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private EditText Email,Password,ConfirmPassword,RollNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setVar();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        //Spinner
        Spinner spinner = findViewById(R.id.spnCourse);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Course,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Signing Up...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                if (validate())
                {
                    databaseReference=FirebaseDatabase.getInstance().getReference();
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.child(roll_no).exists()){
                                //Upload data to database
                                String user_email = Email.getText().toString().trim();
                                String user_password = Password.getText().toString().trim();
                                firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            sendEmailVer();
                                            progressDialog.dismiss();
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Register.this, "Registration Failed or Already Registered", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(Register.this, "Already registered with these credentials.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                    progressDialog.dismiss();
            }
        });
        UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this , Login.class));
            }
        });
        }

    private void setVar(){
        Email = findViewById(R.id.etUserEmail);
        Password = findViewById(R.id.etUserPassword);
        ConfirmPassword= findViewById(R.id.etUserConfirmPassword);
        Signup= findViewById(R.id.btnSignUp);
        UserLogin= findViewById(R.id.tvUserLogin);
        RollNo= findViewById(R.id.etRollNo_);
    }
    private boolean validate(){
        Boolean result = false;
        String email = Email.getText().toString();

        if(!email.contains("@")){
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            String domain = "iiti.ac.in";
            roll_no = RollNo.getText().toString();
            String pass = Password.getText().toString();
            String cp = ConfirmPassword.getText().toString();

            String parts[] = email.split("@");
            //getting roll from email
            numberOnly = parts[0].replaceAll("[^0-9]", "");

            if(email.isEmpty() || pass.isEmpty() || cp.isEmpty() || roll_no.isEmpty()){
                Toast.makeText(this, "Please enter all details.",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            else if(pass.length()<=5){
                Toast.makeText(this, "Password should be at least six characters.",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            else if (!pass.equals(cp))
            {
                Toast.makeText(this, "Passwords do not match.",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            else if(!parts[1].equals(domain)){
                Toast.makeText(this, "Please enter IIT-I email id.", Toast.LENGTH_SHORT).show();
            }
            else if(!roll_no.equals(numberOnly)){
                Toast.makeText(this, "Please enter a valid roll.", Toast.LENGTH_SHORT).show();
            }
            else{
                result = true;
            }
            return result;
        }
    }
    private void sendEmailVer(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //check for existing user
        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        sendUserData();
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        //setting display name as roll no. to get it directly from firebase
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(roll_no).build();
                        user.updateProfile(profileUpdates);

                        Toast.makeText(Register.this, "Successfully Registered, Verification mail has been sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Register.this, Login.class));
                    }
                    else
                        Toast.makeText(Register.this, "Verification mail has not been sent.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child(numberOnly).child("Info").child("Degree").setValue(Degree);
        myRef.child(numberOnly).child("Info").child("timestamp").setValue(ServerValue.TIMESTAMP);
        //timestamp for cleaning database
    }

    //spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        Degree = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
