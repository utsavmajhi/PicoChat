package com.example.trail2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class refragments extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private DatabaseReference muserref;
    public refragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_refragments, container, false);
        mFriendsList = mMainView.findViewById(R.id.requests_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }
    @Override
    public void onStart() {
        super.onStart();
        //
        FirebaseRecyclerOptions<Friends> options =

                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery( mFriendsDatabase, Friends.class)
                        .build();

        FirebaseRecyclerAdapter friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {


            @Override

            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull Friends friends) {
                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, getContext());
                        // When Click to Friends View
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(getContext(), profileactivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);

                              /*  CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(), profileactivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }

                                        if(i == 1){
                                            //remember going to chat actvity

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);


                                        }

                                    }
                                });

                                builder.show();
                                */

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single, parent, false);
                return new FriendsViewHolder(view);
            }


        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
        friendsRecyclerViewAdapter.startListening();

    }


    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText("You have got a Friend Request");

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.message_profile_layout);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.pro).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView =mView.findViewById(R.id.greenon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.INVISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }


}