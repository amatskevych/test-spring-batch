package demo.config;

import demo.model.Product;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListLowestProduct {
    private static final int MAX_PRODUCTS = 10;
    private static final int MAX_SAME_PRODUCTS = 3;

    private Map<Integer, Integer> productAmount = new HashMap<>();
    private Map<Double, LinkedList<Product>> priceProducts = new HashMap<>();
    private Map<Integer, LinkedList<Product>> sameProductIds = new HashMap<>();
    private LinkedList<Product> products = new LinkedList<>();
    private volatile Double highestPrice = null;

    private static final ListLowestProduct INSTANCE = new ListLowestProduct();

    private ListLowestProduct() {
    }

    public static ListLowestProduct getInstance() {
        return INSTANCE;
    }

    synchronized public void tryToAddProduct(Product product) {
        // test for ZERO price
        Double price = product.getPrice();
        if (price == null) {
            return;
        } else if (highestPrice == null) {
            highestPrice = price;
        }

        // test price to be less than a highest price from the result list
        if (highestPrice <= price && products.size() >= MAX_PRODUCTS) {
            return;
        }

        // if a size of the result list is less than max result size
        if (products.size() < MAX_PRODUCTS) {
            if (needToAddSameProduct(product)) {
                addProduct(product);
            }
            return;
        }

        Integer productId = product.getProductId();
        Integer amountSameProduct = getAmountOfSameProducts(product);

//
//        if (amountSameProduct >= MAX_SAME_PRODUCTS) {
//            return;
//        }
//
//        if (highestPrice <= price) {
//            //removeLastOrSameProduct();
//            addProduct(product);
//        }
//        if (getHighestPrice() > product.getProductId())
//
//
//            Integer productId = product.getProductId();
//        Integer amountSameProduct = productAmount.get(productId);
//        if (amountSameProduct == null) {
//            amountSameProduct = 0;
//        }
//        if (amountSameProduct < MAX_SAME_PRODUCTS) {
//            productAmount.put(productId, ++amountSameProduct);
//        }
    }

    synchronized public void sort() {
        products.sort(Comparator.comparing(Product::getPrice));
    }

    synchronized public LinkedList<Product> getProducts() {
        return products;
    }

    private void addProduct(Product product) {
        if (products.size() >= MAX_PRODUCTS) {
            return;
        }

        if (product.getPrice() > highestPrice) {
            highestPrice = product.getPrice();
        }

        // update productAmount
        Integer productId = product.getProductId();
        int amountSameProduct = getAmountOfSameProducts(product);
        productAmount.put(productId, ++amountSameProduct);

        // update sameProductIds
        LinkedList<Product> sameProducts = sameProductIds.computeIfAbsent(productId, k -> new LinkedList<>());
        sameProducts.addLast(product);
        sameProducts.sort(Comparator.comparing(Product::getPrice));

        // update priceProducts
        Double price = product.getPrice();
        LinkedList<Product> samePriceProducts = priceProducts.computeIfAbsent(price, k -> new LinkedList<>());
        samePriceProducts.addLast(product);

        // update products
        products.add(product);
    }

    private int getAmountOfSameProducts(Product product) {
        if (product == null) {
            return 0;
        }
        Integer productId = product.getProductId();
        Integer amountOfSameProducts = productAmount.get(productId);
        if (amountOfSameProducts == null) {
            return 0;
        }
        return amountOfSameProducts;
    }

    private boolean needToAddSameProduct(Product product) {
        if (getAmountOfSameProducts(product) >= MAX_SAME_PRODUCTS) {
            return sameProductIds.get(product.getProductId()).getLast().getPrice() > product.getPrice();
        }
        return true;
    }

    private Product needToRemoveSameProduct(Product product) {
//        if (getAmountOfSameProducts(product) >= MAX_SAME_PRODUCTS) {
//            return sameProductIds.get(product.getProductId()).getLast().getPrice() > product.getPrice();
//        }
        return null;
    }
}
