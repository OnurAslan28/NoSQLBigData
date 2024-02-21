package org.example;

import com.datastax.driver.core.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cassandra implements Datenbank {

    private final String keyspace;
    private final String table;
    private Cluster cluster;
    private Session session;
    private final LaufzeitMessung performanceMeasurement;

    public Cassandra(String keyspace, String table) {
        this.performanceMeasurement = new LaufzeitMessung();
        this.keyspace = keyspace;
        this.table = table;
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        connectToDatabase(keyspace, table);

        if (!doesKeyspaceExist(keyspace)) {
            createKeyspace(keyspace);
            createTable();
        }
        else {
            System.out.println("KEYSPACE: " + keyspace + " SCHON VORHANDEN!");
        }

    }

    @Override
    public void connectToDatabase(String dbName, String collectionName) {
        session = cluster.connect();
    }

    @Override
    public void closeConnection() {
        session.close();
        cluster.close();
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

    public boolean doesKeyspaceExist(String keyspaceName) {
        // Abrufen der Metadaten des Clusters
        Metadata metadata = cluster.getMetadata();

        // Überprüfen, ob der Keyspace vorhanden ist
        return metadata.getKeyspace(keyspaceName) != null;
    }

    private void createKeyspace(String keyspaceName) {
        String createKeyspaceQuery = String.format("CREATE KEYSPACE IF NOT EXISTS %s " +
                "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};", keyspaceName);
        cluster.connect().execute(createKeyspaceQuery);
        System.out.println("Keyspace: " + keyspaceName + " erstellt!");
    }

    public void createTable() {
        String createTableQuery = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                "id text PRIMARY KEY, " +
                "city text, " +
                "loc list<DOUBLE>, " +
                "pop int, " +
                "state text);", keyspace, table);
        session.execute(createTableQuery);
        System.out.println("TABLE: " + table + " ERSTELLT!");
    }

    @Override
    public void importDataFromFile(String filePath) {
        performanceMeasurement.startMeasurement();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            System.out.println("IMPORT VON DATEI LÄUFT");
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("_id", "id");
                importData(line);
            }
            performanceMeasurement.stopMeasurement("Der Datenimport");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importData(String line) {
        String insertQuery = String.format("INSERT INTO %s.%s JSON '%s';", keyspace, table, line);
        session.execute(insertQuery);
    }

    public void updateFussballColumnFamily(String columnName) {
        String queryBuilder = "ALTER TABLE " +
                keyspace + "." + table +
                " ADD " + columnName + " text;";
        session.execute(queryBuilder);
        System.out.println("Neue Spalte: '" + columnName + "' hinzugefügt!");

        setYes(columnName, "HAMBURG");
        setYes(columnName, "BREMEN");
    }

    private void setYes(String columnName, String cityName) {
        List<CityData> cityList;
        cityList = getIdsByCityName(cityName);

        for (CityData city : cityList) {
            String updateQuery = String.format("UPDATE %s.%s SET " + columnName + " = 'ja' WHERE id = '" + city.getCityId() + "'",
                    keyspace, table);
            session.execute(updateQuery);
        }
    }

    @Override
    public String getCityAndStateById(String cityId) {
        performanceMeasurement.startMeasurement();
        String query = String.format("SELECT city, state FROM %s.%s WHERE id = '%s';", keyspace, table, cityId);
        ResultSet resultSet = session.execute(query);
        performanceMeasurement.stopMeasurement("Die Anfrage mit einer Id");
        Row row = resultSet.one();
        if (row != null) {
            return "Id: " + cityId + " Name: " + row.getString("city") + " State: " + row.getString("state");
        } else {
            return "Kein Eintrag gefunden.";
        }
    }

    @Override
    public List<CityData> getIdsByCityName(String cityName) {
        performanceMeasurement.startMeasurement();
        List<CityData> cityList = new ArrayList<>();
        // Hier wird nach einer Stadt in der Tabelle gesucht und verschiedene Ids ausgegeben
        String query = String.format("SELECT id, state, city FROM %s.%s WHERE city = '%s' ALLOW FILTERING;", keyspace, table, cityName);
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            String cityId = row.getString("id");
            String state = row.getString("state");
            String city = row.getString("city");
            CityData cityData = new CityData(cityId, city, state);
            cityList.add(cityData);
        }
        performanceMeasurement.stopMeasurement("Die Anfrage mit einem Stadtnamen");
        return cityList;
    }

    public void dropKeyspace() {
        // Löschen aller Tabellen im Keyspace
        ResultSet resultSet = session.execute("SELECT table_name FROM system_schema.tables WHERE keyspace_name = '" + keyspace + "';");

        for (Row row : resultSet) {
            String tableName = row.getString("table_name");
            session.execute("TRUNCATE " + keyspace + "." + tableName + ";");
        }

        System.out.println("INHALT DES KEYSPACES: '" + keyspace + "' ENTFERNT");
    }

    @Override
    public void dropDatabase() {
        // Hier wird das gesamte Keyspace (Datenbank) gelöscht, einschließlich aller Tabellen und Daten
        String dropKeyspaceQuery = String.format("DROP KEYSPACE IF EXISTS %s;", keyspace);
        session.execute(dropKeyspaceQuery);
        System.out.println("KEYSPAVE: '" + keyspace + "' ENTFERNT");
        createKeyspace(keyspace);
        createTable();
    }

}
