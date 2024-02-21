package org.example;

import java.util.List;

public interface Datenbank {

    void connectToDatabase(String dbName, String collectionName);

    void importDataFromFile(String filePath);

    String getCityAndStateById(String cityId);

    List<CityData> getIdsByCityName(String cityName);

    void dropDatabase();

    void closeConnection();


    // Neue Funktionen für League of Legends-Daten
    void insertLolMatch(LolMatch lolMatch);

    List<LolMatch> getLolMatchesBySummoner(String summoner);

    List<LolMatch> getLolMatchesByChampion(String champion);

    List<LolMatch> getLolMatchesByWin(boolean win);

    List<LolMatch> getLolMatchesByKills(int minKills, int maxKills);

    // Weitere Suchfunktionen nach Bedarf...


}
