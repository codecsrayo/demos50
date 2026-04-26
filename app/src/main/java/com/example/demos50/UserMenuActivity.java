package com.example.demos50;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class UserMenuActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ListView lvUsers;
    private Button btnAdd, btnLogout;
    private DatabaseHelper dbHelper;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        dbHelper   = new DatabaseHelper(this);
        tvWelcome  = findViewById(R.id.tvWelcome);
        lvUsers    = findViewById(R.id.lvUsers);
        btnAdd     = findViewById(R.id.btnAdd);
        btnLogout  = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("username");
        tvWelcome.setText("Bienvenido, " + username);

        userList = new ArrayList<>();
        adapter  = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        lvUsers.setAdapter(adapter);

        loadUsers();

        btnAdd.setOnClickListener(v -> showAddDialog());

        lvUsers.setOnItemClickListener((parent, view, position, id) ->
                showOptionsDialog(position));

        btnLogout.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        userList.clear();
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor.moveToFirst()) {
            do {
                int idIdx   = cursor.getColumnIndex(DatabaseHelper.COL_ID);
                int userIdx = cursor.getColumnIndex(DatabaseHelper.COL_USERNAME);
                int mailIdx = cursor.getColumnIndex(DatabaseHelper.COL_EMAIL);
                userList.add("ID: " + cursor.getInt(idIdx) +
                             " | " + cursor.getString(userIdx) +
                             " | " + cursor.getString(mailIdx));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Usuario");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        builder.setView(view);

        EditText etUser  = view.findViewById(R.id.dialogUsername);
        EditText etPass  = view.findViewById(R.id.dialogPassword);
        EditText etEmail = view.findViewById(R.id.dialogEmail);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString().trim();
            String e = etEmail.getText().toString().trim();
            if (u.isEmpty() || p.isEmpty() || e.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            long result = dbHelper.insertUser(u, p, e);
            if (result != -1) {
                Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else {
                Toast.makeText(this, "Error: usuario ya existe", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showOptionsDialog(int position) {
        String item = userList.get(position);
        int userId = Integer.parseInt(item.split("\\|")[0].replace("ID:", "").trim());

        String[] options = {"Editar", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle("Opciones")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showEditDialog(userId);
                    else confirmDelete(userId);
                }).show();
    }

    private void showEditDialog(int userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Usuario");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        builder.setView(view);

        EditText etUser  = view.findViewById(R.id.dialogUsername);
        EditText etPass  = view.findViewById(R.id.dialogPassword);
        EditText etEmail = view.findViewById(R.id.dialogEmail);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString().trim();
            String e = etEmail.getText().toString().trim();
            if (u.isEmpty() || p.isEmpty() || e.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updateUser(userId, u, p, e);
            Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            loadUsers();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmDelete(int userId) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar este usuario?")
                .setPositiveButton("Sí", (d, w) -> {
                    dbHelper.deleteUser(userId);
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
