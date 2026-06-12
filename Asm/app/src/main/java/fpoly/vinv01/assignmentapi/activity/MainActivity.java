package fpoly.vinv01.assignmentapi.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fpoly.vinv01.assignmentapi.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(() -> {
            startActivity(new android.content.Intent(MainActivity.this, loginAsm.class));
            finish();
        }, 5000);
    }
}