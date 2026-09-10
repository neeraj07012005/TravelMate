package com.travelmate.travelmate.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {

        MessageDispatcherServlet servlet =
                new MessageDispatcherServlet();

        servlet.setApplicationContext(applicationContext);

        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(
                servlet,
                "/ws/*"
        );
    }


    @Bean(name = "travel")
    public DefaultWsdl11Definition defaultWsdl11Definition(
            XsdSchema travelSchema) {

        DefaultWsdl11Definition wsdl =
                new DefaultWsdl11Definition();

        wsdl.setPortTypeName("TravelMatePort");

        wsdl.setLocationUri("/ws");

        wsdl.setTargetNamespace(
                "http://travelmate.com/travel"
        );

        wsdl.setSchema(travelSchema);

        return wsdl;
    }


    @Bean
    public XsdSchema travelSchema() {

        return new SimpleXsdSchema(
                new ClassPathResource("wsdl/travel.xsd")
        );
    }
}