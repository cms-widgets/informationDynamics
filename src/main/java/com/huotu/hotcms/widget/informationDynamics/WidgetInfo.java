/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotcms.widget.informationDynamics;

import com.huotu.hotcms.service.common.ArticleSource;
import com.huotu.hotcms.service.common.ContentType;
import com.huotu.hotcms.service.common.PageType;
import com.huotu.hotcms.service.entity.Article;
import com.huotu.hotcms.service.entity.Category;
import com.huotu.hotcms.service.exception.PageNotFoundException;
import com.huotu.hotcms.service.repository.ArticleRepository;
import com.huotu.hotcms.service.repository.CategoryRepository;
import com.huotu.hotcms.service.service.CategoryService;
import com.huotu.hotcms.widget.CMSContext;
import com.huotu.hotcms.widget.ComponentProperties;
import com.huotu.hotcms.widget.PreProcessWidget;
import com.huotu.hotcms.widget.Widget;
import com.huotu.hotcms.widget.WidgetStyle;
import com.huotu.hotcms.widget.entity.PageInfo;
import com.huotu.hotcms.widget.service.CMSDataSourceService;
import com.huotu.hotcms.widget.service.PageService;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.NumberUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


/**
 * 数据源列表
 * <p>
 * 它可以展示指定数据源的所有下级数据源以及它们的内容</p>
 * <p>
 * 属性中可以保存 指定数据源serial,内容展示size以及内容展示页serial(可选)</p>
 * <p>
 * 同时可以支持请求参数
 * cid,pageNum,size</p>
 *
 * @author CJ
 */
public class WidgetInfo implements Widget, PreProcessWidget {
    public static final String Parameter_CID = "cid";
    public static final String Parameter_NUM = "pageNum";
    public static final String SERIAL = "serial";
    public static final String ContentSerial = "ContentSerial";
    public static final String SIZE = "size";
    public static final String TITLE = "title";
    public static final String DATA_SOURCE = "dataSources";
    public static final String DATA_LIST = "dataList";
    public static final String CATEGORY = "category";
    private static final Log log = LogFactory.getLog(WidgetInfo.class);

    @Override
    public String groupId() {
        return "com.huotu.hotcms.widget.informationDynamics";
    }

    @Override
    public String widgetId() {
        return "informationDynamics";
    }

