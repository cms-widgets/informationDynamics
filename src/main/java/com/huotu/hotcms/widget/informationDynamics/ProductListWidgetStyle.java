package com.huotu.hotcms.widget.informationDynamics;

import com.huotu.hotcms.widget.WidgetStyle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Locale;

/**
 * Created by lhx on 2016/9/16.
 */

public class ProductListWidgetStyle implements WidgetStyle {
    @Override
    public String id() {
        return "ProductListWidgetStyle";
    }

    @Override
    public String name(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "bootstrap 风格";
        }
        return "bootstrap style Products Style";
    }

    @Override
    public String description(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "基于bootstrap样式的 产品列表 ";
        }
        return "Based on the bootstrap style by ProductListWidgetStyle";
    }

    @Override
    public Resource thumbnail() {
        return new ClassPathResource("/thumbnail/product.png", getClass().getClassLoader());
    }

    @Override
    public Resource previewTemplate() {
        return null;
    }

    @Override
    public Resource browseTemplate() {
        return new ClassPathResource("/template/productListWidgetStyle.html", getClass().getClassLoader());
    }
}
