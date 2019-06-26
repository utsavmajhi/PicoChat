package com.example.trail2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class loginactivity extends AppCompatActivity {
    private EditText pass;
    private EditText email;
    private ImageView login;
    private FirebaseAuth mAuth;
    private DatabaseReference muserdatabase;
    private ProgressDialog mprogressdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        email=(EditText) findViewById(R.id.lgemail);
        pass=(EditText) findViewById(R.id.lgpass);
        login=(ImageView) findViewById(R.id.lgbutton);
        mAuth = FirebaseAuth.getInstance();
        muserdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void onlg(View view) {
        String em2=email.getText().toString();
        String p2=pass.getText().toString();

        if(em2.isEmpty()||p2.isEmpty())
        {
            Toast.makeText(loginactivity.this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();

        }else {
            mprogressdialog=new ProgressDialog(loginactivity.this);
            mprogressdialog.setTitle("Authentication");
            mprogressdialog.setMessage("Please Wait while we verify the credentials");
            mprogressdialog.setCanceledOnTouchOutside(false);
            mprogressdialog.show();
            mAuth.signInWithEmailAndPassword(em2, p2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mprogressdialog.dismiss();
                        String current_user_id = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        muserdatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(loginactivity.this, "Succesfully Logged in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(loginactivity.this, MainActivity.class));
                                finish();
                            }
                        });


                    } else {
                        mprogressdialog.dismiss();
                        Toast.makeText(loginactivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}