    @Override
    public String name(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "文章/产品列表";
        }
        return "informationDynamics";
    }

    @Override
    public String description(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "这是一个文章/产品列表，你可以对组件进行自定义修改。";
        }
        return "This is a article or product list,  you can make custom change the component.";
    }

    @Override
    public String dependVersion() {
        return "1.1.0";
    }

    @Override
    public WidgetStyle[] styles() {
        return new WidgetStyle[]{new DefaultWidgetStyle(), new ProductListWidgetStyle(), new NewsWidgetStyle()};
    }

    @Override
    public Resource widgetDependencyContent(MediaType mediaType) {
        if (mediaType.equals(Widget.Javascript))
            return new ClassPathResource("js/widgetInfo.js", getClass().getClassLoader());
        return null;
    }

    @Override
    public Map<String, Resource> publicResources() {
        Map<String, Resource> map = new HashMap<>();
        map.put("thumbnail/aritcleList.png"
                , new ClassPathResource("thumbnail/aritcleList.png", getClass().getClassLoader()));
        map.put("thumbnail/product.png"
                , new ClassPathResource("thumbnail/product.png", getClass().getClassLoader()));
        return map;
    }

    @Override
    public void valid(String styleId, ComponentProperties componentProperties) throws IllegalArgumentException {
        WidgetStyle style = WidgetStyle.styleByID(this, styleId);
        //加入控件独有的属性验证
        String serial = (String) componentProperties.get(SERIAL);
        int count = Integer.valueOf(componentProperties.get(SIZE).toString());
        String title = componentProperties.get(TITLE).toString();
        if (serial == null || count < 1 || title == null) {
            throw new IllegalArgumentException("参数不能满足控件需求");
        }
    }

    @Override
    public Class springConfigClass() {
        return null;
    }


    @Override
    public ComponentProperties defaultProperties(ResourceService resourceService) throws IOException, IllegalStateException {
        ComponentProperties properties = new ComponentProperties();
        // 随意找一个数据源,如果没有。那就没有。。
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CMSDataSourceService.class);
        CategoryService categoryService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CategoryService.class);

        List<Category> categories = cmsDataSourceService.findArticleCategory();
        if (categories.isEmpty()) {
            CategoryRepository categoryRepository = CMSContext.RequestContext().getWebApplicationContext()
                    .getBean(CategoryRepository.class);
            ArticleRepository articleRepository = CMSContext.RequestContext().getWebApplicationContext()
                    .getBean(ArticleRepository.class);
            Category category = new Category();
            category.setContentType(ContentType.Article);
            category.setName("父级");
            categoryService.init(category);
            category.setSite(CMSContext.RequestContext().getSite());
            categoryRepository.save(category);
            properties.put(SERIAL, category.getSerial());

            Category category1 = new Category();
            category1.setContentType(ContentType.Article);
            category1.setName("子级1");
            categoryService.init(category1);
            category1.setSite(CMSContext.RequestContext().getSite());
            category1.setParent(category);
            categoryRepository.save(category1);

            Category category2 = new Category();
            category2.setContentType(ContentType.Article);
            category2.setName("子级2");
            categoryService.init(category2);
            category2.setSite(CMSContext.RequestContext().getSite());
            category2.setParent(category);
            categoryRepository.save(category2);

            Article article = new Article();
            article.setDeleted(false);
            article.setTitle("文章标题");
            article.setCategory(category1);
            article.setContent("文章内容");
            article.setCreateTime(LocalDateTime.now());
            article.setSerial(UUID.randomUUID().toString());
            article.setCreateTime(LocalDateTime.now());
            article.setAuthor("system");
            article.setDescription("文章描述");
            article.setArticleSource(ArticleSource.ORIGINAL);
            article.setLauds(120);
            article.setUnlauds(10);
            article.setScans(130);
            articleRepository.save(article);

            Article article2 = new Article();
            article2.setDeleted(false);
            article2.setTitle("文章标题");
            article2.setCategory(category2);
            article2.setContent("文章内容");
            article2.setCreateTime(LocalDateTime.now());
            article2.setSerial(UUID.randomUUID().toString());
            article2.setCreateTime(LocalDateTime.now());
            article2.setAuthor("system");
            article2.setDescription("文章描述");
            article2.setArticleSource(ArticleSource.ORIGINAL);
            article2.setLauds(120);
            article2.setUnlauds(10);
            article2.setScans(130);
            articleRepository.save(article2);
        } else {
            properties.put(SERIAL, categories.get(0).getSerial());
        }
        properties.put(SIZE, 8);
        properties.put(TITLE, "资讯动态");
        return properties;
    }

    @Override
    public void prepareContext(WidgetStyle style, ComponentProperties properties, Map<String, Object> variables
            , Map<String, String> parameters) {
        CategoryRepository categoryRepository = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CategoryRepository.class);
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean("cmsDataSourceService", CMSDataSourceService.class);
        //1、取context参数pageNum,serial
        String serial = (String) properties.get(WidgetInfo.SERIAL);
        Category parentCategory = categoryRepository.findBySerialAndSite(serial, CMSContext.RequestContext().getSite());
        variables.put("parentCategory", parentCategory);

        //2、取子数据源列表
        List<Category> dataSources = categoryRepository.findByParent_SerialAndDeletedFalse(serial);
        variables.put(DATA_SOURCE, dataSources); //数据源列表
        if (dataSources != null && !dataSources.isEmpty()) {

            String requestCid = parameters != null ? parameters.get(Parameter_CID) : null;
            Category category;
            if (requestCid != null) {
                category = categoryRepository.getOne(NumberUtils.parseNumber(requestCid, Long.class));
            } else
                category = dataSources.get(0);

            int pageNumber;
            String requestNumber = parameters != null ? parameters.get(Parameter_NUM) : null;
            if (requestNumber != null) {
                pageNumber = NumberUtils.parseNumber(requestNumber, Integer.class);
            } else
                pageNumber = 1;


            String requestSize = parameters != null ? parameters.get(SIZE) : null;
            int pageSize;
            if (requestSize != null) {
                pageSize = NumberUtils.parseNumber(requestSize, Integer.class);
            } else
                pageSize = NumberUtils.parseNumber(properties.get(SIZE).toString(), Integer.class);


            //3、使用请求参数获取数据列表
            Page<Article> page = cmsDataSourceService.findArticleContent(category.getSerial(), pageNumber, pageSize);
            String contentSerial = (String) properties.get(ContentSerial);
            if (contentSerial != null) {
                PageInfo contentPage = cmsDataSourceService.findPageInfoContent(contentSerial);
                variables.put("contentURI", contentPage.getPagePath());
            } else {
                try {
                    PageInfo contentPage = getCMSServiceFromCMSContext(PageService.class)
                            .getClosestContentPage(category, null, PageType.DataContent);
                    variables.put("contentURI", contentPage.getPagePath());
                } catch (PageNotFoundException e) {
                    log.warn("...", e);
                    variables.put("contentURI", variables.get("uri"));
                }
            }
            variables.put("pageNumber", pageNumber);
            variables.put(DATA_LIST, page);
            variables.put(CATEGORY, category);//当前选择的数据源
        } else
            variables.put(DATA_LIST, null);
    }
}
