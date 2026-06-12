package fpoly.vinv01.assignmentapi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fpoly.vinv01.assignmentapi.R;

public class loginAsm extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private Button btnLogin, btnRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_asm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI();
        initEvent();
    }
    private void initUI() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
    }
    private void initEvent() {
        btnLogin.setOnClickListener(view -> {
            String email = edtEmail.getText().toString();
            String pass = edtPass.getText().toString();
            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(loginAsm.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(loginAsm.this, "Đăng nhập thành công" + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(loginAsm.this, listAsm.class);
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(loginAsm.this, "Đăng nhập thất bại: " + task.getException().getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(loginAsm.this, registerAsm.class);
            startActivity(intent);
        });
    }
}