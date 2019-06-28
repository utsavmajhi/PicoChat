package com.example.trail2;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.pm.PackageInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class profileactivity extends AppCompatActivity {



    private ImageView mprofileimage;
    private TextView mprofilename,mprofilestatus,mprofilefriendscount;
    private Button mprofilereqbutton, mdecbtn,unfrndbtn;
    private DatabaseReference musersdatabase;

    private FirebaseUser mcurrentuser;

    private ProgressDialog mprogressdialog;

    private String mcurrent_state;

    private DatabaseReference mnotificationdatabse;
    private DatabaseReference mfriendreqdatabase;
    private DatabaseReference  mfriendDatabase;
    private DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        final String user_id=getIntent().getStringExtra("user_id");


        musersdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mprofilename=(TextView)findViewById(R.id.profile_displayName);
        mprofilefriendscount=(TextView)findViewById(R.id.profile_totfriends);
        mprofileimage=(ImageView)findViewById(R.id.profile_image);
        mprofilestatus=(TextView)findViewById(R.id.profile_status);
        mprofilereqbutton=(Button)findViewById(R.id.profile_reqbtn);
         mdecbtn=(Button)findViewById(R.id.profiledec_btn) ;
         unfrndbtn=(Button)findViewById(R.id.profileunreqbtn);
        //friends databse
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mnotificationdatabse=FirebaseDatabase.getInstance().getReference().child("notification");
        mfriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        mfriendreqdatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mcurrent_state="not_friends";

        mdecbtn.setVisibility(View.INVISIBLE);
        mdecbtn.setEnabled(false);
        unfrndbtn.setEnabled(false);
        unfrndbtn.setVisibility(View.INVISIBLE);

         mprogressdialog=new ProgressDialog(this);
         mprogressdialog.setTitle("Loading User Data");
         mprogressdialog.setMessage("Please wait while we load the user data");
         mprogressdialog.setCanceledOnTouchOutside(false);
         mprogressdialog.show();


         //ADDED FOR SYNCING CONTINUOUSLY
        musersdatabase.keepSynced(true);
        mRootRef.keepSynced(true);
        mfriendreqdatabase.keepSynced(true);

        musersdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                mprofilename.setText(display_name);
                mprofilestatus.setText(status);

                Picasso.with(profileactivity.this).load(image).placeholder(R.drawable.pro).into(mprofileimage);

                //-------------Friends list /request feature----------

                mfriendreqdatabase.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id))
                        {
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received"))
                            {

                                mcurrent_state="req_received";
                                mprofilereqbutton.setText("Accept Friend Request");

                                mdecbtn.setVisibility(View.VISIBLE);
                                mdecbtn.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                            {
                                mcurrent_state="req_sent";
                                mprofilereqbutton.setText("Cancel Friend Request");
                                mdecbtn.setVisibility(View.INVISIBLE);
                                mdecbtn.setEnabled(false);

                            }
                            mprogressdialog.dismiss();
                        }
                        else {

                            mfriendDatabase.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        mcurrent_state="Friends";
                                      //  mprofilereqbutton.setText("Unfriend this Person");
                                          mprofilereqbutton.setEnabled(false);
                                          mprofilereqbutton.setVisibility(View.INVISIBLE);
                                        mdecbtn.setVisibility(View.INVISIBLE);
                                        mdecbtn.setEnabled(false);
                                        unfrndbtn.setEnabled(true);
                                        unfrndbtn.setVisibility(View.VISIBLE);

                                    }
                                    mprogressdialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                 {
                                    mprogressdialog.dismiss();
                                }
                            });
                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onclicksendreqbtn(View view) {


        mprofilereqbutton.setEnabled(false);


        //-------------Not friends state------------

        final String user_id=getIntent().getStringExtra("user_id");


        //-----------Unfriend ---------
        if(mcurrent_state.equals("friends"))
        {
           /* mRootRef.child("Friends").child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mRootRef.child("Friends").child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mprofilereqbutton.setEnabled(true);
                            mcurrent_state="not_friends";
                            mprofilereqbutton.setText("Send Friend Request");

                            mdecbtn.setVisibility(View.INVISIBLE);
                            mdecbtn.setEnabled(false);


                        }
                    });

                }
            });*/
            Map unfriendMap=new HashMap();
            unfriendMap.put("Friends/"+mcurrentuser.getUid()+"/"+user_id,null);
            unfriendMap.put("Friends/"+user_id+"/"+mcurrentuser.getUid(),null);
            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError==null)
                    {

                        mRootRef.child("Friendreqreceived").child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mcurrent_state="not_friends";
                                mprofilereqbutton.setText("Send Friend Request");

                                mdecbtn.setVisibility(View.INVISIBLE);
                                mdecbtn.setEnabled(false);
                            }
                        });

                    }else
                    {
                        String error=databaseError.getMessage();
                        Toast.makeText(profileactivity.this,error,Toast.LENGTH_LONG).show();
                    }
                    mprofilereqbutton.setEnabled(true);


                }
            });
            mprofilereqbutton.setEnabled(true);
        }

        if(mcurrent_state.equals("not_friends"))
        {

            DatabaseReference newNotificationref=mRootRef.child("notification").child(user_id).push();
            String newNotificationId=newNotificationref.getKey();

            HashMap<String,String> notificationdata=new HashMap<>();
            notificationdata.put("from",mcurrentuser.getUid());
            notificationdata.put("type","request");

          Map requestMap=new HashMap();
          requestMap.put("Friend_req/"+mcurrentuser.getUid()+"/"+user_id+"/request_type","sent");
          requestMap.put("Friend_req/"+user_id +"/"+mcurrentuser.getUid()+"/request_type","received");
          requestMap.put("Friendreqreceived/"+user_id+"/"+mcurrentuser.getUid()+"/request_type","received");
          requestMap.put("notification/" + user_id+"/"+newNotificationId,notificationdata);



          mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
              @Override
              public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                  if(databaseError!=null)
                  {
                      Toast.makeText(profileactivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                  }

                  mprofilereqbutton.setEnabled(true);
                  mcurrent_state="req_sent";
                  mprofilereqbutton.setText("Cancel Friend Request");
              }
          });
        }
        //---------------cancel request state-----------
        if(mcurrent_state.equals("req_sent"))
        {
            mfriendreqdatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mfriendreqdatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRootRef.child("Friendreqreceived").child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mprofilereqbutton.setEnabled(true);
                                    mcurrent_state="not_friends";
                                    mprofilereqbutton.setText("Send Friend Request");

                                    mdecbtn.setVisibility(View.INVISIBLE);
                                    mdecbtn.setEnabled(false);

                                }
                            });


                        }
                    });

                }
            });
        }
         //-----------req received state-------
        if(mcurrent_state.equals("req_received"))
        {
            final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
          Map friendsMap=new HashMap();
          friendsMap.put("Friends/"+ mcurrentuser.getUid()+"/"+user_id+"/date",currentDate);
          friendsMap.put("Friends/"+user_id+"/"+mcurrentuser.getUid()+"/date",currentDate);

          friendsMap.put("Friend_req/"+mcurrentuser.getUid()+"/"+user_id,null);
          friendsMap.put("Friend_req/"+user_id+"/"+mcurrentuser.getUid(),null);

          mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
              @Override
              public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                  if(databaseError==null)
                  {
                      mRootRef.child("Friendreqreceived").child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              mprofilereqbutton.setEnabled(false);
                              mcurrent_state="friends";
                              //mprofilereqbutton.setText("Unfriend this Person");
                              mprofilereqbutton.setVisibility(View.INVISIBLE);
                              unfrndbtn.setVisibility(View.VISIBLE);
                              unfrndbtn.setEnabled(true);
                              mdecbtn.setVisibility(View.INVISIBLE);
                              mdecbtn.setEnabled(false);
                          }
                      });

                  }else
                  {
                      String error=databaseError.getMessage();
                      Toast.makeText(profileactivity.this,error,Toast.LENGTH_LONG).show();
                  }


              }
          });

        }






    }

    public void declinebtn(View view) {
        final String user_id=getIntent().getStringExtra("user_id");
        mfriendreqdatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mfriendreqdatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mRootRef.child("Friendreqreceived").child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mprofilereqbutton.setEnabled(true);
                                mcurrent_state="not_friends";
                                mprofilereqbutton.setText("Send Friend Request");


                                mdecbtn.setVisibility(View.INVISIBLE);
                                mdecbtn.setEnabled(false);
                            }
                        });



                    }
                });

            }
        });

    }

    public void unfriendclick(View view) {
        final String user_id=getIntent().getStringExtra("user_id");

           /* mRootRef.child("Friends").child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mRootRef.child("Friends").child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mprofilereqbutton.setEnabled(true);
                            mcurrent_state="not_friends";
                            mprofilereqbutton.setText("Send Friend Request");

                            mdecbtn.setVisibility(View.INVISIBLE);
                            mdecbtn.setEnabled(false);


                        }
                    });

                }
            });*/
        Map unfriendMap=new HashMap();
        unfriendMap.put("Friends/"+mcurrentuser.getUid()+"/"+user_id,null);
        unfriendMap.put("Friends/"+user_id+"/"+mcurrentuser.getUid(),null);
        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError==null)
                {

                    mcurrent_state="not_friends";
                    mprofilereqbutton.setText("Send Friend Request");

                    mdecbtn.setVisibility(View.INVISIBLE);
                    mdecbtn.setEnabled(false);
                }else
                {
                    String error=databaseError.getMessage();
                    Toast.makeText(profileactivity.this,error,Toast.LENGTH_LONG).show();
                }
                mprofilereqbutton.setEnabled(true);
                mprofilereqbutton.setText("Send Friend Request");
                unfrndbtn.setEnabled(false);
                unfrndbtn.setVisibility(View.INVISIBLE);


            }
        });
        mprofilereqbutton.setEnabled(true);
        mprofilereqbutton.setVisibility(View.VISIBLE);
        mprofilereqbutton.setText("Send Friend Request");
        unfrndbtn.setEnabled(false);
        unfrndbtn.setVisibility(View.INVISIBLE);
    }
}
