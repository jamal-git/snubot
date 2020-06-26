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
