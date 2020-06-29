package com.oopsjpeg.snubot;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.oopsjpeg.snubot.data.UserData;
import com.oopsjpeg.snubot.react.ReactContainer;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager
{
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoManager(String host, String database)
    {
        CodecProvider codecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(codecProvider));
        MongoClientOptions clientOptions = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

        client = new MongoClient(host, clientOptions);

        this.database = client.getDatabase(database);
    }

    public MongoCollection<UserData> getUserDataCollection()
    {
        return database.getCollection("users", UserData.class);
    }

    public MongoCollection<ReactContainer> getReactContainerCollection()
    {
        return database.getCollection("react_containers", ReactContainer.class);
    }

    public Map<String, UserData> fetchUserDataMap() {
        Snubot.LOGGER.info("Fetching user data map.");
        return getUserDataCollection().find().into(new ArrayList<>()).stream().collect(Collectors.toMap(UserData::getId, data -> data));
    }

    public Map<String, ReactContainer> fetchReactContainerMap() {
        Snubot.LOGGER.info("Fetching react container map.");
        return getReactContainerCollection().find().into(new ArrayList<>()).stream().collect(Collectors.toMap(ReactContainer::getId, data -> data));
    }

    public void saveUserData(UserData data)
    {
        Snubot.LOGGER.info("Saving user data of ID " + data.getId() + ".");
        getUserDataCollection().replaceOne(Filters.eq("_id", data.getId()), data, new ReplaceOptions().upsert(true));
    }

    public void saveReactContainer(ReactContainer container) {
        Snubot.LOGGER.info("Saving react container of ID " + container.getId() + ".");
        getReactContainerCollection().replaceOne(Filters.eq("_id", container.getId()), container, new ReplaceOptions().upsert(true));
    }
}
