package com.example.trail2;


import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference muserref;



    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList=mMessageList;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_single_layout,viewGroup,false);

        return new MessageViewHolder(v);

    }




    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText=(TextView)itemView.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_layout);
            displayName=(TextView)itemView.findViewById(R.id.name_text_layout);
            messageImage=(ImageView)itemView.findViewById(R.id.message_image_layout);



        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        Messages c=mMessageList.get(i);
        String from_user=c.getFrom();
        String message_type=c.getType();


        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                messageViewHolder.displayName.setText(name);

                Picasso.with(messageViewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.pro).into(messageViewHolder.profileImage);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text"))
        {
            messageViewHolder.messageText.setText(c.getMessage());
            messageViewHolder.messageImage.setVisibility(View.INVISIBLE);
        }
        else
        {
            messageViewHolder.messageText.setVisibility(View.INVISIBLE);

            Picasso.with(messageViewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.pro).into(messageViewHolder.messageImage);
        }


        mAuth=FirebaseAuth.getInstance();
        String current_user_id=mAuth.getCurrentUser().getUid();


        if(from_user.equals(current_user_id))
        {

            messageViewHolder.messageText.setBackgroundResource(R.drawable.message_text_background2);
           messageViewHolder.messageText.setTextColor(Color.BLACK);


        }
        else
        {

            messageViewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            messageViewHolder.messageText.setTextColor(Color.WHITE);

        }
       // messageViewHolder.messageText.setText(c.getMessage());


    }


    @Override
    public int getItemCount() {

        return mMessageList.size();
    }

}
