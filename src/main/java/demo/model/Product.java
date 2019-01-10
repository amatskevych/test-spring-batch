package demo.model;

public class Product {
    private Integer productId;
    private String name;
    private String condition;
    private String state;
    private Double price;

    @SuppressWarnings("unused")
    public Product() {
    }

    @SuppressWarnings("unused")
    public Product(Integer productId, String name, String condition, String state, Double price) {
        this.productId = productId;
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.price = price;
    }

    @SuppressWarnings("unused")
    public Integer getProductId() {
        return productId;
    }

    @SuppressWarnings("unused")
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getCondition() {
        return condition;
    }

    @SuppressWarnings("unused")
    public void setCondition(String condition) {
        this.condition = condition;
    }

    @SuppressWarnings("unused")
    public String getState() {
        return state;
    }

    @SuppressWarnings("unused")
    public void setState(String state) {
        this.state = state;
    }

    @SuppressWarnings("unused")
    public Double getPrice() {
        return price;
    }

    @SuppressWarnings("unused")
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", state='" + state + '\'' +
                ", price=" + price +
                '}';
    }
}
