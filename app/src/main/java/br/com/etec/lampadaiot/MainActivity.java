package br.com.etec.lampadaiot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView imageLamp;
    private TextView lampStatus;
    private Button toggleLampButton;

    private static final String EMAIL = "Juliano.santos88@icloud.com";
    private static final String PASSWORD = "Juli@no7365";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("lampada");

        imageLamp = findViewById(R.id.imageLamp);
        lampStatus = findViewById(R.id.lampStatus);
        toggleLampButton = findViewById(R.id.toggleLampButton);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            mAuth.signInWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    setupLampControl();
                } else {
                    showToast("Falha na autenticação: " + task.getException().getMessage());
                }
            });
        } else {
            setupLampControl();
        }
    }

    private void setupLampControl() {
        toggleLampButton.setOnClickListener(v -> mDatabase.child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String status = task.getResult().getValue(String.class);
                String newStatus = "OFF".equals(status) ? "ON" : "OFF";
                mDatabase.child("status").setValue(newStatus);
            }
        }));

        mDatabase.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if ("ON".equals(status)) {
                    imageLamp.setImageResource(R.drawable.on);
                    lampStatus.setText("Lâmpada está ligada");
                } else {
                    imageLamp.setImageResource(R.drawable.off);
                    lampStatus.setText("Lâmpada está desligada");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Erro ao acessar o banco de dados: " + error.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
