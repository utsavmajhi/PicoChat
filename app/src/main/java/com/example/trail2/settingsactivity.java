package com.example.trail2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class settingsactivity extends AppCompatActivity {
     private DatabaseReference muserdatabse;
     private FirebaseUser mcurrentuser;

     //progress dialog
     private ProgressDialog mprogressdialog;

     //android layout

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mstatus;

    private  static final int GALLERY_PIC=1;

    //firebase storage
    private StorageReference mImageStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingsactivity);

        mDisplayImage=(CircleImageView)findViewById(R.id.settings_image);
        mName=(TextView)findViewById(R.id.settings_display_name);
        mstatus=(TextView)findViewById(R.id.settings_status);

        mImageStorage= FirebaseStorage.getInstance().getReference();

        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mcurrentuser.getUid();

        muserdatabse= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        muserdatabse.keepSynced(true);


        muserdatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String name=dataSnapshot.child("name").getValue().toString();
               final String image=dataSnapshot.child("image").getValue().toString();
               String status=dataSnapshot.child("status").getValue().toString();
               String thumb_image=dataSnapshot.child("thumbimage").getValue().toString();

                mName.setText(name);
                mstatus.setText(status);

                if(!image.equals("default"))
                {
                    Picasso.with(settingsactivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.pro).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(settingsactivity.this).load(image).placeholder(R.drawable.pro).into(mDisplayImage);
                        }
                    });
                    //online capabilities(normal way)
                    //Picasso.with(settingsactivity.this).load(image).placeholder(R.drawable.pro).into(mDisplayImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onclickstatus(View view) {

        String status_value=mstatus.getText().toString();

         Intent status_intent=new Intent(settingsactivity.this,statusactivity.class);
         status_intent.putExtra("status_value",status_value);
        startActivity(status_intent);
    }
//remember u are have compiled to work on api level 22 minimum
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void onclickimagebtn(View view) {

        Intent galleryIntent=new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PIC);

        // start picker to get image for cropping and then use the image in cropping activity
        /*  CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(settingsactivity.this);
                */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PIC && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

            //Toast.makeText(settingsactivity.this,imageUrl,Toast.LENGTH_LONG).show();
        }
        //getting cropped activity image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mprogressdialog=new ProgressDialog(settingsactivity.this);
                mprogressdialog.setTitle("Uploading Image");
                mprogressdialog.setMessage("Please Wait while we upload and process the image");
                mprogressdialog.setCanceledOnTouchOutside(false);
                mprogressdialog.show();


                Uri resultUri = result.getUri();
                String current_user_id=mcurrentuser.getUid();

                //OPTIMIZATION FOR THUMBNAIL IMAGES
              /*  File thumb_filepath=new File(resultUri.getPath());



                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumb_byte = baos.toByteArray();*/

                 final StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");


                 filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful())
                         {
                             filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     String download_url=uri.toString();
                                     muserdatabse.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {
                                                 mprogressdialog.dismiss();
                                                 Toast.makeText(settingsactivity.this,"Success uploading",Toast.LENGTH_LONG).show();
                                             }
                                         }
                                     });

                                 }
                             });






                         }
                         else
                         {
                             Toast.makeText(settingsactivity.this,"not working",Toast.LENGTH_LONG).show();
                             mprogressdialog.dismiss();
                         }
                     }
                 });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
