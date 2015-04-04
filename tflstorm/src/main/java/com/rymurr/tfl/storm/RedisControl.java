package com.rymurr.tfl.storm;


import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class RedisControl {
    private static final Joiner JOINER = Joiner.on(":");
    private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final Jedis jedis;

    public RedisControl(String host) {
        String[] hostPortPair = host.split(":");
        if (hostPortPair.length == 2) {
            this.jedis = new Jedis(hostPortPair[0], Integer.parseInt(hostPortPair[1]));
        } else {
            this.jedis = new Jedis(hostPortPair[0]);
        }
    }

    public RedisObject entryList(UniqueLineEvent event) {
        addToList(event);
        return entry(event);
    }

    public void addToList(UniqueLineEvent event) {
        final String id = JOINER.join("trainEvent", event.getStationKey(), event.getTrainKey());
        final String value = FORMAT.print(event.getCreateTime()) + "@" + event.getLocation();
        jedis.lpush(id, value);

    }

    public RedisObject entry(UniqueLineEvent event) {
        final String id = JOINER.join("trainHistory", event.getStationKey(), event.getTrainKey());
        Boolean exists = jedis.exists(id);
        RedisObject object;
        if (exists) {
            ImmutableMap.Builder<String, String> hashBuilder = ImmutableMap.<String, String>builder()
                    .put("secondsToStation", Integer.toString(event.getSecondsToStation()))
                    .put("lastSeen", FORMAT.print(event.getCreateTime()))
                    .put("line", event.getLine())
                    .put("location", event.getLocation());
            if (!jedis.hget(id, "destination").equals(event.getDestination())) {
                jedis.hincrBy(id, "destinationChanged", 1);
                hashBuilder.put("destination", event.getDestination());
            }
            if (event.getSecondsToStation() == 0) {
                hashBuilder.put("completed", Boolean.toString(true));
                jedis.lpush("completedTrains", id);
            }
            jedis.hincrBy(id, "seenCount", 1);
            Map<String, String> hash = hashBuilder.build();
            jedis.hmset(id, hash);
            object = RedisObject.redisObjectFromRedis(jedis, id);
            System.out.println("increment = " + object);
            //jedis.publish(publishChannel, id);
        } else {
            Map<String, String> hash = ImmutableMap.<String, String>builder()
                    .put("firstSeen", FORMAT.print(event.getCreateTime()))
                    .put("lastSeen", FORMAT.print(event.getCreateTime()))
                    .put("secondsToStation", Integer.toString(event.getSecondsToStation()))
                    .put("destination", event.getDestination())
                    .put("location", event.getLocation())
                    .put("destinationChanged", "0")
                    .put("seenCount", "1")
                    .put("line", event.getLine())
                    .put("completed", Boolean.toString(false))
                    .build();
            jedis.hmset(id, hash);
            object = RedisObject.redisObjectFromRedis(jedis, id);
            System.out.println("new = " + object);
        }
        return object;
    }

    public static class RedisObject {
        private final String destination;
        private final String location;
        private final int destinationChanges;
        private final int seenCount;
        private final int secondsToStation;
        private final boolean completed;
        private final DateTime firstSeen;
        private final DateTime lastSeen;
        private final String line;

        public RedisObject(String destination, String location, int destinationChanges, int seenCount, int secondsToStation, boolean completed, DateTime firstSeen, DateTime lastSeen, String line) {
            this.destination = destination;
            this.location = location;
            this.destinationChanges = destinationChanges;
            this.seenCount = seenCount;
            this.secondsToStation = secondsToStation;
            this.completed = completed;
            this.firstSeen = firstSeen;
            this.lastSeen = lastSeen;
            this.line = line;
        }

        public RedisObject(Map<String, String> object) {
            this.destination = object.get("destination");
            this.location = object.get("location");
            this.destinationChanges = Integer.parseInt(object.get("destinationChanged"));
            this.seenCount = Integer.parseInt(object.get("seenCount"));
            this.secondsToStation = Integer.parseInt(object.get("secondsToStation"));
            this.completed = Boolean.parseBoolean(object.get("completed"));
            this.firstSeen = FORMAT.parseDateTime(object.get("firstSeen"));
            this.lastSeen = FORMAT.parseDateTime(object.get("lastSeen"));
            this.line = object.get("line");
        }

        public static RedisObject redisObjectFromRedis(Jedis jedis, String id) {
            Map<String, String> object = jedis.hgetAll(id);
            return new RedisObject(object);
        }

        public String getDestination() {
            return destination;
        }

        public String getLocation() {
            return location;
        }

        public int getDestinationChanges() {
            return destinationChanges;
        }

        public int getSeenCount() {
            return seenCount;
        }

        public int getSecondsToStation() {
            return secondsToStation;
        }

        public boolean isCompleted() {
            return completed;
        }

        public DateTime getFirstSeen() {
            return firstSeen;
        }

        public DateTime getLastSeen() {
            return lastSeen;
        }

        public String getLine() {
            return line;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("destination", destination)
                    .add("location", location)
                    .add("destinationChanges", destinationChanges)
                    .add("seenCount", seenCount)
                    .add("secondsToStation", secondsToStation)
                    .add("completed", completed)
                    .add("firstSeen", firstSeen)
                    .add("lastSeen", lastSeen)
                    .add("line", line)
                    .toString();
        }
    }
}
