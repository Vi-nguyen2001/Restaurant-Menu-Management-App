package fpoly.vinv01.assignmentapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.models.CartManager;
import fpoly.vinv01.assignmentapi.models.Dish;
import fpoly.vinv01.assignmentapi.service.HttpRequest;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Dish> dishList;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<Dish> dishList, CartUpdateListener listener) {
        this.dishList = dishList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvTenSPCart.setText(dish.getName());
        holder.tvGiaSPCart.setText(String.format("%.0f đ", dish.getPrice()));
        holder.tvSoLuongCart.setText(String.valueOf(dish.getQuantity()));

        String imageUrl = dish.getImageUrl();
        if (imageUrl != null) {
            if (imageUrl.contains("localhost")) {
                imageUrl = imageUrl.replace("http://localhost:3000/", HttpRequest.BASE_URL);
            } else if (!imageUrl.startsWith("http")) {
                imageUrl = HttpRequest.BASE_URL + imageUrl;
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgAvt);

        holder.btnPlusCart.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;
            Dish currentDish = dishList.get(currentPos);
            
            int newQty = currentDish.getQuantity() + 1;
            currentDish.setQuantity(newQty);
            CartManager.getInstance().updateQuantity(currentDish.getId(), newQty);
            notifyItemChanged(currentPos);
            if (listener != null) {
                listener.onCartUpdated();
            }
        });

        holder.btnMinusCart.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;
            Dish currentDish = dishList.get(currentPos);
            
            if (currentDish.getQuantity() > 1) {
                int newQty = currentDish.getQuantity() - 1;
                currentDish.setQuantity(newQty);
                CartManager.getInstance().updateQuantity(currentDish.getId(), newQty);
                notifyItemChanged(currentPos);
                if (listener != null) {
                    listener.onCartUpdated();
                }
            }
        });

        holder.btnRemoveCart.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;
            Dish currentDish = dishList.get(currentPos);
            
            CartManager.getInstance().removeFromCart(currentDish.getId()); // Đã tự động xóa khỏi dishList vì chung tham chiếu
            notifyItemRemoved(currentPos);
            notifyItemRangeChanged(currentPos, dishList.size());
            if (listener != null) {
                listener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt, btnRemoveCart;
        ImageButton btnMinusCart, btnPlusCart;
        TextView tvTenSPCart, tvGiaSPCart, tvSoLuongCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.imgAvt);
            btnRemoveCart = itemView.findViewById(R.id.btnRemoveCart);
            btnMinusCart = itemView.findViewById(R.id.btnMinusCart);
            btnPlusCart = itemView.findViewById(R.id.btnPlusCart);
            tvTenSPCart = itemView.findViewById(R.id.tvTenSPCart);
            tvGiaSPCart = itemView.findViewById(R.id.tvGiaSPCart);
            tvSoLuongCart = itemView.findViewById(R.id.tvSoLuongCart);
        }
    }
}
