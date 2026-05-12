package com.nishan.mobile.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TestDataManager {

    private static Map<String, User> users;

    private TestDataManager() {
        loadData();
    }

    private static class TestDataManagerHolder {
        private static final TestDataManager INSTANCE
                = new TestDataManager();
    }

    public static TestDataManager getInstance() {
        return TestDataManagerHolder.INSTANCE;
    }

    private void loadData() {
        try {
            InputStream stream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("testdata/User.json");

            if (stream == null) {
                throw new RuntimeException(
                        "Users.json not found in testdata/");
            }

            ObjectMapper mapper = new ObjectMapper();
            // read JSON into map of String → User
            users = mapper.readValue(stream,
                    mapper.getTypeFactory()
                            .constructMapType(
                                    Map.class, String.class, User.class));

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load test data", e);
        }
    }

    public User getUser(String userType) {
        User user = users.get(userType);
        if (user == null) {
            throw new RuntimeException(
                    "User type not found: " + userType +
                            " | Available: " + users.keySet());
        }
        return user;
    }
}