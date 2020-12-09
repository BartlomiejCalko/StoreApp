package pl.calko.productlistapp.storeapp;


public class Product {

    private  String id;
    private  String name;
    private  float price;
    private  boolean status;

   public Product() {
//        this.id = null;
//        this.name = null;
//        this.price = 0;
//        this.status = false;
    }

    public Product(String id, String name, float price, boolean status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public boolean isStatus() {
        return status;
    }
}
