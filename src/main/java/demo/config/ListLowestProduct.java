package demo.config;

import demo.model.Product;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ListLowestProduct {
    private static final int MAX_PRODUCTS = 5;
    private static final int MAX_SAME_PRODUCTS = 3;

    private Map<Integer, Integer> productAmount = new HashMap<>();
    private Map<Double, LinkedList<Product>> priceProducts = new HashMap<>();
    private Map<Integer, LinkedList<Product>> sameProductIds = new HashMap<>();
    private final LinkedList<Product> products = new LinkedList<>();
    private final AtomicInteger productsSize = new AtomicInteger();
    private volatile Double highestPrice = null;

    private static final ListLowestProduct INSTANCE = new ListLowestProduct();

    private ListLowestProduct() {
    }

    public static ListLowestProduct getInstance() {
        return INSTANCE;
    }

    void tryToAddProduct(Product product) {
        // test for ZERO price
        Double price = product.getPrice();
        if (price == null) {
            return;
        }

        synchronized (productsSize) {
            if (highestPrice == null) {
                highestPrice = price;
            }
            // test price to be less than a highest price from the result list
            if (highestPrice <= price && productsSize.get() >= MAX_PRODUCTS) {
                return;
            }
        }

        if (productsSize.get() < MAX_PRODUCTS) { // if a size of the result list is less than max result size
            synchronized (products) {
                addProduct(product, false);
            }
            return;
        }

        synchronized (products) {
            if (price < highestPrice) { // if a new product price is less the highest price from the result list
                addProduct(product, true);
            }
        }
    }

    synchronized public void sort() {
        products.sort(Comparator.comparing(Product::getPrice));
    }

    synchronized public LinkedList<Product> getProducts() {
        return products;
    }

    private void addProduct(Product product, boolean fullResultList) {
        if (productsSize.get() >= MAX_PRODUCTS && !fullResultList) {
            return;
        }

        if (product.getPrice() > highestPrice) {
            highestPrice = product.getPrice();
        }

        // update productAmount
        Integer productId = product.getProductId();
        Integer amountSameProduct = productAmount.getOrDefault(productId, 0);
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

        if (amountSameProduct > MAX_SAME_PRODUCTS) { //remove same product with the highest price

            // update productAmount
            productAmount.put(productId, --amountSameProduct);

            // update sameProductIds
            Product productToRemove = sameProducts.removeLast();

            // update priceProducts
            priceProducts.get(productToRemove.getPrice()).remove(productToRemove);

            // update priceProducts
            products.remove(productToRemove);


        } else if (fullResultList) { //remove the latest product with the highest price

            // update priceProducts
            LinkedList<Product> highestPriceProducts = priceProducts.get(highestPrice);
            Product productToRemove = highestPriceProducts.removeLast();
            Integer productIdToRemove = productToRemove.getProductId();

            // update highestPrice
            if (highestPriceProducts.isEmpty()) {
                priceProducts.remove(highestPrice);
                Optional<Double> optionalHighestPrice = priceProducts.keySet().stream().max(Comparator.naturalOrder());
                highestPrice = optionalHighestPrice.orElse(null);
            }

            // update productAmount
            Integer amountSameProductsToRemove = productAmount.getOrDefault(productIdToRemove, 0);
            productAmount.put(productIdToRemove, --amountSameProductsToRemove);

            // update sameProductIds
            sameProductIds.get(productIdToRemove).remove(productToRemove);

            // update priceProducts
            products.remove(productToRemove);

        } else {
            productsSize.getAndIncrement();
        }
    }
}
