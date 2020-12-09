package pl.calko.productlistapp.storeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import pl.calko.productlistapp.R;

public class StoreProductsAdapter extends BaseAdapter {

    private final ArrayList<Product> products;
    private AdapterListener adapterListener;
    private LayoutInflater inflater;

    public StoreProductsAdapter(ArrayList<Product> items, AdapterListener productsListener) {
        this.products = items;
        this.adapterListener = productsListener;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }

            convertView = inflater.inflate(R.layout.layout_for_product, parent, false);

            viewHolder.pName = convertView.findViewById(R.id.name);
            viewHolder.pPrice = convertView.findViewById(R.id.price);
            viewHolder.pEdit = convertView.findViewById(R.id.edit);
            viewHolder.pRemove = convertView.findViewById(R.id.remove);

            viewHolder.pName.setEnabled(false);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Product product = (Product) getItem(position);

        viewHolder.connectWithData(product,adapterListener);

        return convertView;
    }

    private static class ViewHolder {

        private CheckBox pName;
        private TextView pPrice;
        private TextView pEdit;
        private TextView pRemove;

        private void connectWithData(final Product product,final AdapterListener adapterListener){
            pName.setText(product.getName());
            pPrice.setText(String.format("Cena: %s", product.getPrice()));
            pName.setChecked(product.isStatus());

            pRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterListener.remove(product);
                }
            });

            pEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterListener.editProduct(product);
                }
            });
        }
    }
}
