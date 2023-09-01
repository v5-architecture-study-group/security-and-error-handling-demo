package com.example.secerrordemo.infra.session.hazelcast;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class HazelcastConfig {

    private static final Logger log = LoggerFactory.getLogger(HazelcastConfig.class);
    private static final String MAP_NAME = "com.example.secerrordemo.sessions";

    @Bean
    ClientConfig clientConfig(@Value("${HZ_ADDRESS}") List<String> addresses, @Value("${HZ_CLUSTERNAME}") String clusterName) {
        log.info("Connecting to cluster {} at {}", clusterName, addresses);
        var config = new ClientConfig();
        config.setClusterName(clusterName);
        config.getNetworkConfig().setAddresses(addresses);
        return config;
    }

    @Bean
    HazelcastSessionStore hazelcastSessionStore(HazelcastInstance hazelcastInstance) {
        return new HazelcastSessionStore(hazelcastInstance.getMap(MAP_NAME));
    }
}
