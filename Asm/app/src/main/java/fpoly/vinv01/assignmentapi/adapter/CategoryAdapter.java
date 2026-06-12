package fpoly.vinv01.assignmentapi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.handle.item_Dish_handle;
import fpoly.vinv01.assignmentapi.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private item_Dish_handle handle;

    public CategoryAdapter(Context context, List<Category> categoryList, ArrayList<Category> list, item_Dish_handle handle) {
        this.context = context;
        this.categoryList = categoryList;
        this.handle = handle;
    }


    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText("Tên danh mục: " + category.getName());
        holder.tvDescription.setText("Mô tả: " + category.getDescription());
        holder.imgEdit.setOnClickListener(view -> handle.onEdit(position));
        holder.imgDelete.setOnClickListener(view -> handle.onDelete(position));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvDescription;
        ImageView imgEdit, imgDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
