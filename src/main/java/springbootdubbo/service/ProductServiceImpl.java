package springbootdubbo.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.springbootdubbo.annotation.BuziExceptionHandler;
import com.example.springbootdubbo.po.Product;
import com.example.springbootdubbo.po.ResultObject;
import com.example.springbootdubbo.service.ProductService;
import com.example.springbootdubbo.vo.OrderProductVo;
import com.example.springbootdubbo.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springbootdubbo.mapper.ProductMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Service(interfaceClass = ProductService.class,version = "1.0.0",timeout = 1500)
@Slf4j
@BuziExceptionHandler
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public ResultObject reduceStock(OrderVo orderVo) {
        List<OrderProductVo> list = orderVo.getOrderProductVos();
        Map<Integer,Integer> map = new HashMap<>();
        list.forEach(orderProductVo -> {
            map.put(orderProductVo.getProductId(),orderProductVo.getNum());
        });
        ResultObject resultObject = this.queryProduct(list.stream().map(orderProductVo -> orderProductVo.getProductId()).collect(Collectors.toList()));
        List<Product> productList = (List<Product>)resultObject.getData();
        List<Product> filterProductList = productList.stream().filter(product -> {return (product.getSkuStock()-map.get(product.getId())>=0);}).collect(Collectors.toList());
        if(productList.size()!=filterProductList.size()){
            log.info("==========商品库存不足");
            return ResultObject.fail("商品库存不足");
        }
        productList.forEach(product -> {
            int id = product.getId();
            int skuStock = product.getSkuStock();
            product = new Product();
            product.setId(id);
            product.setSkuStock(skuStock-map.get(id));
            this.updateProduct(product);
        });
        return ResultObject.success("");
    }

    @Override
    public ResultObject updateProduct(Product product) {
        int count = productMapper.updateProduct(product);
        return ResultObject.success(count);
    }

    @Override
    public ResultObject queryProduct(List<Integer> list) {
        List<Product> products =null;
        try{
            products = productMapper.queryProduct(list);
            return ResultObject.success(products);
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.fail();
        }
    }
}
