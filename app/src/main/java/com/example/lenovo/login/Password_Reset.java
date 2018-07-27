
package com.example.lenovo.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Password_Reset extends AppCompatActivity {

    private EditText Email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        Button resPass = findViewById(R.id.btnResetPass);
        Email = findViewById(R.id.etPassEmail);

        firebaseAuth= FirebaseAuth.getInstance();

        resPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = Email.getText().toString().trim();
                if(useremail.equals("")){
                    Toast.makeText(Password_Reset.this,"Please enter your registered email id.",Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Password_Reset.this,"Password reset email has been sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(Password_Reset.this,Login.class));
                            }
                            else{
                                Toast.makeText(Password_Reset.this,"Error in sending reset email.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}
