package top.xcphoenix.groupblog.manager.dao;

import org.apache.ibatis.annotations.Param;
import top.xcphoenix.groupblog.model.dao.User;
import top.xcphoenix.groupblog.model.dto.UserSummary;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author      xuanc
 * @date        2020/1/17 下午7:49
 * @version     1.0
 */ 
public interface UserManager {
    void createNewUser(User user);

    /**
     * 获取用户博客参数
     *
     * @param uid 用户id
     * @return 博客参数
     */
    User getUserBlogArgs(long uid);

    /**
     * 更新最新发布的博客时间
     * @param uid 用户id
     * @param timestamp 时间
     */
    void updateLastPubTime(long uid, Timestamp timestamp);

    /**
     * 获取用户博客的最新更新时间
     *
     * @param uid 用户id
     * @return 最近博客的推送时间
     */
    Timestamp getLastPubTime(long uid);

    /**
     * 获取用户概要信息
     *
     * @return 用户id,执行抓取任务需要的bean
     */
    List<UserSummary> getUsersSummary();

    /**
     * 获取用户描述
     *
     * @param uid 用户id
     * @return 用户描述
     */
    User getUserDesc(long uid);

    /**
     * 获取用户头像链接
     *
     * @param uid 用户id
     * @return 用户头像url
     */
    String getUserAvatar(long uid);

    /**
     *  获取cid对应的用户信息
     * @param cid grade对应的id
     * @return 用户信息
     */
    List<User> getUsersInfo(long cid);

    /**
     * 获取uid对应的用户摘要
     * @param uid
     * @return
     */
    UserSummary getUserSummaryByUid(long uid);

    /**
     * 检查用户信息
     * @param uid
     * @return
     */
    User checkUser(long uid);

    /**
     * 修改用户权限
     * @param uid 用户uid
     * @param cid 要修改的权限
     * @return  修改结果
     */
    boolean modifyPermissions(long uid,int cid);

    /**
     * 删除用户
     * @param uid
     * @return
     */
    boolean removeMember(Long uid);

    /**
     * 设置用户的博客为null
     * @param uid
     * @return
     */
    boolean updateUserArgToNULL(long uid);
}
