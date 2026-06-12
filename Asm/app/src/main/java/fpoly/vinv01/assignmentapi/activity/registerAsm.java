package fpoly.vinv01.assignmentapi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import fpoly.vinv01.assignmentapi.R;

public class registerAsm extends AppCompatActivity {
    private EditText edtE, edtPassWord, edtConfirmPassWord;
    private Button btnR, btnCancel;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_asm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        initEvent();
    }
    private void initUi(){
        edtE = findViewById(R.id.edtE);
        edtPassWord = findViewById(R.id.edtPassWord);
        edtConfirmPassWord = findViewById(R.id.edtConfirmPassWord);
        btnR = findViewById(R.id.btnR);
        btnCancel = findViewById(R.id.btnCancel);
        auth = FirebaseAuth.getInstance();
    }
    private void initEvent(){
        btnCancel.setOnClickListener(view -> {
            finish();
        });

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtE.getText().toString();
                String pass = edtPassWord.getText().toString();
                String confirmPass = edtConfirmPassWord.getText().toString();
                if(email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()){
                    android.widget.Toast.makeText(registerAsm.this, "Vui lòng nhập đầy đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(confirmPass)) {
                    android.widget.Toast.makeText(registerAsm.this, "Mật khẩu xác nhận không khớp", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(registerAsm.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(registerAsm.this, loginAsm.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(registerAsm.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}