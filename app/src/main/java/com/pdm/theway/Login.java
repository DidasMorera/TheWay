package com.pdm.theway;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
//import com.pdm.theway.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private EditText emails, senhas;
    private ProgressBar pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emails = findViewById(R.id.email);
        senhas = findViewById(R.id.senha);
        TextView criars = findViewById(R.id.criar);
        Button entrars = findViewById(R.id.entrar);
        pro = findViewById(R.id.progressBar);
        pro.setVisibility(View.INVISIBLE);

        criars.setOnClickListener(v -> {

            Intent intent = new Intent(Login.this, Cadastro.class);
            startActivity(intent);
        });

        entrars.setOnClickListener(v -> {
             String emaily = emails.getText().toString();
             String senhay = senhas.getText().toString();

             if(emaily.isEmpty() || senhay.isEmpty()){
                 Toast.makeText(Login.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
             }else{
                 autenticarUsuario();
             }
        });

    }

    public void autenticarUsuario(){

        String emaily = emails.getText().toString();
        String senhay = senhas.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emaily, senhay).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                pro.setVisibility(View.VISIBLE);
                //Toast.makeText(Login.this, "Sucesso ao logar", Toast.LENGTH_LONG).show();

                new Handler().postDelayed(this::TelaPrincipal, 3000);
            }else{
                String erro;
                try {
                    throw Objects.requireNonNull(task.getException());
                }catch (Exception e){
                    erro = "Erro ao logar usuario";
                }
                Toast.makeText(Login.this,erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioActual != null){
            TelaPrincipal();
        }
    }

    private void TelaPrincipal(){
        Intent intent = new Intent(Login.this, Nav.class);
        startActivity(intent);
        finish();
    }
}