package com.example.trail2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.googlecode.mp4parser.authoring.Edit;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private DatabaseReference mRootRef;
    private Toolbar mChatToolbar;
    private String mChatUser;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private  CircleImageView mProfileImage;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference muserref;
    private DatabaseReference prophotouserid;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD=10;
    private int mCurrentPage=1;

    //new Solution
    private int itemPos=0;
    private String mLastKey="";
    private String mPrevKey="";

    //images sending part
    private static final int GALLERY_PIC=1;
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar=(Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mImageStorage= FirebaseStorage.getInstance().getReference();
        mCurrentUserId=mAuth.getCurrentUser().getUid();


        mChatUser=getIntent().getStringExtra("user_id");
        String userName=getIntent().getStringExtra("user_name");
        prophotouserid=FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater=(LayoutInflater)ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);

         actionBar.setCustomView(action_bar_view);

         //-------custom action bar items-----

        mTitleView=(TextView)findViewById(R.id.custom_bar_title);
        mLastSeenView=(TextView)findViewById(R.id.custom_bar_seen);
        mProfileImage=(CircleImageView)findViewById(R.id.custom_bar_image);

        mChatAddBtn=(ImageButton)findViewById(R.id.chat_add_btn);
        mChatSendBtn=(ImageButton)findViewById(R.id.chat_send_btn);
        mChatMessageView=(EditText)findViewById(R.id.chat_messgae_view);


        mAdapter=new MessageAdapter(messagesList);
        mMessagesList=(RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mLinearLayout=new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        loadMessages();
        mTitleView.setText(userName);
        prophotouserid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image=dataSnapshot.child("image").getValue().toString();
                if(image.equals("default"))
                {

                }else{
                Picasso.with(mProfileImage.getContext()).load(image)
                        .placeholder(R.drawable.pro).into(mProfileImage);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


       mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online=dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                if(online.equals("true"))
                {
                    mLastSeenView.setText("Online");
                }
                else
                {
                   GetTimeAgo getTimeAgo=new GetTimeAgo();

                   long lastTime=Long.parseLong(online);

                   String lastSeenTime=GetTimeAgo.getTimeAgo(lastTime);
                    mLastSeenView.setText(lastSeenTime);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(!dataSnapshot.hasChild(mChatUser)) {

                   Map chatAddMap=new HashMap();
                   chatAddMap.put("seen",false);
                   chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                   Map chatUserMap=new HashMap();
                   chatUserMap.put("Chat/"+mCurrentUserId+"/"+mChatUser,chatAddMap);
                   chatUserMap.put("Chat/"+mChatUser+"/"+mCurrentUserId,chatAddMap);

                   mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                           if(databaseError!=null)
                           {
                               Log.d("CHAT_LOG",databaseError.getMessage().toString());
                           }

                       }
                   });

               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

       mChatSendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendMessage();
           }
       });
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PIC);

              //  onActivityResult();

            }
        });





       mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               mCurrentPage++;

               itemPos=0;

               loadMoreMessages();
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PIC && resultCode==RESULT_OK)
        {
           // Uri imageUri=data.getData();
            final String current_user_ref="messages/"+mCurrentUserId+"/"+mChatUser;
            final String chat_user_ref="messages/"+mChatUser+"/"+mCurrentUserId;
            DatabaseReference user_message_push=mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();
            final String push_id=user_message_push.getKey();
            Toast.makeText(this, "succees", Toast.LENGTH_SHORT).show();
            Uri imageUri=data.getData();
            final StorageReference filepath=mImageStorage.child("messages_images").child(push_id+".jpg");

           filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String download_url=uri.toString();
                                Toast.makeText(ChatActivity.this, download_url, Toast.LENGTH_SHORT).show();
                                Map messageMap=new HashMap();
                                messageMap.put("message",download_url);
                                messageMap.put("seen",false);
                                messageMap.put("type","image");
                                messageMap.put("time",ServerValue.TIMESTAMP);
                                messageMap.put("from",mCurrentUserId);

                                Map messageUserMap=new HashMap();
                                messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                                messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                                mChatMessageView.setText("");
                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if(databaseError!=null)
                                        {
                                            Toast.makeText(ChatActivity.this, "done evrything", Toast.LENGTH_SHORT).show();
                                            Log.d("CHATLOG",databaseError.getMessage().toString());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

            // start cropping activity for pre-acquired image saved on the device

        }

    }

    private void loadMoreMessages()
    {

        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery=messageRef.orderByKey().endAt(mLastKey).limitToLast(10);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                String messageKey=dataSnapshot.getKey();
                messagesList.add(itemPos++,message);


                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++,message);

                }else
                {
                    mPrevKey=mLastKey;
                }


                if(itemPos==1)
                {

                    mLastKey=messageKey;
                    mPrevKey=messageKey;
                }


                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void loadMessages() {


        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery=messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message=dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos==1)
                {
                    String messageKey=dataSnapshot.getKey();
                    mLastKey=messageKey;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);
                mRefreshLayout.setRefreshing(false);
               // mLinearLayout.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage()
    {
        String message=mChatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message))
        {
            String current_user_ref="messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref="messages/"+mChatUser+"/"+mCurrentUserId;

            DatabaseReference user_message_push=mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id=user_message_push.getKey();

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserId);

            Map messageUserMap=new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            mChatMessageView.setText("");
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError!=null)
                    {

                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }

                }
            });


        }
    }




}
