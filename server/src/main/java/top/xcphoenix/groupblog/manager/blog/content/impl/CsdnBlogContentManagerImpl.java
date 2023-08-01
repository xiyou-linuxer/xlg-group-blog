package top.xcphoenix.groupblog.manager.blog.content.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import top.xcphoenix.groupblog.expection.blog.BlogParseException;
import top.xcphoenix.groupblog.expection.processor.ProcessorException;
import top.xcphoenix.groupblog.manager.blog.content.BlogContentManager;
import top.xcphoenix.groupblog.model.dao.Blog;
import top.xcphoenix.groupblog.processor.Processor;
import top.xcphoenix.groupblog.service.picture.CrawPictureService;
import top.xcphoenix.groupblog.utils.HtmlUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author xuanc
 * @version 1.0
 * @date 2020/1/12 上午12:41
 */
@Service("content-csdn")
@Slf4j
@PropertySource(value = "${config-dir}/content/csdnBlogRule.properties", encoding = "UTF-8")
public class CsdnBlogContentManagerImpl implements BlogContentManager {

    @Value("${blog.rule.title}")
    private String titleRule;
    @Value("${blog.rule.isOriginal}")
    private String isOriginalRule;
    @Value("${blog.original.flag}")
    private String originalFlag;
    @Value("${blog.rule.author}")
    private String authorRule;
    @Value("${blog.rule.pubTime}")
    private String pubTimeRule;
    @Value("${blog.pubTime.format}")
    private String pubTimeFormat;
    @Value("${blog.rule.content}")
    private String contentRule;
    @Value("${blog.summary.word.limit:200}")
    private int summaryWordLimit;
    CrawPictureService crawPictureService;
    private Processor processor;

    public CsdnBlogContentManagerImpl(@Qualifier("craw-picture") CrawPictureService crawPictureService,
                                      @Qualifier("selenium") Processor processor) {
        this.crawPictureService = crawPictureService;
        this.processor = processor;
    }

    @Override
    public Blog getBlog(String url, Blog blog) throws ProcessorException, BlogParseException {
        log.info("get blog data from web content");

        SimpleDateFormat pubDateFormat = new SimpleDateFormat(pubTimeFormat);
        String webContent = (String) processor.processor(url);
        blog = blog == null ? new Blog() : blog;

        try {
            Document document = Jsoup.parse(webContent);

            if (blog.getTitle() == null) {
                blog.setTitle(document.select(titleRule).first().text());
            }
            if (blog.getAuthor() == null) {
                blog.setAuthor(document.select(authorRule).first().text());
            }
            if (blog.getPubTime() == null) {
                String timeStr = document.select(pubTimeRule).first().text();
                //删除除时间外字
                timeStr = timeStr.replaceAll("[\u4e00-\u9fa5]", "");
                timeStr = timeStr.replaceAll("[a-zA-Z]","");
                blog.setPubTime(new Timestamp(pubDateFormat.parse(timeStr).getTime()));
            }

            Element content = document.select(contentRule).first();
            String contentHtml = crawPictureService.downPicture(content,url);
            blog.setContent(contentHtml);

            if (blog.getSummary() == null) {
                blog.setSummary(
                        HtmlUtil.delSpace(
                                HtmlUtil.htmlToText(contentHtml)
                        ).substring(0, summaryWordLimit)
                );
            }

            blog.setOriginal(
                    document.getElementsByClass("article-type-img")
                            .first().attr("src")
                            .contains(originalFlag)
            );
        } catch (Exception ex) {
            throw new BlogParseException("blog parse error", ex);
        }

        return blog;
    }

}
