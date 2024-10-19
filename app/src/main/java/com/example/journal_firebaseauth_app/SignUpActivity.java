package com.example.journal_firebaseauth_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    Button signup_btn;
    EditText emailET,passwordET,usernameET;

    //firebase authentication
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    //firebase connection
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        emailET=findViewById(R.id.email_signup);
        passwordET=findViewById(R.id.password_signup);
        usernameET=findViewById(R.id.username_signup);
        signup_btn=findViewById(R.id.signup);

        //Authentication

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();

                if(firebaseUser==null){
                    //user already logged in
                }
                else {
                    //no user yet
                }
            }
        };

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(emailET.getText().toString()) &&
                        !TextUtils.isEmpty(passwordET.getText().toString()) &&
                        !TextUtils.isEmpty(usernameET.getText().toString())){
                    String email=emailET.getText().toString().trim();
                    String password=passwordET.getText().toString().trim();
                    String username=usernameET.getText().toString().trim();
                    CreateUserAccount(email,password,username);
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void CreateUserAccount(String email, String password, String username) {
        if(!TextUtils.isEmpty(emailET.getText().toString()) &&
                !TextUtils.isEmpty(passwordET.getText().toString())){

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //we take user to next activity:(AddJournalActivity)
                        firebaseUser=firebaseAuth.getCurrentUser();
                        assert firebaseUser!=null;
                        final String currentUserID=firebaseUser.getUid();

                        //create a user map so that we can create a user in the user collection if  firestore
                        Map<String,String> userobj=new HashMap<>();
                        userobj.put("userID",currentUserID);
                        userobj.put("username",username);

                        //adding user to firestore
                        collectionReference.add(userobj)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (Objects.requireNonNull(task.getResult()).exists()){

                                                    String name=task.getResult().getString("username");

                                                    //getting use of global journal activity

                                                    JournalUser journalUser= JournalUser.getInstance();
                                                    journalUser.setUserID(currentUserID);
                                                    journalUser.setUsername(name);
                                                    //if the user is registered successfully ,then move to the addJournalActivity

                                                    Intent intent=new Intent(SignUpActivity.this, AddJournalActivity.class);
                                                    intent.putExtra("username",name);
                                                    intent.putExtra("userID",currentUserID);
                                                    startActivity(intent);
                                                }
                                                else {

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //display failure message
                                                Toast.makeText(SignUpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}