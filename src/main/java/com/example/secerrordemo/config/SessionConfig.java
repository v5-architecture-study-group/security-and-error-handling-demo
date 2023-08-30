package com.example.secerrordemo.config;

import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import java.io.IOException;

@Configuration
@EnableHazelcastHttpSession
class SessionConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        var config = new Config();
        var attributeConfig = new AttributeConfig()
                .setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                .setExtractorClassName(PrincipalNameExtractor.class.getName());
        config.getMapConfig(HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME)
                .addAttributeConfig(attributeConfig).addIndexConfig(
                        new IndexConfig(IndexType.HASH, HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));
        //var serializerConfig = new SerializerConfig();
        //serializerConfig.setImplementation(new HazelcastSessionSerializer()).setTypeClass(MapSession.class);
        //config.getSerializationConfig().addSerializerConfig(serializerConfig);
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    @Order(-200) // Spring Security filter chain is -100 by default
    public HttpFilter sessionStoringFilter() {
        return new HttpFilter() {
            @Override
            protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
                try {
                    super.doFilter(request, response, chain);
                } finally {
                    var session = request.getSession(false);
                    if (session != null) {
                        // Force session serialization after the request, in case the VaadinSession (or any other mutable session attribute) has been changed
                        session.getAttributeNames().asIterator().forEachRemaining(attributeName -> session.setAttribute(attributeName, session.getAttribute(attributeName)));
                    }
                    // TODO Something is still not properly serialized: Expected sync id 2, got 3. Could it be something with Websocket?
                }
            }
        };
    }
}
