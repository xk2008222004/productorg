package springbootdubbo.mapper;

import com.example.springbootdubbo.po.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {


    public List<Product>  queryProduct(List<Integer> list);


    public int updateProduct(Product product);
}
