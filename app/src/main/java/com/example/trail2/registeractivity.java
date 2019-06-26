package com.example.trail2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class registeractivity extends AppCompatActivity {
    private EditText name;
    private EditText pass;
    private EditText email;
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private DatabaseReference muserdatabase;
    private ProgressDialog mprogressdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity2);
        name=(EditText) findViewById(R.id.etname);
        email=(EditText) findViewById(R.id.regemail);
        pass=(EditText) findViewById(R.id.regpass);
        register=(Button)findViewById(R.id.regbu);
        mAuth = FirebaseAuth.getInstance();
        muserdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void onregister(View view) {
         String n1=name.getText().toString();
         String p1=pass.getText().toString();
         String em1=email.getText().toString();

         if(n1.isEmpty()||p1.isEmpty()||em1.isEmpty())
         {
             Toast.makeText(registeractivity.this,"Please fill all the credentials",Toast.LENGTH_SHORT).show();
         }else {

             registration(n1, em1, p1);
         }

    }
    public void registration(final String n1,String email,String password) {
        mprogressdialog=new ProgressDialog(registeractivity.this);
        mprogressdialog.setTitle("Registering");
        mprogressdialog.setMessage("Please Wait! while you are being registered");
        mprogressdialog.setCanceledOnTouchOutside(false);
        mprogressdialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //database functions
                   FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=currentUser.getUid();
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    mdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> usermap=new HashMap<>();
                    usermap.put("name",n1);
                    usermap.put("status","Hi there!I am using Kamehameha");
                    usermap.put("image","default");
                    usermap.put("thumbimage","default");
                    mdatabase.setValue(usermap);
                    muserdatabase.child(uid).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(registeractivity.this,"Registration Succesful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(registeractivity.this,MainActivity.class));
                        }
                    });



                }
                else
                {
                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    Toast.makeText(registeractivity.this,"Registration not successful reason:"+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
