package fpoly.vinv01.assignmentapi.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fpoly.vinv01.assignmentapi.R;
import fpoly.vinv01.assignmentapi.adapter.DishAdapter;
import fpoly.vinv01.assignmentapi.handle.item_Dish_handle;
import fpoly.vinv01.assignmentapi.models.Category;
import fpoly.vinv01.assignmentapi.models.Dish;
import fpoly.vinv01.assignmentapi.models.Response;
import fpoly.vinv01.assignmentapi.models.UploadResponse;
import fpoly.vinv01.assignmentapi.service.HttpRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class listAsm extends AppCompatActivity {

    private RecyclerView rcvDish;
    private DishAdapter adapter;
    private List<Dish> dishList;
    private HttpRequest httpRequest;
    private FloatingActionButton them;

    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    private Uri selectedImageUri;
    private int selectedDrawableResId = -1;
    private String selectedDrawableName = "";
    private ImageView dialogImageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_asm);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rcvDish = findViewById(R.id.danhSach);
        them = findViewById(R.id.them);
        httpRequest = new HttpRequest();
        dishList = new ArrayList<>();
        rcvDish.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DishAdapter(this, (ArrayList<Dish>) dishList, new item_Dish_handle() {
            @Override
            public void onEdit(int position) {
                showAddEditDialog(dishList.get(position));
            }

            @Override
            public void onDelete(int position) {
                confirmDeleteDish(position);
            }
        });
        rcvDish.setAdapter(adapter);

        // Đăng ký ActivityResultLauncher để nhận kết quả chọn ảnh từ thư viện
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        selectedDrawableResId = -1; // Xóa drawable đã chọn trước đó
                        selectedDrawableName = "";
                        if (dialogImageView != null && selectedImageUri != null) {
                            dialogImageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        fetchCategoryList();
        fetchDistList();

        them.setOnClickListener(view -> showAddEditDialog(null));
    }

    private void fetchCategoryList() {
        httpRequest.callApi().getCategories().enqueue(new Callback<Response<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<Category>> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        categoryList.clear();
                        categoryNames.clear();
                        categoryList.addAll(apiResponse.getData());
                        for (Category cat : categoryList) {
                            categoryNames.add(cat.getName());
                        }
                        Log.d("CATEGORY", "Lấy danh mục thành công: " + categoryNames.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Category>>> call, Throwable t) {
                Log.e("CATEGORY", "Lỗi tải danh mục: " + t.getMessage());
            }
        });
    }

    private void fetchDistList(){
        Call<Response<ArrayList<Dish>>> call = httpRequest.callApi().getDishes();
        call.enqueue(new Callback<Response<ArrayList<Dish>>>(){

            @Override
            public void onResponse(Call<Response<ArrayList<Dish>>> call, retrofit2.Response<Response<ArrayList<Dish>>> response) {
                if(response.isSuccessful()){
                    Response<ArrayList<Dish>> apiResponse = response.body();
                    if (apiResponse!= null && apiResponse.getStatus() == 200){
                        dishList.clear();
                        dishList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        Log.e("API_SUCCESS", "Lấy danh sách thành công");
                    }else {
                        Log.e("API_ERROR", "Lỗi phản hồi" + response.code());
                        Toast.makeText(listAsm.this, "Lỗi server" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Dish>>> call, Throwable t) {
                Log.e("API_FAILURE", "Lỗi kết nối API:"+t.getMessage());
                Toast.makeText(listAsm.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(final Dish editingDish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_them_dish, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogImageView = dialogView.findViewById(R.id.image);
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        Spinner spnCate = dialogView.findViewById(R.id.spnCate);
        EditText edtDesc = dialogView.findViewById(R.id.edtDesc);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        // Reset các giá trị ảnh tạm thời
        selectedImageUri = null;
        selectedDrawableResId = -1;
        selectedDrawableName = "";

        // Cài đặt adapter cho Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCate.setAdapter(spinnerAdapter);

        if (editingDish != null) {
            dialog.setTitle("Sửa món ăn");
            btnAdd.setText("Cập nhật");
            edtName.setText(editingDish.getName());
            edtPrice.setText(String.valueOf(editingDish.getPrice()));
            edtDesc.setText(editingDish.getDescription());

            if (editingDish.getCategory() != null) {
                int pos = categoryNames.indexOf(editingDish.getCategory());
                if (pos >= 0) {
                    spnCate.setSelection(pos);
                }
            }

            String imageUrl = editingDish.getImageUrl();
            if (imageUrl != null) {
                if (imageUrl.contains("localhost")) {
                    imageUrl = imageUrl.replace("http://localhost:3000/", HttpRequest.BASE_URL);
                } else if (!imageUrl.startsWith("http")) {
                    imageUrl = HttpRequest.BASE_URL + imageUrl;
                }
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.img)
                        .error(R.drawable.img)
                        .into(dialogImageView);
            }
        } else {
            dialog.setTitle("Thêm món ăn");
            btnAdd.setText("Thêm");
        }

        dialogImageView.setOnClickListener(v -> {
            AlertDialog.Builder imgBuilder = new AlertDialog.Builder(listAsm.this);
            imgBuilder.setTitle("Chọn nguồn ảnh");
            String[] options = {"Chọn từ thư viện (Gallery)", "Chọn ảnh mẫu có sẵn (Drawable)"};
            imgBuilder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(intent);
                } else {
                    showDrawablePicker();
                }
            });
            imgBuilder.show();
        });

        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(listAsm.this, "Vui lòng nhập đầy đủ tên và giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(listAsm.this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = "";
            if (spnCate.getSelectedItem() != null) {
                category = spnCate.getSelectedItem().toString();
            }

            final String finalCategory = category;

            if (selectedImageUri != null) {
                File file = getFileFromUri(selectedImageUri);
                if (file != null) {
                    uploadAndSaveDish(file, name, price, finalCategory, desc, editingDish, dialog);
                } else {
                    Toast.makeText(listAsm.this, "Lỗi xử lý file ảnh!", Toast.LENGTH_SHORT).show();
                }
            } else if (selectedDrawableResId != -1) {
                File file = getFileFromDrawable(selectedDrawableResId, selectedDrawableName);
                if (file != null) {
                    uploadAndSaveDish(file, name, price, finalCategory, desc, editingDish, dialog);
                } else {
                    Toast.makeText(listAsm.this, "Lỗi xử lý ảnh mẫu!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (editingDish != null) {
                    saveDishToServer(editingDish.getImageUrl(), name, price, finalCategory, desc, editingDish, dialog);
                } else {
                    File file = getFileFromDrawable(R.drawable.img, "default_img");
                    if (file != null) {
                        uploadAndSaveDish(file, name, price, finalCategory, desc, editingDish, dialog);
                    } else {
                        saveDishToServer("", name, price, finalCategory, desc, null, dialog);
                    }
                }
            }
        });

        dialog.show();
    }

    private void showDrawablePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh mẫu");
        final String[] itemNames = {"Ảnh Món 1 (img)", "Ảnh Món 2 (img_1)", "Ảnh Món 3 (img_2)"};
        final int[] itemResIds = {R.drawable.img, R.drawable.img_1, R.drawable.img_2};
        final String[] itemResNames = {"img", "img_1", "img_2"};

        builder.setItems(itemNames, (dialogInterface, i) -> {
            selectedDrawableResId = itemResIds[i];
            selectedDrawableName = itemResNames[i];
            selectedImageUri = null; // Clear gallery uri
            if (dialogImageView != null) {
                dialogImageView.setImageResource(selectedDrawableResId);
            }
        });
        builder.show();
    }

    private File getFileFromUri(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
            if (extension == null) extension = "jpg";
            File tempFile = new File(getCacheDir(), "upload_temp_" + System.currentTimeMillis() + "." + extension);
            InputStream inputStream = contentResolver.openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File getFileFromDrawable(int drawableResId, String resName) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResId);
            File tempFile = new File(getCacheDir(), resName + "_" + System.currentTimeMillis() + ".png");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadAndSaveDish(File file, String name, double price, String category, String desc, Dish editingDish, AlertDialog dialog) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        httpRequest.callApi().uploadImage(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, retrofit2.Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String uploadedUrl = response.body().getImage();
                    if (uploadedUrl != null) {
                        saveDishToServer(uploadedUrl, name, price, category, desc, editingDish, dialog);
                    } else {
                        Toast.makeText(listAsm.this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(listAsm.this, "Lỗi server upload ảnh!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(listAsm.this, "Lỗi kết nối upload ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDishToServer(String imageUrl, String name, double price, String category, String desc, Dish editingDish, AlertDialog dialog) {
        if (editingDish == null) {
            Dish newDish = new Dish(null, name, price, imageUrl, category, desc);
            httpRequest.callApi().addDish(newDish).enqueue(new Callback<Response<Dish>>() {
                @Override
                public void onResponse(Call<Response<Dish>> call, retrofit2.Response<Response<Dish>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Toast.makeText(listAsm.this, "Thêm món ăn thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchDistList();
                    } else {
                        String msg = "Lỗi không xác định!";
                        try {
                            if (response.errorBody() != null) {
                                String errorStr = response.errorBody().string();
                                Response<?> errorObj = new com.google.gson.Gson().fromJson(errorStr, Response.class);
                                if (errorObj != null && errorObj.getMessage() != null) {
                                    msg = errorObj.getMessage();
                                } else {
                                    msg = errorStr;
                                }
                            } else if (response.body() != null) {
                                msg = response.body().getMessage();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(listAsm.this, "Thêm thất bại: " + msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Dish>> call, Throwable t) {
                    Toast.makeText(listAsm.this, "Lỗi kết nối thêm món ăn!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            editingDish.setName(name);
            editingDish.setPrice(price);
            editingDish.setImageUrl(imageUrl);
            editingDish.setCategory(category);
            editingDish.setDescription(desc);

            httpRequest.callApi().updateDish(editingDish.getId(), editingDish).enqueue(new Callback<Response<Dish>>() {
                @Override
                public void onResponse(Call<Response<Dish>> call, retrofit2.Response<Response<Dish>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                        Toast.makeText(listAsm.this, "Cập nhật món ăn thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchDistList();
                    } else {
                        String msg = "Cập nhật thất bại!";
                        try {
                            if (response.errorBody() != null) {
                                String errorStr = response.errorBody().string();
                                Response<?> errorObj = new com.google.gson.Gson().fromJson(errorStr, Response.class);
                                if (errorObj != null && errorObj.getMessage() != null) {
                                    msg = errorObj.getMessage();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(listAsm.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Dish>> call, Throwable t) {
                    Toast.makeText(listAsm.this, "Lỗi kết nối cập nhật món ăn!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDeleteDish(int position) {
        Dish dish = dishList.get(position);
        if (dish.getId() == null) {
            Toast.makeText(this, "Không tìm thấy ID món ăn!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món " + dish.getName() + " không?")
                .setPositiveButton("Xóa", (dialogInterface, i) -> {
                    httpRequest.callApi().deleteDish(dish.getId()).enqueue(new Callback<Response<Dish>>() {
                        @Override
                        public void onResponse(Call<Response<Dish>> call, retrofit2.Response<Response<Dish>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                                Toast.makeText(listAsm.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                                fetchDistList();
                            } else {
                                String msg = "Xóa thất bại!";
                                try {
                                    if (response.errorBody() != null) {
                                        String errorStr = response.errorBody().string();
                                        Response<?> errorObj = new com.google.gson.Gson().fromJson(errorStr, Response.class);
                                        if (errorObj != null && errorObj.getMessage() != null) {
                                            msg = errorObj.getMessage();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(listAsm.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<Dish>> call, Throwable t) {
                            Toast.makeText(listAsm.this, "Lỗi kết nối xóa món ăn!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
