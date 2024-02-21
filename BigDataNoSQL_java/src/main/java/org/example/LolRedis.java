package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LolRedis implements Datenbank {

    private Jedis jedis;
    private final LaufzeitMessung performanceMeasurement;

    public LolRedis() {
        connectToDatabase("no", "no");
        this.performanceMeasurement = new LaufzeitMessung();
    }

    public void connectToDatabase(String dbName, String collectionName) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            this.jedis = jedis;
            System.out.println("Connection to redis-db successful");
        } catch (Exception e) {
            System.out.println("Connection to redis-db failed");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importDataFromFile(String filePath) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            performanceMeasurement.startMeasurement();

            while ((line = bufferedReader.readLine()) != null) {
                // Konvertiere die Zeile in ein JSON-Array
                JSONArray jsonArray = new JSONArray("[" + line + "]");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    long matchId = json.getLong("match_id");
                    String summoner = json.getString("summoner");
                    String champion = json.getString("champion");
                    boolean win = json.getBoolean("win");
                    int kills = json.getInt("kills");

                    jedis.hset("lolmatch:" + matchId, "summoner", summoner);
                    jedis.hset("lolmatch:" + matchId, "champion", champion);
                    jedis.hset("lolmatch:" + matchId, "win", String.valueOf(win));
                    jedis.hset("lolmatch:" + matchId, "kills", String.valueOf(kills));

                    jedis.rpush("lolmatch_summoner:" + summoner, String.valueOf(matchId));
                    jedis.rpush("lolmatch_champion:" + champion, String.valueOf(matchId));
                    jedis.rpush("lolmatch_win:" + win, String.valueOf(matchId));
                    jedis.rpush("lolmatch_kills:" + kills, String.valueOf(matchId));
                }
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


    @Override
    public String getCityAndStateById(String cityId) {
        return null;
    }

    @Override
    public List<CityData> getIdsByCityName(String cityName) {
        return null;
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
        try {
            performanceMeasurement.startMeasurement();

            String matchId = String.valueOf(lolMatch.getMatch_id());
            jedis.hset("lolmatch:" + matchId, "summoner", lolMatch.getSummoner());
            jedis.hset("lolmatch:" + matchId, "accountId", lolMatch.getAccount_id());
            jedis.hset("lolmatch:" + matchId, "summonerId", lolMatch.getSummoner_id());
            jedis.hset("lolmatch:" + matchId, "champion", lolMatch.getChampion());
            jedis.hset("lolmatch:" + matchId, "win", String.valueOf(lolMatch.isWin()));
            jedis.hset("lolmatch:" + matchId, "keystone", lolMatch.getKeystone());
            jedis.hset("lolmatch:" + matchId, "dSpell", lolMatch.getDSpell());
            jedis.hset("lolmatch:" + matchId, "fSpell", lolMatch.getFSpell());
            jedis.hset("lolmatch:" + matchId, "kills", String.valueOf(lolMatch.getKills()));
            jedis.hset("lolmatch:" + matchId, "assists", String.valueOf(lolMatch.getAssists()));
            jedis.hset("lolmatch:" + matchId, "deaths", String.valueOf(lolMatch.getDeaths()));
            jedis.hset("lolmatch:" + matchId, "kdaRatio", String.valueOf(lolMatch.getKda_ratio()));
            jedis.hset("lolmatch:" + matchId, "damageDealt", String.valueOf(lolMatch.getDamage_dealt()));
            jedis.hset("lolmatch:" + matchId, "creepScore", String.valueOf(lolMatch.getCreep_score()));
            jedis.hset("lolmatch:" + matchId, "visionScore", String.valueOf(lolMatch.getVision_score()));
            jedis.hset("lolmatch:" + matchId, "firstBlood", String.valueOf(lolMatch.isFirst_blood()));
            jedis.hset("lolmatch:" + matchId, "pentaKills", String.valueOf(lolMatch.getPenta_kills()));
            jedis.hset("lolmatch:" + matchId, "goldEarned", String.valueOf(lolMatch.getGold_earned()));
            jedis.hset("lolmatch:" + matchId, "gameDuration", String.valueOf(lolMatch.getGame_duration()));

            // Die Liste der Items wird als einfache Zeichenfolge gespeichert (kann je nach Anforderungen angepasst werden)
            jedis.hset("lolmatch:" + matchId, "items", lolMatch.getItems().toString());

            performanceMeasurement.stopMeasurement("Inserting LolMatch into Redis");
        } catch (Exception e) {
            System.out.println("Failed to insert LolMatch into Redis");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LolMatch> getLolMatchesBySummoner(String summoner) {
        List<LolMatch> lolMatches = new ArrayList<>();
        List<String> matchIds = jedis.lrange("lolmatch_summoner:" + summoner, 0, -1);

        try {
            // Erstellen Sie eine CSV-Datei
            FileWriter csvWriter = new FileWriter("lolmatches_by_summoner.csv");
            // CSV-Header schreiben
            csvWriter.append("MatchId,Summoner,Champion,Win,Kills\n");

            for (String matchId : matchIds) {
                // Konvertiere die matchId von String zu long
                long longMatchId = Long.parseLong(matchId);

                String champion = jedis.hget("lolmatch:" + longMatchId, "champion");
                boolean win = Boolean.parseBoolean(jedis.hget("lolmatch:" + longMatchId, "win"));
                int kills = Integer.parseInt(jedis.hget("lolmatch:" + longMatchId, "kills"));

                // Verwende den Konstruktor mit long matchId
                LolMatch lolMatch = new LolMatch(longMatchId, summoner, champion, win, kills);
                lolMatches.add(lolMatch);

                // Füge die Daten zur CSV-Datei hinzu
                csvWriter.append(longMatchId + "," + summoner + "," + champion + "," + win + "," + kills + "\n");
            }

            // Schließe die CSV-Datei
            csvWriter.flush();
            csvWriter.close();

            // Konsolenausgabe
            System.out.println("Daten wurden in CSV-Datei exportiert: lolmatches_by_summoner.csv");

        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der CSV-Datei");
            e.printStackTrace();
        }

        return lolMatches;
    }


    @Override
    public List<LolMatch> getLolMatchesByChampion(String champion) {
        List<LolMatch> lolMatches = new ArrayList<>();
        List<String> matchIds = jedis.lrange("lolmatch_champion:" + champion, 0, -1);

        try {
            // Erstellen Sie eine CSV-Datei
            FileWriter csvWriter = new FileWriter("lolmatches_by_champion.csv");
            // CSV-Header schreiben
            csvWriter.append("MatchId,Summoner,Champion,Win,Kills\n");

            for (String matchId : matchIds) {
                // Konvertiere die matchId von String zu long
                long longMatchId = Long.parseLong(matchId);

                String summoner = jedis.hget("lolmatch:" + longMatchId, "summoner");
                boolean win = Boolean.parseBoolean(jedis.hget("lolmatch:" + longMatchId, "win"));
                int kills = Integer.parseInt(jedis.hget("lolmatch:" + longMatchId, "kills"));

                // Verwende den Konstruktor mit long matchId
                LolMatch lolMatch = new LolMatch(longMatchId, summoner, champion, win, kills);
                lolMatches.add(lolMatch);

                // Füge die Daten zur CSV-Datei hinzu
                csvWriter.append(longMatchId + "," + summoner + "," + champion + "," + win + "," + kills + "\n");
            }

            // Schließe die CSV-Datei
            csvWriter.flush();
            csvWriter.close();

            // Konsolenausgabe
            System.out.println("Daten wurden in CSV-Datei exportiert: lolmatches_by_champion.csv");

        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der CSV-Datei");
            e.printStackTrace();
        }

        return lolMatches;
    }

    @Override
    public List<LolMatch> getLolMatchesByWin(boolean win) {
        List<LolMatch> lolMatches = new ArrayList<>();
        List<String> matchIds = jedis.lrange("lolmatch_win:" + win, 0, -1);

        try {
            // Erstellen Sie eine CSV-Datei
            FileWriter csvWriter = new FileWriter("lolmatches_by_win.csv");
            // CSV-Header schreiben
            csvWriter.append("MatchId,Summoner,Champion,Win,Kills\n");

            for (String matchId : matchIds) {
                // Konvertiere die matchId von String zu long
                long longMatchId = Long.parseLong(matchId);

                String summoner = jedis.hget("lolmatch:" + longMatchId, "summoner");
                String champion = jedis.hget("lolmatch:" + longMatchId, "champion");
                int kills = Integer.parseInt(jedis.hget("lolmatch:" + longMatchId, "kills"));

                // Verwende den Konstruktor mit long matchId
                LolMatch lolMatch = new LolMatch(longMatchId, summoner, champion, win, kills);
                lolMatches.add(lolMatch);

                // Füge die Daten zur CSV-Datei hinzu
                csvWriter.append(longMatchId + "," + summoner + "," + champion + "," + win + "," + kills + "\n");
            }

            // Schließe die CSV-Datei
            csvWriter.flush();
            csvWriter.close();

            // Konsolenausgabe
            System.out.println("Daten wurden in CSV-Datei exportiert: lolmatches_by_win.csv");

        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der CSV-Datei");
            e.printStackTrace();
        }

        return lolMatches;
    }
    @Override
    public List<LolMatch> getLolMatchesByKills(int minKills, int maxKills) {
        List<LolMatch> lolMatches = new ArrayList<>();
        Set<String> matchIds = jedis.keys("lolmatch:*");

        try {
            // Erstellen Sie eine CSV-Datei
            FileWriter csvWriter = new FileWriter("lolmatches_by_kills.csv");
            // CSV-Header schreiben
            csvWriter.append("MatchId,Summoner,Champion,Win,Kills\n");

            for (String matchId : matchIds) {
                // Konvertiere die matchId von String zu long
                long longMatchId = Long.parseLong(matchId.split(":")[1]);

                // Holen Sie sich die Kills für das aktuelle LOL-Match
                int kills = Integer.parseInt(jedis.hget("lolmatch:" + longMatchId, "kills"));

                // Überprüfe, ob die Kills im gewünschten Bereich liegen
                if (kills >= minKills && kills <= maxKills) {
                    // Holen Sie sich die restlichen LOL-Match-Daten
                    String summoner = jedis.hget("lolmatch:" + longMatchId, "summoner");
                    String champion = jedis.hget("lolmatch:" + longMatchId, "champion");
                    boolean win = Boolean.parseBoolean(jedis.hget("lolmatch:" + longMatchId, "win"));

                    // Verwende den Konstruktor mit long matchId
                    LolMatch lolMatch = new LolMatch(longMatchId, summoner, champion, win, kills);
                    lolMatches.add(lolMatch);

                    // Füge die Daten zur CSV-Datei hinzu
                    csvWriter.append(longMatchId + "," + summoner + "," + champion + "," + win + "," + kills + "\n");
                }
            }

            // Schließe die CSV-Datei
            csvWriter.flush();
            csvWriter.close();

            // Konsolenausgabe
            System.out.println("Daten wurden in CSV-Datei exportiert: lolmatches_by_kills.csv");

        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der CSV-Datei");
            e.printStackTrace();
        }

        return lolMatches;
    }
}
