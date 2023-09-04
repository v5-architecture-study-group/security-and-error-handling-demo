package com.example.secerrordemo.infra.session.hazelcast;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
class HazelcastConfig {

    private static final String MAP_NAME = "com.example.secerrordemo.sessions";

    @Bean
    ClientConfig clientConfig(@Value("${hazelcast.client.addresses}") List<String> addresses, @Value("${hazelcast.client.cluster-name}") String clusterName) {
        var config = new ClientConfig();
        config.setClusterName(clusterName);
        config.getNetworkConfig().setAddresses(addresses);
        return config;
    }

    @Bean
    HazelcastSessionStore hazelcastSessionStore(HazelcastInstance hazelcastInstance, @Value("${server.servlet.session.timeout}") Duration sessionTimeout) {
        var mapConfig = new MapConfig(MAP_NAME);
        mapConfig.setTimeToLiveSeconds((int) sessionTimeout.plusSeconds(30).toSeconds()); // Add a 30 sec slack
        hazelcastInstance.getConfig().addMapConfig(mapConfig);
        return new HazelcastSessionStore(hazelcastInstance.getMap(MAP_NAME));
    }
}
