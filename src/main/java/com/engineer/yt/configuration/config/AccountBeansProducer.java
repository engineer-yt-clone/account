package com.engineer.yt.configuration.config;

import com.orbitz.consul.Consul;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AccountBeansProducer {

    @Produces
    Consul consulClient = Consul.builder().build();

}
