package test;

import com.deyuan.dao.ProductDao;
import com.deyuan.pojo.Product;
import com.deyuan.service.IProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class TestApp {

    public static void main(String[] args) {
        ApplicationContext c  = new ClassPathXmlApplicationContext("applicationContext.xml");
        IProductService productDao = (IProductService) c.getBean("productServiceImpl");
        System.out.println(productDao);
        List<Product> all = productDao.findAll();
        for (Product product : all) {
            System.out.println(product);
        }
    }
}
