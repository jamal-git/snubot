package com.oopsjpeg.snubot;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.oopsjpeg.snubot.data.UserData;
import com.oopsjpeg.snubot.react.ReactContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirestoreManager
{
    private final Firestore database;

    public FirestoreManager(GoogleCredentials credentials)
    {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        database = FirestoreClient.getFirestore();
    }

    private CollectionReference getUserDataCollection()
    {
        return database.collection("users");
    }

    private CollectionReference getReactContainerCollection()
    {
        return database.collection("react_containers");
    }

    public List<UserData> fetchUserDataList()
    {
        Snubot.LOGGER.info("Fetching user data list from Firestore database.");
        try
        {
            return new ArrayList<>(getUserDataCollection().get().get().toObjects(UserData.class));
        } catch (InterruptedException | ExecutionException error)
        {
            error.printStackTrace();
        }
        return null;
    }

    public List<ReactContainer> fetchReactContainerList()
    {
        Snubot.LOGGER.info("Fetching react container list from Firestore database.");
        try
        {
            return new ArrayList<>(getReactContainerCollection().get().get().toObjects(ReactContainer.class));
        } catch (InterruptedException | ExecutionException error)
        {
            error.printStackTrace();
        }
        return null;
    }

    public void saveUserData(UserData data)
    {
        Snubot.LOGGER.info("Saving user data of ID " + data.getId() + ".");
        getUserDataCollection().document(data.getId()).set(data);
    }

    public void saveReactContainer(ReactContainer container) {
        Snubot.LOGGER.info("Saving react container of ID " + container.getId() + ".");
        getReactContainerCollection().document(container.getId()).set(container);
    }
}
