package myBatis;

import db.dao.ProductsMapper;
import db.model.Products;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;

public class MyBatisTest {
    public static void main(String[] args) {
        SqlSessionFactory sessionFactory =
                new SqlSessionFactoryBuilder()
                        .build(MyBatisTest.class.getResourceAsStream("mybatis-config.xml"));

        SqlSession session = sessionFactory.openSession();

        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);

        // Добавление продукта в базу

        Products product = new Products();
        product.setCategoryId(1L);
        product.setId(24L);
        product.setPrice(555);
        product.setTitle("Juice");
        productsMapper.insert(product);
        session.commit();
        System.out.println(product);

        Products products = productsMapper.selectByPrimaryKey(24L);
        System.out.println(products);

        Assert.assertEquals(products, product);

        // Удаление продукта

        productsMapper.deleteByPrimaryKey(24L);
        session.commit();

        Products productDelete = productsMapper.selectByPrimaryKey(24L);
        System.out.println(productDelete);

        Assert.assertEquals(null, productDelete);

        session.close();





    }
}
