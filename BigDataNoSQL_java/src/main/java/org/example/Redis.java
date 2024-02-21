package org.example;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Redis implements Datenbank {

    private Jedis jedis;
    private final LaufzeitMessung performanceMeasurement;

    public Redis() {
        connectToDatabase("no", "no");
        this.performanceMeasurement = new LaufzeitMessung();
    }

    public void connectToDatabase(String dbName, String collectionName) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            this.jedis = jedis;
            System.out.println("Connection to redis-db successful");
        }
        catch(Exception e) {
            System.out.println("Connection to redis-db failed");
            throw new RuntimeException(e);
        }
    }

    public void dropDatabase() {
        this.jedis.flushDB();
        System.out.println("Inhalt der Redis-DB wurde gelöscht");
    }

    public void closeConnection() {
        this.jedis.close();
        System.out.println("Redis-DB wurde gestoppt");
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

    public void importDataFromFile(String filePath) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            performanceMeasurement.startMeasurement();
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject json = new JSONObject(line);

                String id = json.getString("_id");
                String city = json.getString("city");
                double longitude = json.getJSONArray("loc").getDouble(0);
                double latitude = json.getJSONArray("loc").getDouble(1);
                int population = json.getInt("pop");
                String state = json.getString("state");

                jedis.hset("city:" + id, "city", city);
                jedis.hset("city:" + id, "longitude", String.valueOf(longitude));
                jedis.hset("city:" + id, "latitude", String.valueOf(latitude));
                jedis.hset("city:" + id, "population", String.valueOf(population));
                jedis.hset("city:" + id, "state", state);

                jedis.rpush("city_name:" + city, id);
            }
            performanceMeasurement.stopMeasurement("Der Datenimport");
            System.out.println("Import in Redis-DB erfolgreich!");
        } catch (IOException e) {
            System.out.println("Import in Redis-DB war nicht erfolgreich!");
            throw new RuntimeException(e);
        } finally {
            jedis.close();
        }
    }

    public String getCityAndStateById(String id)  {
        performanceMeasurement.startMeasurement();
        String city = jedis.hget("city:" + id, "city");
        String state = jedis.hget("city:" + id, "state");
        performanceMeasurement.stopMeasurement("Die Anfrage mit einer Id");
        if (city != null)  {
            return "City: " + city + ", State: " + state;
        }
        else {
            return "Es konnte keine Stadt mit der eingegebenen PLZ (ID) gefunden werden!";
        }
    }

    public List<CityData> getIdsByCityName(String cityName) {
        performanceMeasurement.startMeasurement();
        List<CityData> cityList = new ArrayList<>();
        List<String> cityIds = jedis.lrange("city_name:" + cityName, 0, -1);

        if (cityIds.isEmpty()) {
            System.out.println("Es konnte keine Stadt mit dem eingegebenen Namen gefunden werden!");
        }
        else {
            for (String id : cityIds) {
                String city = jedis.hget("city:" + id, "city");
                String state = jedis.hget("city:" + id, "state");
                CityData cityData = new CityData(id, city, state);
                cityList.add(cityData);
            }
        }
        performanceMeasurement.stopMeasurement("Die Anfrage mit einem Stadtnamen");
        return cityList;
    }

}
