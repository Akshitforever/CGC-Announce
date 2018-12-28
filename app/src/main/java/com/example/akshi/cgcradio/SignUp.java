package com.example.akshi.cgcradio;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText inputEmail, inputPassword,cpassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        Button btnSignIn = (Button) findViewById(R.id.sign_in_button);
        Button btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        progressBar.setVisibility(View.GONE);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String conpassword=cpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!conpassword.equals(password))
                {
                    Toast.makeText(getApplicationContext(), "Password Do not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                        Toast.makeText(SignUp.this,"E-mail already registered",Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(SignUp.this, task.getException().getMessage() + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    sendEmail();
                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                                    reference=reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Is_First_Time");
                                    reference.setValue("Yes");
                                    reference.getParent().child("DOB").setValue("22/01/1999");
                                    reference.getParent().child("Name").setValue("New User");
                                    reference.getParent().child("Number").setValue("xxxxxxxxxx");

                                    //TODO reference.getParent().child("image").setValue("https://www.google.com");
                                    Toast.makeText(SignUp.this,"Verification Link sent",Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    finish();
                                }
                            }
                        });


            }
        });
    }
    private void sendEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUp.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
//                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
