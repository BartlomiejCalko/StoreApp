package pl.calko.productlistapp.storeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.calko.productlistapp.R;

public class ProductListActivity extends AppCompatActivity {

    private final static String TABLE_NAME = "storeProducts";
    private DatabaseReference storeProducts;

    private String userFirebaseId;

    private ArrayList<Product> products = new ArrayList<>();
    private StoreProductsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        initFirebase();
        initUi();
        fetchProductsFromFirebase();
    }

    private void initUi() {
        findViewById(R.id.add_new_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductEditActivity(null);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        adapter = new StoreProductsAdapter(products, new AdapterListener() {
            @Override
            public void editProduct(Product product) {
                openProductEditActivity(product.getId());
            }

            @Override
            public void remove(Product product) {
                storeProducts.child(userFirebaseId).child(product.getId()).removeValue();
                products.remove(product);
                adapter.notifyDataSetChanged();
            }
        });
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    private void openProductEditActivity(String productId) {
        Intent intent = new Intent(this, ProductEditActivity.class);
        intent.putExtra("arg_product_id", productId);
        startActivity(intent);
    }

    private void initFirebase() {
        userFirebaseId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storeProducts = database.getReference(TABLE_NAME);
    }

    private void fetchProductsFromFirebase() {
        storeProducts.child(userFirebaseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadProductsToListView(cerateProductsFromFirebaseResult(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private List<Product> cerateProductsFromFirebaseResult(DataSnapshot dataSnapshot) {
        List<Product> products = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            Product product = data.getValue(Product.class);
            products.add(product);
        }
        return products;
    }

    private void loadProductsToListView(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        adapter.notifyDataSetChanged();
    }
}
