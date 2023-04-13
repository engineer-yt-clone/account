package com.engineer.yt.configuration.lifecycle;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.health.ServiceHealth;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
public class AccountLifecycle {

    private String instanceId;

    @Inject
    Instance<Consul> consulClient;
    @ConfigProperty(name = "quarkus.application.name")
    String appName;
    @ConfigProperty(name = "quarkus.application.version")
    String appVersion;

    void onStart(@Observes StartupEvent ev) {
        if (consulClient.isResolvable()) {
            ScheduledExecutorService executorService = Executors
                    .newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {
                HealthClient healthClient = consulClient.get().healthClient();
                List<ServiceHealth> instances = healthClient
                        .getHealthyServiceInstances(appName).getResponse();
                instanceId = appName + "-" + instances.size();
                int port = Integer.parseInt(System.getProperty("quarkus.http.port"));
                ImmutableRegistration registration = ImmutableRegistration.builder()
                        .id(instanceId)
                        .name(appName)
                        .address("127.0.0.1")
                        .port(port)
                        .putMeta("version", appVersion)
                        .build();
                consulClient.get().agentClient().register(registration);
                log.info("Instance registered: id={}, address=127.0.0.1:{}",
                        registration.getId(), port);
            }, 5000, TimeUnit.MILLISECONDS);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        if (consulClient.isResolvable()) {
            consulClient.get().agentClient().deregister(instanceId);
            log.info("Instance de-registered: id={}", instanceId);
        }
    }

}
