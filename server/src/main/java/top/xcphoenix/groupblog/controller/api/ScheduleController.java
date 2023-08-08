package top.xcphoenix.groupblog.controller.api;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xcphoenix.groupblog.annotation.AdminAuth;
import top.xcphoenix.groupblog.annotation.UserAuth;
import top.xcphoenix.groupblog.expection.blog.BlogParseException;
import top.xcphoenix.groupblog.manager.dao.BlogManager;
import top.xcphoenix.groupblog.manager.dao.UserManager;
import top.xcphoenix.groupblog.model.Result;
import top.xcphoenix.groupblog.model.dao.Auth;
import top.xcphoenix.groupblog.model.dao.User;
import top.xcphoenix.groupblog.service.api.UserService;
import top.xcphoenix.groupblog.service.dispatch.ScheduleCrawlService;

import java.net.MalformedURLException;


@RestController
@RequestMapping("/api")
public class ScheduleController {

    private ScheduleCrawlService scheduleCrawlService;
    private UserService userService;
    private BlogManager blogManager;
    private UserManager userManager;
    public ScheduleController(ScheduleCrawlService scheduleCrawlService, UserService userService,UserManager userManager,BlogManager blogManager) {
        this.scheduleCrawlService = scheduleCrawlService;
        this.userService = userService;
        this.userManager = userManager;
        this.blogManager = blogManager;
    }

    @AdminAuth
    @GetMapping("/schedule")
    public Result<Void> execSchedule() {
        scheduleCrawlService.crawlIncr();
        return Result.success("定时任务开启...", null);
    }

    @UserAuth
    @GetMapping("/execByUid/{uid}")
    public Result<Void> execByUid(@PathVariable("uid") Long uid) {
        String success = scheduleCrawlService.crawlIncr(uid);
        if(success != null){
            return Result.error(-1,success);
        }
        return Result.success("添加任务开启...",null);
    }

    @AdminAuth
    @GetMapping("/removeMemberBlog/{uid}")
    public Result<Void> removeMemberBlogs(@PathVariable("uid") long uid) {
        //并修改blog时间为1970;
        try{
            blogManager.removeMemberBlogs(uid);
        }catch (Exception e){
            return Result.error(-1,"删除失败");
        }
        return Result.success("删除任务开启...",null);
    }

    @UserAuth
    @GetMapping("/removeMember/{uid}/{blog}")
    public Result<Void> removeBlog(@RequestHeader("Authorization") String token,
                                   @PathVariable("uid") long uid,
                                   @PathVariable("blog") long blogId) throws BlogParseException {
        token = token.substring(7);
        Long newUid = JWT.decode(token).getClaim("uid").asLong();
        if(newUid != uid){
            // 获取权限等级
            User user = userManager.checkUser(newUid);
            if(user.getAuthority() != Auth.ADMIN){
                return  Result.error(-1,"权限不足");
            }
        }
        // 获取删除前的时间，并修改提交时间
        Boolean result = blogManager.removeMemberBlog(uid,blogId);
        if(!result){
            return  Result.error(-1,"删除失败");
        }
        return  Result.success(null);
    }
}
