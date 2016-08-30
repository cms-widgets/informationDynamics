/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotcms.widget.informationDynamics;

import com.huotu.hotcms.widget.WidgetStyle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Locale;

/**
 * @author CJ
 */
public class DefaultWidgetStyle implements WidgetStyle {

    @Override
    public String id() {
        return "informationDynamicsDefaultStyle";
    }

    @Override
    public String name(Locale locale) {
        if (locale.equals(Locale.CHINESE)) {
            return "bootstrap 风格";
        }
        return "bootstrap style informationDynamics";
    }

    @Override
    public String description(Locale locale) {
        if (locale.equals(Locale.CHINESE)) {
            return "基于bootstrap样式的   ";
        }
        return "Based on the bootstrap style by informationDynamics";
    }

    @Override
    public Resource thumbnail() {
        return new ClassPathResource("/thumbnail/defaultStyleThumbnail.png", getClass().getClassLoader());
    }

    @Override
    public Resource previewTemplate() {
        return null;
    }

    @Override
    public Resource browseTemplate() {
        return new ClassPathResource("/template/defaultStyleBrowseTemplate.html", getClass().getClassLoader());
    }

}
