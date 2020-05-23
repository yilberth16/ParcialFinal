package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.covid.Database.esquemaDB;
import com.example.covid.Modelo.Pacientes;
import com.example.covid.ViewHolder.PacienteViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class IngresarPacientes extends AppCompatActivity {

    FloatingActionButton fabAdd;
    ImageView imgCedula;
    FirebaseDatabase database;
    DatabaseReference paciente;
    ImagePicker imagePicker;
    CameraImagePicker cameraPicker;
    String pickerPath;
    private Uri fotoPerfilUri;

    RelativeLayout rootLayout;
    DatabaseReference reference;
    private FirebaseAuth mAuth;



    String direccionGps;
    esquemaDB db;
    private StorageReference referenceFotoDePerfil;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private FirebaseStorage storage;


    MaterialEditText edtNombrePaciente,edtDireccionPaciente,edtFechaDeIngreso,edtCedulaPaciente;
    FirebaseRecyclerAdapter<Pacientes, PacienteViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_pacientes);


        rootLayout = findViewById(R.id.rootLayoutIngresarPaciente);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarLayoutPaciente();
            }
        });

        mAuth = FirebaseAuth.getInstance();


        reference = FirebaseDatabase.getInstance().getReference("usuarios");
        imagePicker = new ImagePicker(this);
        cameraPicker = new CameraImagePicker(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_pacientes);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = new esquemaDB(getApplicationContext());

        storage = FirebaseStorage.getInstance();

        referenceFotoDePerfil = storage.getReference("Fotos/FotoCedulaPaciente/" + UUID.randomUUID().toString());

        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);

        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                if(!list.isEmpty()){
                    String path = list.get(0).getOriginalPath();
                    fotoPerfilUri = Uri.parse(path);
                    imgCedula.setImageURI(fotoPerfilUri);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(IngresarPacientes.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        cameraPicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                String path = list.get(0).getOriginalPath();
                fotoPerfilUri = Uri.fromFile(new File(path));
                imgCedula.setImageURI(fotoPerfilUri);
            }

            @Override
            public void onError(String s) {
                Toast.makeText(IngresarPacientes.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        database = FirebaseDatabase.getInstance();
        paciente = database.getReference("Pacientes").push();




        CargarTodosLosTrabajadores();
    }



    private void CargarTodosLosTrabajadores() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        Query reference = database2.getInstance().getReference("Pacientes").orderByChild("miUsuario").equalTo(user.getDisplayName());

        FirebaseRecyclerOptions<Pacientes> todosLosPacientes = new FirebaseRecyclerOptions.Builder<Pacientes>()
                .setQuery(reference, Pacientes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Pacientes, PacienteViewHolder>(todosLosPacientes) {
            @Override
            protected void onBindViewHolder(PacienteViewHolder holder, final int position, final Pacientes model) {
                holder.trabajador_name.setText(model.getNombreCompleto());


                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(IngresarPacientes.this,MapaPaciente.class);
                        intent.putExtra("id", adapter.getRef(position).getKey());
                        intent.putExtra("url",model.getFotoCedula());
                        startActivity(intent);
                    }
                });

                holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                       //EliminarPaciente();
                    }
                });
            }

            @NonNull
            @Override
            public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gestion_paciente_layout, parent, false);

                return new PacienteViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    protected void onResume() {
        super.onResume();
        CargarTodosLosTrabajadores();
    }

    private void EliminarPaciente(String key) {
        DatabaseReference eliminar = FirebaseDatabase.getInstance().getReference("Pacientes");
        eliminar.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(rootLayout, "Se ha eliminado correctamente", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IngresarPacientes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }



    private void mostrarLayoutPaciente() {
        final AlertDialog.Builder crear_trabajador_dialog = new AlertDialog.Builder(IngresarPacientes.this);
        crear_trabajador_dialog.setTitle("Ingresar paciente");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.crear_trabajador_layout,null);

         edtNombrePaciente = view.findViewById(R.id.edtNombrePaciente);
        edtDireccionPaciente = view.findViewById(R.id.edtDireccionPaciente);
         edtFechaDeIngreso = view.findViewById(R.id.edtFechaDeIngreso);
         edtCedulaPaciente = view.findViewById(R.id.edtCedulaPaciente);
         imgCedula = view.findViewById(R.id.imgCedulaPaciente);




        edtFechaDeIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(IngresarPacientes.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                        Calendar calendarResultado = Calendar.getInstance();
                        calendarResultado.set(Calendar.YEAR,year);
                        calendarResultado.set(Calendar.MONTH,mes);
                        calendarResultado.set(Calendar.DAY_OF_MONTH,dia);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = calendarResultado.getTime();
                        String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                        edtFechaDeIngreso.setText(fechaDeNacimientoTexto);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        imgCedula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        crear_trabajador_dialog.setPositiveButton("CREAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Pacientes").push();
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Toast.makeText(IngresarPacientes.this, "Ya el paciente se encuentra registrado", Toast.LENGTH_SHORT).show();
                        }else{

                            Pacientes pacientes = new Pacientes();

                            pacientes.setCedula(edtCedulaPaciente.getText().toString());
                            pacientes.setDireccion(edtDireccionPaciente.getText().toString());
                            pacientes.setFechaDeIngreso(edtFechaDeIngreso.getText().toString());
                            pacientes.setNombreCompleto(edtNombrePaciente.getText().toString());
                            pacientes.setMiUsuario(user.getDisplayName());
                            paciente.setValue(pacientes);
                            subirFotoUri();
                            db.agregarDatos(edtCedulaPaciente.getText().toString(),edtNombrePaciente.getText().toString(),edtDireccionPaciente.getText().toString(),edtFechaDeIngreso.getText().toString(),fotoPerfilUri.toString());
                            Snackbar.make(rootLayout, "Se ha registrado correctamente", Snackbar.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        crear_trabajador_dialog.setView(view);

        crear_trabajador_dialog.setIcon(R.drawable.ic_group_black_24dp);



        crear_trabajador_dialog.show();
    }



    public void subirFotoUri() {
        referenceFotoDePerfil.putFile(fotoPerfilUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                referenceFotoDePerfil.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        referenceFotoDePerfil.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                paciente.child("fotoCedula").setValue(uri.toString());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(getBaseContext(), "No se pudo subir la foto", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

    }
    private void seleccionarImagen() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(IngresarPacientes.this);
        dialog.setTitle("Foto de la cedula");

        String[] items = {"Galeria","Camara"};

        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        imagePicker.pickImage();
                        break;
                    case 1:
                        pickerPath = cameraPicker.pickImage();
                        break;
                }
            }
        });

        android.app.AlertDialog dialogConstruido = dialog.create();
        dialogConstruido.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == RESULT_OK){
            imagePicker.submit(data);
        }else if(requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            cameraPicker.reinitialize(pickerPath);
            cameraPicker.submit(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}