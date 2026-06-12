package fpoly.vinv01.assignmentapi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fpoly.vinv01.assignmentapi.R;

public class ketNoi extends AppCompatActivity {
    private TextView category;
    private TextView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ket_noi);

        category = findViewById(R.id.category);
        menu = findViewById(R.id.menu);

        category.setOnClickListener(Intent -> {
            Intent intent = new Intent(ketNoi.this, Category_list.class);
            startActivity(intent);
        });

        menu.setOnClickListener(Intent -> {
            Intent intent = new Intent(ketNoi.this, listAsm.class);
            startActivity(intent);
        });
    }
}