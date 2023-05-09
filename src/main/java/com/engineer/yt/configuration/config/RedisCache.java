package com.engineer.yt.configuration.config;

import io.quarkus.redis.client.RedisClient;
import io.smallrye.mutiny.Uni;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class RedisCache {

    @Autowired
    RedisClient client;

//    private final ReactiveKeyCommands<String> keyCommands;
//    private final ValueCommands<String, String> countCommands;
//
//    public RedisCache(RedisDataSource ds, ReactiveRedisDataSource reactive) {
//        countCommands = ds.value(String.class);
//        keyCommands = reactive.key();
//
//    }
//
//    public String get(String key) {
//        String value = countCommands.get(key);
//        if (value == null) {
//            return "";
//        }
//        return value;
//    }
//
//    public  void set(String key, String value) {
//        countCommands.set(key, value);
//    }
//
//     public void setex(String key, long second, String value) {
//        countCommands.setex(key, second, value);
//    }
//
//    public void increment(String key, Long incrementBy) {
//        countCommands.incrby(key, incrementBy);
//    }
//
//    public Uni<Void> del(String key) {
//        return keyCommands.del(key)
//                .replaceWithVoid();
//    }
//
//    public Uni<List<String>> keys() {
//        return keyCommands.keys("*");
//    }
}
