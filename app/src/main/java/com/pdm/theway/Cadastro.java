package com.pdm.theway;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
//import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.pdm.theway.databinding.ActivityCadastroBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cadastro extends AppCompatActivity {

    Button cadastrars;
    EditText nomes, emails, senhas;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        cadastrars = findViewById(R.id.cadastrar);
        nomes = findViewById(R.id.nome);
        emails = findViewById(R.id.email);
        senhas = findViewById(R.id.senha);

        cadastrars.setOnClickListener(v -> {
            String nomey = nomes.getText().toString();
            String emaily = emails.getText().toString();
            String senhay = senhas.getText().toString();

            if(nomey.isEmpty() || emaily.isEmpty() ||
                    senhay.isEmpty() ){

                Toast.makeText(Cadastro.this,"Preencha todos os campos",Toast.LENGTH_LONG).show();

            }else {
                cadastrar();
            }

        });

    }

    private void cadastrar(){

        String emaily = emails.getText().toString();
        String senhay = senhas.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emaily, senhay).
                addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        SalvarDadosUsuario();

                        Toast.makeText(Cadastro.this, "Cadastro feito com sucesso", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Cadastro.this, Login.class);
                        startActivity(intent);
                        finish();



                    }else{
                        String erro;
                        try {
                            throw Objects.requireNonNull(task.getException());

                        }catch (FirebaseAuthWeakPasswordException e) {
                            erro = "Digite uma senha com no minimo 6 caracteres";

                        }catch (FirebaseAuthUserCollisionException e) {
                            erro = "Esta conta ja foi cadastrada";
                        }catch(FirebaseAuthInvalidCredentialsException e){
                            erro = "Email invalido";

                        }catch (Exception e){
                            erro = "Erro ao cadastrar usuario";
                        }
                        Toast.makeText(Cadastro.this, erro, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void SalvarDadosUsuario(){

        String nomey = nomes.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nomey);

        usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.set(usuarios).addOnSuccessListener(unused -> Log.d("db", "Sucesso ao salvar os dados"))
                .addOnFailureListener(e -> Log.d("db_error", "Erro ao salvar os dados" + e));
    }

}