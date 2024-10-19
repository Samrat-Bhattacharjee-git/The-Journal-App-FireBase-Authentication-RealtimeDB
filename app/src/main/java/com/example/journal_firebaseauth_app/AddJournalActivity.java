package com.example.journal_firebaseauth_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal_firebaseauth_app.model.Journal;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    //widgets
    ImageView img1,img2;
    TextView txt1,txt2;
    EditText edt1,edt2;
    ProgressBar progressBar;
    Button savePost;

    //User ID and Username
    String currentUserID;
    String currentUserName;

    //fireBase auth
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;

    //connection to firestore
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    //firebase storage
    StorageReference storageReference;

    CollectionReference collectionReference= db.collection("Journal");
    Uri imageuri;

    private static final int GALLERY_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
        img1=findViewById(R.id.imageView);
        img2=findViewById(R.id.postcamerabutton);
        txt1=findViewById(R.id.posttitle);
        txt2=findViewById(R.id.postdate);
        edt1=findViewById(R.id.posttitleet);
        edt2=findViewById(R.id.thoughts);
        progressBar=findViewById(R.id.progress);
        savePost=findViewById(R.id.savebtn);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);

        if(JournalUser.getInstance()!=null){
            currentUserID=JournalUser.getInstance().getUserID();
            currentUserName=JournalUser.getInstance().getUsername();
            txt2.setText(currentUserName);
        }
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if(user!=null){

                }
                else {

                }
            }
        };

        savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveJournal();
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting image from gallery
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

    }

    private void SaveJournal() {
        String title=edt1.getText().toString().trim();
        String thoughts=edt2.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageuri!=null ){
            //the saving path of the images in the storage
            //.../journal_image/our_image.png
            final StorageReference filepath=storageReference.child("Journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());

            //uploading the image
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();

                            Journal journal=new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thoughts);
                            journal.setImageUrl(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUsername(currentUserName);
                            journal.setUserID(currentUserID);

                            //invoking collection reference

                            collectionReference.add(journal)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(AddJournalActivity.this,JournalListActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddJournalActivity.this, "Failed:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            if(data!=null){
                imageuri=data.getData(); //getting the actual image path
                img2.setImageURI(imageuri); //showing the image
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}