package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mongo implements Datenbank {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;

    private final LaufzeitMessung performanceMeasurement;

    public Mongo(String dbName, String collectionName) {
        connectToDatabase(dbName, collectionName);
        this.performanceMeasurement = new LaufzeitMessung();
    }

    public void connectToDatabase(String dbName, String collectionName) {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();

        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase(dbName);
        collection = mongoDatabase.getCollection(collectionName);
    }

    public void importDataFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Document> documents = new ArrayList<>();

            performanceMeasurement.startMeasurement();
            while ((line = br.readLine()) != null) {
                // Überprüfung, ob die Zeile nicht leer ist
                if (!line.trim().isEmpty()) {
                    Document document = Document.parse(line);
                    documents.add(document);
                }
            }

            collection.insertMany(documents);
            collection.createIndex(Indexes.ascending("_id"));
            performanceMeasurement.stopMeasurement("Der Datenimport");

            System.out.println("Datei erfolgreich in mongoDB importiert!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Rest des Codes bleibt unverändert

    public String getCityAndStateById(String cityId) {
        performanceMeasurement.startMeasurement();
        Document result = collection.find(Filters.eq("_id", cityId)).first();
        performanceMeasurement.stopMeasurement("Die Anfrage mit einer Id");
        assert result != null;
        return "Id: " + result.getString("_id") + " Name: " +  result.getString("city") + " State: " + result.getString("state");
    }

    public List<CityData> getIdsByCityName(String cityName) {
        performanceMeasurement.startMeasurement();
        List<CityData> cityList = new ArrayList<>();

        FindIterable<Document> result = collection.find(Filters.eq("city", cityName));
        for (Document document : result) {
            String cityId = document.getString("_id");
            String state = document.getString("state");
            String city = document.getString("city");
            CityData cityData = new CityData(cityId, city, state);
            cityList.add(cityData);
        }
        performanceMeasurement.stopMeasurement("Die Anfrage mit einem Stadtnamen");
        return cityList;
    }

    public void dropDatabase() {
        collection.drop();
        System.out.println("Die Collection " + collection.getNamespace() + " wurde erfolgreich gelöscht!");
    }

    public void closeConnection() {
        mongoClient.close();
        System.out.println("Verbindung zur mongoDb wurde getrennt!");
    }

    @Override
    public void insertLolMatch(LolMatch lolMatch) {

    }

    @Override
    public List<LolMatch> getLolMatchesBySummoner(String summoner) {
        return null;
    }

    @Override
    public List<LolMatch> getLolMatchesByChampion(String champion) {
        return null;
    }

    @Override
    public List<LolMatch> getLolMatchesByWin(boolean win) {
        return null;
    }

    @Override
    public List<LolMatch> getLolMatchesByKills(int minKills, int maxKills) {
        return null;
    }

}
