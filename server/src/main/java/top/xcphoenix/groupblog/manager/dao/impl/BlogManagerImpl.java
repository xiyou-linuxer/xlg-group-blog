package top.xcphoenix.groupblog.manager.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import top.xcphoenix.groupblog.expection.blog.BlogParseException;
import top.xcphoenix.groupblog.mybatis.mapper.BlogMapper;
import top.xcphoenix.groupblog.mybatis.mapper.UserMapper;
import top.xcphoenix.groupblog.model.dao.Blog;
import top.xcphoenix.groupblog.manager.dao.BlogManager;
import top.xcphoenix.groupblog.model.vo.BlogData;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author      xuanc
 * @date        2020/1/17 下午6:20
 * @version     1.0
 */
@Slf4j
@Service
public class BlogManagerImpl implements BlogManager {

    BlogMapper blogMapper;
    UserMapper userMapper;
    PlatformTransactionManager transactionManager;

    @Autowired
    public BlogManagerImpl(BlogMapper blogMapper, UserMapper userMapper, PlatformTransactionManager transactionManager) {
        this.blogMapper = blogMapper;
        this.userMapper = userMapper;
        this.transactionManager = transactionManager;
    }

    @Override
    public void addBlog(Blog blog) {
        log.info("add blog");

        if (blogMapper.addBlog(blog) != 0) {
            Timestamp lastPubTime = userMapper.getLastPubTime(blog.getUid());
            if (lastPubTime.getTime() < blog.getPubTime().getTime()) {
                log.info("update user pubTime");
                userMapper.updateLastPubTime(blog.getPubTime(), blog.getUid());
            }
        }
    }

    @Override
    public boolean exists(String sourceId) {
        return blogMapper.exists(sourceId);
    }

    @Override
    public List<BlogData> getBlogSummaries(int pageSize, int pageOffset) {
        return blogMapper.getBlogSummaryAsTime(pageSize, pageOffset, null);
    }

    @Override
    public List<BlogData> getBlogSummariesAsUser(int pageSize, int pageOffset, long uid) {
        return blogMapper.getBlogSummaryAsTime(pageSize, pageOffset, uid);
    }

    @Override
    public BlogData getBlog(long blogId) {
        return blogMapper.getBlog(blogId);
    }

    @Override
    public long getBlogNumAsUser(long uid){
        return blogMapper.getBlogNumAsUser(uid);
    }

    @Override
    public List<Blog> getNearbyBlogs(Timestamp time) {
        return blogMapper.getNearbyBlogs(time, null);
    }

    @Override
    public List<Blog> getNearbyBlogsAsUser(Timestamp time, long uid) {
        return blogMapper.getNearbyBlogs(time, uid);
    }

    @Override
    public Boolean removeMemberBlog(long uid, long blogId) throws BlogParseException {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            Timestamp blogPubTime = blogMapper.getBlogPubTime(uid);
            if(blogPubTime == null){
                throw new BlogParseException("获取时间失败");
            }
            Boolean remove = blogMapper.removeMemberBlog(uid, blogId);
            if(!remove){
                throw new BlogParseException("删除blog失败");
            }
            //修改时间
            int count = userMapper.mandatoryUpdateLastPubTime(blogPubTime, uid);
            if(count != 1){
                throw new BlogParseException("修改时间失败");
            }
            transactionManager.commit(transactionStatus);
        } catch (BlogParseException e) {
            transactionManager.rollback(transactionStatus);
            return false;
        }

        return true;
    }

    @Override
    public Boolean removeMemberBlogs(long uid) throws BlogParseException {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            boolean result = blogMapper.removeMemberBlogs(uid);
            if (!result) {
                throw new BlogParseException("Failed to remove member blogs：删除blog出错");
            }
            Timestamp timestamp = new Timestamp(0L);
            int updateResult = userMapper.mandatoryUpdateLastPubTime(timestamp, uid);
            if (updateResult != 1) {
                throw new BlogParseException("Failed to remove member blogs:更新时间出错");
            }
            transactionManager.commit(transactionStatus);
        } catch (BlogParseException e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }

        return true;
    }

}
