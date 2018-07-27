package com.example.lenovo.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Change_Pass_Admin extends AppCompatActivity {

    private EditText opass,npass,cpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_admin);


        opass=findViewById(R.id.etOldpass);
        npass=findViewById(R.id.etNewpass);
        cpass=findViewById(R.id.etConfpass);
        Button update = findViewById(R.id.btnUpdate);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                final String old=opass.getText().toString();
                final String NPASS=npass.getText().toString();
                final String CPASS=cpass.getText().toString();
                if(old.isEmpty() || NPASS.isEmpty() || CPASS.isEmpty()){
                    Toast.makeText(Change_Pass_Admin.this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
                }
                else databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String PASS=dataSnapshot.child("Password").getValue().toString();

                        if(!old.equals(PASS))
                            Toast.makeText(Change_Pass_Admin.this, "Old Password is incorrect.", Toast.LENGTH_SHORT).show();
                        else if(!NPASS.equals(CPASS)){
                            Toast.makeText(Change_Pass_Admin.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            databaseReference.child("Password").setValue(NPASS);
                            Toast.makeText(Change_Pass_Admin.this, "Update Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Change_Pass_Admin.this,Admin.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
