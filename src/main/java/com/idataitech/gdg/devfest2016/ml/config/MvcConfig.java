package com.idataitech.gdg.devfest2016.ml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.net.URLEncoder;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@Import({
        DispatcherServletAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        WebMvcAutoConfiguration.class
})
@EnableConfigurationProperties(ServerProperties.class)
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private MultipartConfigElement multipartConfig;

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
    public ServletRegistrationBean dispatcherServletRegistration(RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping,
                                                                 DispatcherServlet dispatcherServlet) {
        requestMappingInfoHandlerMapping.setAlwaysUseFullPath(true);

        String[] mappings = new String[]{
                "/*"                  // 根目录
        };
        ServletRegistrationBean servlet = new ServletRegistrationBean(dispatcherServlet, mappings);
        servlet.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
        if (multipartConfig != null) {
            servlet.setMultipartConfig(this.multipartConfig);
        }
        return servlet;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request.getMethod().equals("GET") && request.getHeader("Domain-Proxy") == null) {
                    String redirect = request.getScheme() + "://domainproxy.idataitech.com" + request.getRequestURI();
                    redirect = redirect + "?PROXY_TARGET=" + request.getHeader("Host");
                    String query = request.getQueryString();
                    if (query != null) {
                        redirect = redirect + "&" + query;
                    }
                    response.sendRedirect(redirect);
                    return false;
                }

                if (request.getRequestURI().equals(request.getContextPath() + "/wx/login")) {
                    return true;
                }

                if (request.getSession().getAttribute("openid") == null) {
                    String redirect = request.getRequestURL().toString();
                    String query = request.getQueryString();
                    if (query != null) {
                        redirect = redirect + "?" + query;
                    }
                    response.sendRedirect("/wx/login?redirect=" + URLEncoder.encode(redirect, "utf-8"));
                    return false;
                }

                return super.preHandle(request, response, handler);
            }
        }).addPathPatterns("/wx/**");
        super.addInterceptors(registry);
    }

}
