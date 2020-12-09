package pl.calko.productlistapp.storeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.calko.productlistapp.R;

public class ProductEditActivity extends AppCompatActivity {
    private final static String TABLE_NAME = "storeProducts";
    private DatabaseReference storeProducts;

    private String userFirebaseId;
    private String productId;

    private CheckBox statusChbx;
    private EditText nameEt;
    private EditText priceEt;
    private Toolbar toolbar;
    private Button addToDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
        setContentView(R.layout.activity_product_edit);

        setupUi();
        setupProduct(getIntent().getStringExtra("arg_product_id"));
    }

    private void initFirebase() {
        userFirebaseId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storeProducts = database.getReference(TABLE_NAME);
    }

    private void setupUi() {
        nameEt = findViewById(R.id.nameEt);
        priceEt = findViewById(R.id.priceEt);
        statusChbx = findViewById(R.id.change_status);
        toolbar = findViewById(R.id.toolbar);
        addToDb = findViewById(R.id.add_to_db);
        addToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEt.getText().toString().isEmpty()) {
                    nameEt.setError("Product name required!");
                    return;
                }
                if (priceEt.getText().toString().isEmpty()) {
                    priceEt.setError("Price required!");
                    return;
                }

                if (productId == null) {
                    addProduct();
                } else {
                    updateProduct();
                }
                finish();
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addProduct() {
        String newId = storeProducts.child(userFirebaseId).push().getKey();
        Product product = new Product(newId, nameEt.getText().toString(),
                Float.valueOf(priceEt.getText().toString()), statusChbx.isChecked());
        storeProducts.child(userFirebaseId).child(newId).setValue(product);
    }

    private void updateProduct() {
        Product product = new Product(productId, nameEt.getText().toString(),
                Float.valueOf(priceEt.getText().toString()), statusChbx.isChecked());
        storeProducts.child(userFirebaseId).child(productId).setValue(product);
    }

    private void setupProduct(String productId) {
        this.productId = productId;
        if (productId != null) {
            addToDb.setText("Update");
            toolbar.setTitle("Edit product");
            storeProducts.child(userFirebaseId).child(productId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    storeProducts.child(userFirebaseId)
                            .child(ProductEditActivity.this.productId).removeEventListener(this);
                    Product product = dataSnapshot.getValue(Product.class);
                    handleProductToEdit(product);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    storeProducts.child(userFirebaseId)
                            .child(ProductEditActivity.this.productId).removeEventListener(this);
                }
            });
        } else {
            addToDb.setText("Add to database");
            toolbar.setTitle("New product");
        }
    }

    private void handleProductToEdit(Product product) {
        nameEt.setText(product.getName());
        priceEt.setText(String.format("%s", product.getPrice()));
        statusChbx.setChecked(product.isStatus());
    }
}
