package com.idataitech.gdg.devfest2016.ml.config;

import freemarker.template.TemplateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig extends FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration {

    @Autowired
    @Qualifier("freeMarkerConfiguration")
    private freemarker.template.Configuration freemarkerConfiguration;

    @PostConstruct
    public void setVariables() throws TemplateException {
        freemarkerConfiguration.setSetting("number_format", "#.##");
    }

}
