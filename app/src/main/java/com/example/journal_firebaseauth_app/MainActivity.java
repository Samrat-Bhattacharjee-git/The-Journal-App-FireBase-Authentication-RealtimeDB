package com.example.journal_firebaseauth_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    //widgets
    Button login_btn;
    TextView createAccount_btn;
    EditText emailET,passwordET;

    //firebase authentication
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    //firebase connection
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    CollectionReference collectionReference=firebaseFirestore.collection("Users");

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createAccount_btn=findViewById(R.id.createaccount);
        login_btn=findViewById(R.id.login);
        emailET=findViewById(R.id.email);
        passwordET=findViewById(R.id.password);

        firebaseAuth=FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginWithEmailPassword(emailET.getText().toString().trim(),
                        passwordET.getText().toString().trim());
            }
        });

        createAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    private void LoginWithEmailPassword(String email, String password) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            assert user!=null;
                            final String currentUserID=user.getUid();

                            collectionReference.whereEqualTo("userID",currentUserID)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if(error!=null){

                                            }
                                            assert value!=null;
                                            if(!value.isEmpty()){
                                                for (QueryDocumentSnapshot snapshot:value){
                                                    JournalUser journalUser=JournalUser.getInstance();
                                                    journalUser.setUsername(snapshot.getString("username"));
                                                    journalUser.setUserID(snapshot.getString("userID"));

                                                    //go to journalListActivity after successful login
                                                    startActivity(new Intent(MainActivity.this, AddJournalActivity.class));
                                                }
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something Went Wrong: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show();
        }
    }
}