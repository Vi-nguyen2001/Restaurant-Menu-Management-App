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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.adapter.CartAdapter;
import fpoly.vinv01.assignmentapi.models.CartManager;
import fpoly.vinv01.assignmentapi.models.Dish;

public class Cart_manager extends AppCompatActivity {
    private RecyclerView rcvCart;
    private CartAdapter adapter;
    private List<Dish> dishList;

    private ImageView btnBackCart;
    private TextView tvTotalPrice;
    private MaterialButton btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupCart();
        setupListeners();
    }

    private void initViews() {
        rcvCart = findViewById(R.id.rcvCart);
        btnBackCart = findViewById(R.id.btnBackCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupCart() {
        dishList = CartManager.getInstance().getCartList();
        rcvCart.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new CartAdapter(dishList, new CartAdapter.CartUpdateListener() {
            @Override
            public void onCartUpdated() {
                updateTotalPrice();
            }
        });
        rcvCart.setAdapter(adapter);
        updateTotalPrice();
    }

    private void setupListeners() {
        btnBackCart.setOnClickListener(view -> {
            finish();
        });

        btnCheckout.setOnClickListener(view -> {
            if (dishList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = CartManager.getInstance().calculateTotal();
            Toast.makeText(this, String.format("Thanh toán thành công! Tổng tiền: %.0f đ", total), Toast.LENGTH_LONG).show();

            // Xóa sạch giỏ hàng sau khi thanh toán thành công
            CartManager.getInstance().clearCart();
            adapter.notifyDataSetChanged();
            updateTotalPrice();
        });
    }

    private void updateTotalPrice() {
        double total = CartManager.getInstance().calculateTotal();
        tvTotalPrice.setText(String.format("%.0f đ", total));
    }
}
