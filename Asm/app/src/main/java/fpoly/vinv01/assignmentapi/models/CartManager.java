package fpoly.vinv01.assignmentapi.models;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Dish> cartList;

    private CartManager() {
        cartList = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<Dish> getCartList() {
        return cartList;
    }

    public void addToCart(Dish dish, int quantity) {
        for (Dish item : cartList) {
            if (item.getId() != null && item.getId().equals(dish.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // Clone và set số lượng
        Dish cloneDish = new Dish(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl(),
                dish.getCategory(),
                dish.getDescription()
        );
        cloneDish.setQuantity(quantity);
        cartList.add(cloneDish);
    }

    public void updateQuantity(String id, int quantity) {
        for (int i = 0; i < cartList.size(); i++) {
            Dish item = cartList.get(i);
            if (item.getId() != null && item.getId().equals(id)) {
                if (quantity <= 0) {
                    cartList.remove(i);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void removeFromCart(String id) {
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getId() != null && cartList.get(i).getId().equals(id)) {
                cartList.remove(i);
                return;
            }
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Dish item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartList.clear();
    }
}
