package com.example.trail2;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class namechange extends AppCompatActivity {
    private Toolbar mtoolbar;
    private FirebaseUser mcurrentuser;

    private TextInputLayout mnameedit;

    private DatabaseReference mstatusdatabase;
    private ProgressDialog mprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namechange);
        mtoolbar=(Toolbar)findViewById(R.id.statussappbar2);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mnameedit=(TextInputLayout)findViewById(R.id.nameediting);

        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mcurrentuser.getUid();
        mstatusdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

    }

    public void onclicksave2(View view) {
        mprogress=new ProgressDialog(namechange.this);
        mprogress.setTitle("Saving Changes");
        mprogress.setMessage("Please Wait!");
        mprogress.show();
        String nameedited=mnameedit.getEditText().getText().toString();
        mstatusdatabase.child("name").setValue(nameedited).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful())
           {
               mprogress.dismiss();
               Toast.makeText(namechange.this,"Successfully Changed Your Username",Toast.LENGTH_SHORT).show();

           }
           else {
               Toast.makeText(namechange.this,"Error while saving changes.Please Retry after Sometime!",Toast.LENGTH_SHORT).show();
           }


            }
        });

    }
}
