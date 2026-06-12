package fpoly.vinv01.assignmentapi.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.models.CartManager;
import fpoly.vinv01.assignmentapi.models.Dish;
import fpoly.vinv01.assignmentapi.service.HttpRequest;

public class detailAsm extends AppCompatActivity {

    private ImageView btnBack, imgDetail;
    private TextView tvDetailName, tvDetailCategory, tvDetailPrice, tvDetailDescription, tvQuantity;
    private MaterialButton btnMinus, btnPlus, btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_asm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();
        bindData();
        setupListeners();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBack);
        imgDetail = findViewById(R.id.imgDetail);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void bindData() {
        Dish dish = (Dish) getIntent().getSerializableExtra("dish");
        if (dish != null) {
            tvDetailName.setText(dish.getName());
            tvDetailCategory.setText("DANH MỤC: " + (dish.getCategory() != null ? dish.getCategory().toUpperCase() : "CHƯA XÁC ĐỊNH"));
            tvDetailPrice.setText(String.format("%.0f đ", dish.getPrice()));
            tvDetailDescription.setText(dish.getDescription());

            String imageUrl = dish.getImageUrl();
            if (imageUrl != null) {
                if (imageUrl.contains("localhost")) {
                    imageUrl = imageUrl.replace("http://localhost:3000/", HttpRequest.BASE_URL);
                } else if (!imageUrl.startsWith("http")) {
                    imageUrl = HttpRequest.BASE_URL + imageUrl;
                }
            }
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(imgDetail);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        final int[] quantity = {1};
        tvQuantity.setText(String.valueOf(quantity[0]));

        btnPlus.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            Dish dish = (Dish) getIntent().getSerializableExtra("dish");
            if (dish != null) {
                CartManager.getInstance().addToCart(dish, quantity[0]);
                Toast.makeText(this, "Đã thêm " + quantity[0] + " " + dish.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}