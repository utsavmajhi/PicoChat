package com.example.trail2;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class statusactivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private TextInputLayout mstatus;
    private Button msavebtn;
//progress dialog
    private ProgressDialog mprogress;
    //firbase
     private FirebaseUser mcurrentuser;
    private DatabaseReference mstatusdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusactivity);


        mtoolbar=(Toolbar)findViewById(R.id.statussappbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mcurrentuser.getUid();
        mstatusdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        String status_value=getIntent().getStringExtra("status_value");

        mstatus=(TextInputLayout)findViewById(R.id.status_input);
        msavebtn=(Button)findViewById(R.id.statussavebtn);

         mstatus.getEditText().setText(status_value);

    }

    public void onclicksave(View view) {
        mprogress=new ProgressDialog(statusactivity.this);
        mprogress.setTitle("Saving Changes");
        mprogress.setMessage("Please Wait!");
        mprogress.show();
        String status=mstatus.getEditText().getText().toString();
        mstatusdatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mprogress.dismiss();
                }
                else
                {
                    Toast.makeText(statusactivity.this,"Error while saving changes",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
