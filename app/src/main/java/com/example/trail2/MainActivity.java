package com.example.trail2;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.core.ServerValues;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mviewpager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout  mtablayout;
    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("PicoChat");

        //tabs
        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }
        else
        {
            startActivity(new Intent(MainActivity.this,startingactivity.class));
            finish();

        }

        mviewpager=(ViewPager) findViewById(R.id.maintabpager);
        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mSectionsPagerAdapter);
        mtablayout=(TabLayout) findViewById(R.id.maintabs);
        mtablayout.setupWithViewPager(mviewpager);




    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {

            startActivity(new Intent(MainActivity.this,startingactivity.class));

        }
        else
        {
            mUserRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null)
        {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
        // mUserRef.child("online").setValue(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.mainlogout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,startingactivity.class));
        }
        if(item.getItemId()==R.id.mainsetting)
        {
            startActivity(new Intent(MainActivity.this,settingsactivity.class));
        }
        if(item.getItemId()==R.id.mainall)
        {
            startActivity(new Intent(MainActivity.this,usersactivity.class));

        }
        return true;
    }
}
