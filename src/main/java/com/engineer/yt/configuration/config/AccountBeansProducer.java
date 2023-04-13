package com.engineer.yt.configuration.config;

import com.orbitz.consul.Consul;
import io.quarkus.arc.properties.IfBuildProperty;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AccountBeansProducer {

    @Produces
    @IfBuildProperty(name = "quarkus.consul-discovery.enabled", stringValue = "true")
    Consul consulClient = Consul.builder().build();

}
