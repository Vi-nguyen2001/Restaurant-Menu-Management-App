package fpoly.vinv01.assignmentapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.activity.Cart_manager;
import fpoly.vinv01.assignmentapi.activity.detailAsm;
import fpoly.vinv01.assignmentapi.handle.item_Dish_handle;
import fpoly.vinv01.assignmentapi.models.CartManager;
import fpoly.vinv01.assignmentapi.models.Dish;
import fpoly.vinv01.assignmentapi.service.HttpRequest;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {
    private List<Dish> dishList;
    private Context context;

    private item_Dish_handle handle;

    public DishAdapter(Context context, List<Dish> dishList, item_Dish_handle handle) {
        this.context = context;
        this.dishList = dishList;
        this.handle = handle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvName.setText(dish.getName());
        holder.tvPrice.setText(String.format("Giá: %.0f đ", dish.getPrice()));
        holder.tvCategory.setText("Danh mục: " + dish.getCategory());

        String imageUrl = dish.getImageUrl();
        if (imageUrl != null) {
            if (imageUrl.contains("localhost")) {
                // Nếu server trả về localhost, ta thay thế bằng BASE_URL từ HttpRequest (đã chứa IP thật)
                imageUrl = imageUrl.replace("http://localhost:3000/", HttpRequest.BASE_URL);
            } else if (!imageUrl.startsWith("http")) {
                // Nếu server chỉ trả về đường dẫn tương đối (ví dụ: uploads/abc.jpg)
                // ta sẽ nối với BASE_URL để tạo link hoàn chỉnh
                imageUrl = HttpRequest.BASE_URL + imageUrl;
            }
        }
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imgDish);

        holder.btnAddToCart.setOnClickListener(v ->{
            CartManager.getInstance().addToCart(dish, 1);
            Toast.makeText(context, "Đã thêm " + dish.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, Cart_manager.class);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, detailAsm.class);
            intent.putExtra("dish", dish);
            context.startActivity(intent);
        });

        holder.imgEdit.setOnClickListener(view -> handle.onEdit(position));
        holder.imgDelete.setOnClickListener(view -> handle.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish, btnAddToCart, imgEdit, imgDelete;
        TextView tvName, tvPrice, tvCategory;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            tvName = itemView.findViewById(R.id.tvDishName);
            tvPrice = itemView.findViewById(R.id.tvDishPrice);
            tvCategory = itemView.findViewById(R.id.tvDishCategory);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
