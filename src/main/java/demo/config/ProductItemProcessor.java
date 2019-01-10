package demo.config;

import demo.model.Product;
import org.springframework.batch.item.ItemProcessor;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(final Product product) throws Exception {
        //ListLowestProduct.getInstance().tryToAddProduct(product);
        return product;
    }
}
