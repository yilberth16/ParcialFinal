package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button btnIngresar, btnRegistrar;

    RelativeLayout rootLayout;

     FirebaseAuth auth;

    MaterialEditText edtNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIngresar = (Button) findViewById(R.id.btnIniciar_sesion);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        auth = FirebaseAuth.getInstance();
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
    }

    private void showRegisterDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTRATE");
        dialog.setMessage("Por favor ingresa tu numero para registrarte");


        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_registrar, null);


        final MaterialEditText edtContraseña = register_layout.findViewById(R.id.edtPassword);
         edtNombre = register_layout.findViewById(R.id.edtNombre);
        final MaterialEditText edtTelefono = register_layout.findViewById(R.id.edtPhone);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);




        dialog.setView(register_layout);
        //SET Button

        dialog.setPositiveButton("Registrate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


                //Check validation
                if (TextUtils.isEmpty(edtContraseña.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor ingrese una contraseña", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }

                if (edtContraseña.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Por favor ingrese la direccion de correo electronico", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }

                if (TextUtils.isEmpty(edtTelefono.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor ingrese la direccion de correo electronico", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor ingrese la direccion de correo electronico", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }

                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtContraseña.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("usuarios").push();
                                    reference.child("Nombre").setValue(edtNombre.getText().toString());
                                    reference.child("Celular").setValue(edtTelefono.getText().toString());
                                    reference.child("Correo").setValue(edtEmail.getText().toString());




                                            Toast.makeText(getBaseContext(), getString(R.string.bienvenido) + "a" + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                                            intent.putExtra("nombre",edtNombre.getText().toString());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);




                                }
                            }
                        });
            }


        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
            }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(MainActivity.this,InicioActivity.class));
            finish();
        }
    }
    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Iniciar Sesion");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText edtPassword = (MaterialEditText) login_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtEmail = (MaterialEditText) login_layout.findViewById(R.id.edtEmail);
        final FirebaseAuth login_aut = FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        dialog.setView(login_layout);
        dialog.setPositiveButton("Iniciar Sesion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                //deshabilitar el botón iniciar sesión si se está procesando
                btnIngresar.setEnabled(false);

                //Check validation

                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor ingrese una contraseña", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }


                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor ingrese una contraseña", Snackbar.LENGTH_SHORT)
                            .show();
                    return;

                }


                    final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setCancelable(false).build();
                    waitingDialog.show();

                auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }else{
                                    //dialogInterface.dismiss();
                                    Snackbar.make(rootLayout, "Datos incorrectos", Snackbar.LENGTH_SHORT)
                                            .show();
                                    waitingDialog.dismiss();
                                }
                            }
                        });

    }
});
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}

