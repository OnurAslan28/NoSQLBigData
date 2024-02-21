package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LolMongo implements Datenbank {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;

    private final LaufzeitMessung performanceMeasurement;

    public LolMongo(String dbName, String collectionName) {
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

    @Override
    public String getCityAndStateById(String cityId) {
        return null;
    }

    @Override
    public List<CityData> getIdsByCityName(String cityName) {
        return null;
    }

    @Override
    public void dropDatabase() {

    }

    @Override
    public void closeConnection() {

    }

    @Override
    public void insertLolMatch(LolMatch lolMatch) {

    }

    @Override
    public List<LolMatch> getLolMatchesBySummoner(String summoner) {
        return findMatches(Filters.eq("summoner", summoner));
    }

    @Override
    public List<LolMatch> getLolMatchesByChampion(String champion) {
        return findMatches(Filters.eq("champion", champion));
    }

    @Override
    public List<LolMatch> getLolMatchesByWin(boolean win) {
        return findMatches(Filters.eq("win", win));
    }

    @Override
    public List<LolMatch> getLolMatchesByKills(int minKills, int maxKills) {
        return findMatches(Filters.and(
                Filters.gte("kills", minKills),
                Filters.lte("kills", maxKills)
        ));
    }

    // Hilfsmethode für die Abfragen
    private List<LolMatch> findMatches(org.bson.conversions.Bson filter) {
        List<LolMatch> matches = new ArrayList<>();
        try (var cursor = collection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                matches.add(parseLolMatch(doc));
            }
        }
        return matches;
    }

    private LolMatch parseLolMatch(Document document) {
        // Implementiere diese Methode entsprechend der Struktur deiner Dokumente
        // Achte darauf, dass die Reihenfolge und Typen der Daten übereinstimmen
        return new LolMatch(
                document.getLong("match_id"),
                document.getString("summoner"),
                document.getString("account_id"),
                document.getString("summoner_id"),
                document.getString("champion"),
                document.getBoolean("win"),
                document.getString("keystone"),
                document.getString("d_spell"),
                document.getString("f_spell"),
                document.getInteger("kills"),
                document.getInteger("assists"),
                document.getInteger("deaths"),
                document.getDouble("kda_ratio"),
                document.getInteger("damage_dealt"),
                document.getInteger("creep_score"),
                document.getInteger("vision_score"),
                document.getBoolean("first_blood"),
                document.getInteger("penta_kills"),
                document.getInteger("gold_earned"),
                document.getDouble("game_duration"),
                document.getList("items", String.class)
        );
    }
    // Implementiere die restlichen Methoden der Datenbank-Schnittstelle basierend auf den League of Legends-Daten
    // getCityAndStateById, getIdsByCityName, dropDatabase, closeConnection

}
