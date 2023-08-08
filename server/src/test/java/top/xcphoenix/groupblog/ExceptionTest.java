package top.xcphoenix.groupblog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import top.xcphoenix.groupblog.expection.blog.BlogParseException;
import top.xcphoenix.groupblog.manager.dao.BlogManager;
import top.xcphoenix.groupblog.mybatis.mapper.BlogMapper;
import top.xcphoenix.groupblog.mybatis.mapper.UserMapper;

import java.sql.Timestamp;

/**
 * @author xuanc
 * @version 1.0
 * @date 2020/1/27 上午9:34
 */
@SpringBootTest
public class ExceptionTest {

    @Autowired
    private UserMapper userManager;
    @Autowired
    BlogManager blogManager;
    @Test
    void test1() throws BlogParseException {
//        Timestamp timestamp = new Timestamp(0L);
        blogManager.removeMemberBlogs(10158);
    }

    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    BlogMapper blogMapper;
    @Autowired
    UserMapper userMapper;
    @Test
    void test2() {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            boolean b = userManager.modifyPermissions(10158, 2);
            int i = userManager.updateLastPubTime(new Timestamp(0L), 10158);
            if(i != 1){
                throw new BlogParseException("nil");
            }
            transactionManager.commit(transactionStatus);
        } catch (BlogParseException e) {
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }

}
