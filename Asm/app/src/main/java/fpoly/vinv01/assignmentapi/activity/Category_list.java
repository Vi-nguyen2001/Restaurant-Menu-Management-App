package fpoly.vinv01.assignmentapi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.adapter.CategoryAdapter;
import fpoly.vinv01.assignmentapi.handle.item_Dish_handle;
import fpoly.vinv01.assignmentapi.models.Category;
import fpoly.vinv01.assignmentapi.models.Response;
import fpoly.vinv01.assignmentapi.service.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;

public class Category_list extends AppCompatActivity {
    private RecyclerView rcvCategory;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private HttpRequest httpRequest;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_list);

        rcvCategory = findViewById(R.id.rcvDanhMuc);
        add = findViewById(R.id.add);
        httpRequest = new HttpRequest();
        categoryList = new ArrayList<>();
        rcvCategory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(this, categoryList,(ArrayList<Category>) categoryList, new item_Dish_handle() {

            @Override
            public void onEdit(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Category_list.this);
                View dialogView = LayoutInflater.from(Category_list.this).inflate(R.layout.dialog_add_category, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                EditText edtName = dialogView.findViewById(R.id.edtNameCategory);
                EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
                Button btnAdd = dialogView.findViewById(R.id.btnAddCategoty);
                Button btnHuy = dialogView.findViewById(R.id.btnHuy);

                if (categoryList.get(position) != null) {
                    edtName.setText(categoryList.get(position).getName());
                    edtDescription.setText(categoryList.get(position).getDescription());
                }
                btnHuy.setOnClickListener(v -> dialog.dismiss());

                btnAdd.setOnClickListener(v -> {
                    String name = edtName.getText().toString().trim();
                    String description = edtDescription.getText().toString().trim();
                    Category currentCategory = categoryList.get(position);
                    currentCategory.setName(name);
                    currentCategory.setDescription(description);
                    updeteCategory(currentCategory,position);
                    dialog.dismiss();
                });
                dialog.show();
            }

            @Override
            public void onDelete(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Category_list.this);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này không?");
                builder.setPositiveButton("Xóa", (dialogInterface, i) -> {
                    deleteCategory(categoryList.get(position).getId(),position);
                });
                builder.setNegativeButton("Hủy", null);
                builder.show();
            }
        });
        rcvCategory.setAdapter(adapter);
        fetchCategoryList();
        add.setOnClickListener(view -> showAddEditDialog(null,-1));
    }

    private void fetchCategoryList() {
        httpRequest.callApi().getCategories().enqueue(new Callback<Response<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                if (response.isSuccessful()) {
                    Response<ArrayList<Category>> apiResponse = response.body();
                    if (apiResponse!= null && apiResponse.getStatus() == 200){
                        categoryList.clear();
                        categoryList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        Log.e("API_SUCCESS", "Lấy danh mục thành công");
                    }else {
                        Log.e("API_ERROR", "Lỗi phản hồi" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Category>>> call, Throwable t) {
                Log.e("CATEGORY", "Lỗi tải danh mục: " + t.getMessage());
            }
        });
    }

    private void showAddEditDialog(Category category,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText edtName = dialogView.findViewById(R.id.edtNameCategory);
        EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
        Button btnAdd = dialogView.findViewById(R.id.btnAddCategoty);
        Button btnHuy = dialogView.findViewById(R.id.btnHuy);

        if (category != null) {
            edtName.setText(category.getName());
            edtDescription.setText(category.getDescription());
        }

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(Category_list.this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }

            Category newCategory = new Category();
            newCategory.setName(name);
            newCategory.setDescription(description);

            Call<Response<Category>> call = httpRequest.callApi().addCategory(newCategory);
            call.enqueue(new Callback<Response<Category>>() {
                @Override
                public void onResponse(Call<Response<Category>> call, retrofit2.Response<Response<Category>> response) {
                    if (response.isSuccessful()) {
                        Response<Category> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getStatus() == 200) {
                            Toast.makeText(Category_list.this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                            fetchCategoryList();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(Category_list.this, "Thêm danh mục thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String msg = "Lỗi " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorStr = response.errorBody().string();
                                Response<?> errorObj = new com.google.gson.Gson().fromJson(errorStr, Response.class);
                                if (errorObj != null && errorObj.getMessage() != null) {
                                    msg = errorObj.getMessage();
                                } else {
                                    msg = errorStr;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Category_list.this, "Thêm danh mục thất bại: " + msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Category>> call, Throwable t) {
                    Toast.makeText(Category_list.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void deleteCategory(String id,int position ){
        Call<Response<Category>> call = httpRequest.callApi().deleteCategory(id);
        call.enqueue(new Callback<Response<Category>>() {
            @Override
            public void onResponse(Call<Response<Category>> call, retrofit2.Response<Response<Category>> response) {
                if (response.isSuccessful()) {
                    Response<Category> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Toast.makeText(Category_list.this, "Xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                        categoryList.remove(position);
                        adapter.notifyItemRemoved(position);
                    } else {
                        Toast.makeText(Category_list.this, "Xóa danh mục thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<Category>> call, Throwable t) {
                Toast.makeText(Category_list.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updeteCategory(Category category,int position){
        Call<Response<Category>> call = httpRequest.callApi().updateCategory(category.getId(),category);
        call.enqueue(new Callback<Response<Category>>() {
            @Override
            public void onResponse(Call<Response<Category>> call, retrofit2.Response<Response<Category>> response) {
                if (response.isSuccessful()) {
                    Response<Category> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Toast.makeText(Category_list.this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                        categoryList.set(position,category);
                        adapter.notifyItemChanged(position);
                    } else {
                        Toast.makeText(Category_list.this, "Cập nhật danh mục thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<Category>> call, Throwable t) {
                Toast.makeText(Category_list.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}