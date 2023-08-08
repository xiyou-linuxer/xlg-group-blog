package top.xcphoenix.groupblog.manager.dao;

import top.xcphoenix.groupblog.expection.blog.BlogParseException;
import top.xcphoenix.groupblog.model.dao.Blog;
import top.xcphoenix.groupblog.model.vo.BlogData;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author      xuanc
 * @date        2020/1/17 下午6:17
 * @version     1.0
 */ 
public interface BlogManager {

    /**
     * 添加博客
     *
     * @param blog 博客信息
     */
    void addBlog(Blog blog);

    /**
     * 博客是否存在
     *
     * @param sourceId 博客来源id
     * @return 博客存在否
     */
    boolean exists(String sourceId);

    /**
     * 依据时间获取博客摘要信息
     *
     * @param pageSize 页大小
     * @param pageOffset 偏移量
     * @return 博客数据
     */
    List<BlogData> getBlogSummaries(int pageSize, int pageOffset);

    /**
     * 依据时间获取指定用户博客摘要信息
     *
     * @param pageSize 页大小
     * @param pageOffset 偏移量
     * @param uid 用户id
     * @return 博客数据
     */
    List<BlogData> getBlogSummariesAsUser(int pageSize, int pageOffset, long uid);


    /**
     * 获取指定博客的数据
     *
     * @param blogId 博客id
     * @return 博客数据
     */
    BlogData getBlog(long blogId);

    /**
     * 获取用户博客数量
     * @param uid 用户id
     * @return
     */
    long getBlogNumAsUser(long uid);

    /**
     * 获取附近时间的博客
     *
     * @param time 时间
     * @return 上一篇和下一篇的博客
     */
    List<Blog> getNearbyBlogs(Timestamp time);

    /**
     * 获取指定用户附近时间的博客
     *
     * @param time 时间
     * @param uid 用户id
     * @return 上一篇和下一篇的博客
     */
    List<Blog> getNearbyBlogsAsUser(Timestamp time, long uid);

    /**
     * 删除某人某一篇博客
     * @param uid 用uid
     * @param blogId 博客id
     * @return 结果
     */
    Boolean removeMemberBlog(long uid, long blogId) throws BlogParseException;

    /**
     *
     * @param uid
     * @return
     */
    Boolean removeMemberBlogs(long uid) throws BlogParseException;
}
