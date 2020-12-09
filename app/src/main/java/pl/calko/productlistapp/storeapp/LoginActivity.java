package pl.calko.productlistapp.storeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.calko.productlistapp.R;

public class LoginActivity extends AppCompatActivity {

    private final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUi();
    }

    private void setupUi() {
        final EditText emailEt = findViewById(R.id.email);
        final EditText passwordEt = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button newAccount = findViewById(R.id.newAccount);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                    return;
                login(email, password);
            }
        });
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                    return;
                addNewAccount(email, password);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, ProductListActivity.class));
            finish();
        }
    }

    private void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
                        } else {
                            Toast.makeText(getBaseContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNewAccount(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            users.child(user.getUid()).setValue((new User(user.getUid())));
                            startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
                        } else {
                            Toast.makeText(getBaseContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
