package org.example;

import java.util.List;
import java.util.Scanner;

public class Anwendung {

    public static void main(String[] args) {
        dbClient();
    }

    public static void dbClient() {

        System.out.println();
        System.out.println("DATENBANKEN AUSWÄHLEN");
        System.out.println("1 = REDISDB");
        System.out.println("2 = MONGODB");
        System.out.println("3 = CASSANDRADB");
        System.out.println("4 = LOL_MONGODB"); // Neu hinzugefügte Option
        System.out.println("5 = LOL_REDISDB"); // Neu hinzugefügte Option
        System.out.println("6 = PROGRAMM BEENDEN");

        Scanner scanner = new Scanner(System.in);

        if (scanner.hasNext()) {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    //Connect to redis db
                    Redis redisDatabase = new Redis();

                    //Delete contents of redis db
                    redisDatabase.dropDatabase();

                    //Import data into redis db
                    redisDatabase.importDataFromFile("C:\\Users\\Onur Aslan\\IdeaProjects\\aufgabe04\\src\\main\\resources\\plz.data");

                    //Start interacting with user
                    userClient(redisDatabase);
                    break;
                case "2":
                    //Connect to mongoDb
                    Mongo mongoDb = new Mongo("nosql", "cities");
                    //MongoDatabaseConnection mongoDb = new MongoDatabaseConnection("fussball", "teams");
                    //Delete contents of mongoDb collection
                    mongoDb.dropDatabase();

                    //Import data into mongodb
                    String dataFilePath = "C:\\Users\\Onur Aslan\\IdeaProjects\\aufgabe04\\src\\main\\resources\\plz.data";
                    //String dataFilePath = "C:\Users\Onur Aslan\IdeaProjects\aufgabe04\src\main\resources\sinndeslebensclear.txt";
                    mongoDb.importDataFromFile(dataFilePath);

                    //Start interacting with user
                    userClient(mongoDb);
                    break;
                case "3":
                    Cassandra cassandraDb = new Cassandra("nosql", "cities");
                    cassandraDb.dropDatabase();
                    cassandraDb.importDataFromFile("C:\\Users\\Onur Aslan\\IdeaProjects\\aufgabe04\\src\\main\\resources\\plz.data");
                    cassandraDb.updateFussballColumnFamily("fussball");
                    userClient(cassandraDb);
                    break;
                case "4":
                    // Neue Option für LOL_MONGODB
                    LolMongo lolMongoDb = new LolMongo("lol_db", "matches");
                    //lolMongoDb.dropDatabase();
                    //lolMongoDb.importDataFromFile("C:\\Users\\Onur Aslan\\IdeaProjects\\aufgabe04\\src\\main\\resources\\Match_History_Complete.json");
                    userClientLol(lolMongoDb);
                    break;
                case "5":
                    //Connect to redis db
                    LolRedis lolredisDatabase = new LolRedis();

                    //Delete contents of redis db
                    //lolredisDatabase.dropDatabase();

                    //Import data into redis db
                    //lolredisDatabase.importDataFromFile("C:\\Users\\Onur Aslan\\IdeaProjects\\aufgabe04\\src\\main\\resources\\lol_redis.json");

                    //Start interacting with user
                    userClientLolRedis(lolredisDatabase);
                    break;
                case "6":
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("UNGÜLTIGE EINGABE!");
                    dbClient();
            }
        }
    }


    public static void userClient(Datenbank database) {
        System.out.println();
        System.out.println("WÄHLEN SIE IHRE AKTION AUS!");
        System.out.println("1 = Mit einer PLZ (ID) einen Ort suchen");
        System.out.println("2 = Mit einem Ortsnamen eine PLZ (ID) suchen");
        System.out.println("3 = Programm beenden");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        if (scanner.hasNext()) {
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("Bitte geben Sie die PLZ (ID) ein!");
                    String id = scanner.next();
                    System.out.println(database.getCityAndStateById(id));
                    userClient(database);
                    break;
                case "2":
                    System.out.println("Bitte geben Sie den Ortsnamen ein!");
                    String cityName = scanner.next().toUpperCase();
                    List<CityData> cityList = database.getIdsByCityName(cityName);
                    for (CityData cityData : cityList) {
                        System.out.println(cityData.toString());
                    }
                    userClient(database);
                    break;
                case "3":
                    scanner.close();
                    dbClient();
                    break;
                default:
                    System.out.println("EINGABE NICHT KORREKT!");
                    userClient(database);
            }
        }
    }

    public static void userClientLol(Datenbank database) {
        System.out.println();
        System.out.println("WÄHLEN SIE IHRE AKTION AUS!");
        System.out.println("1 = Lol-Matches nach Summoner suchen");
        System.out.println("2 = Lol-Matches nach Champion suchen");
        System.out.println("3 = Lol-Matches nach Gewinn suchen");
        System.out.println("4 = Lol-Matches nach Anzahl der Kills suchen");
        System.out.println("5 = Programm beenden");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        if (scanner.hasNext()) {
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("Bitte geben Sie den Summoner ein!");
                    String summoner = scanner.nextLine().trim();
                    List<LolMatch> summonerMatches = database.getLolMatchesBySummoner(summoner);
                    printLolMatches(summonerMatches);
                    break;
                case "2":
                    System.out.println("Bitte geben Sie den Champion ein!");
                    String champion = scanner.nextLine().trim();
                    List<LolMatch> championMatches = database.getLolMatchesByChampion(champion);

                    printLolMatches(championMatches);
                    break;
                case "3":
                    System.out.println("Bitte geben Sie 'true' für gewonnene oder 'false' für verlorene Spiele ein!");
                    boolean win = scanner.nextBoolean();
                    List<LolMatch> winMatches = database.getLolMatchesByWin(win);
                    printLolMatches(winMatches);
                    break;
                case "4":
                    System.out.println("Bitte geben Sie die Mindestanzahl der Kills ein:");
                    int minKills = scanner.nextInt();
                    System.out.println("Bitte geben Sie die Höchstanzahl der Kills ein:");
                    int maxKills = scanner.nextInt();
                    List<LolMatch> killMatches = database.getLolMatchesByKills(minKills, maxKills);
                    printLolMatches(killMatches);
                    break;
                case "5":
                    scanner.close();
                    break;
                default:
                    System.out.println("EINGABE NICHT KORREKT!");
            }
        }
    }

    public static void userClientLolRedis(Datenbank database) {
        System.out.println();
        System.out.println("WÄHLEN SIE IHRE AKTION AUS!");
        System.out.println("1 = Lol-Redis Matches nach Summoner suchen");
        System.out.println("2 = Lol-Redis Matches nach Champion suchen");
        System.out.println("3 = Lol-Redis Matches nach Gewinn suchen");
        System.out.println("4 = Lol-Redis Matches nach Anzahl der Kills suchen");
        System.out.println("5 = Programm beenden");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        if (scanner.hasNext()) {
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("Bitte geben Sie den Summoner ein!");
                    String summoner = scanner.nextLine().trim();
                    List<LolMatch> summonerMatches = database.getLolMatchesBySummoner(summoner);
                    printLolMatches(summonerMatches);
                    break;
                case "2":
                    System.out.println("Bitte geben Sie den Champion ein!");
                    String champion = scanner.nextLine().trim();
                    List<LolMatch> championMatches = database.getLolMatchesByChampion(champion);
                    printLolMatches(championMatches);
                    break;
                case "3":
                    System.out.println("Bitte geben Sie 'true' für gewonnene oder 'false' für verlorene Spiele ein!");
                    boolean win = scanner.nextBoolean();
                    List<LolMatch> winMatches = database.getLolMatchesByWin(win);
                    printLolMatches(winMatches);
                    break;
                case "4":
                    System.out.println("Bitte geben Sie die Mindestanzahl der Kills ein:");
                    int minKills = scanner.nextInt();
                    System.out.println("Bitte geben Sie die Höchstanzahl der Kills ein:");
                    int maxKills = scanner.nextInt();
                    List<LolMatch> killMatches = database.getLolMatchesByKills(minKills, maxKills);
                    printLolMatches(killMatches);
                    break;
                case "5":
                    scanner.close();
                    break;
                default:
                    System.out.println("EINGABE NICHT KORREKT!");
            }
        }
    }

        private static void printLolMatches (List<LolMatch> lolMatches) {
            System.out.println("Ergebnisse:");
            for (LolMatch lolMatch : lolMatches) {
                System.out.println(lolMatch.toString());
            }
        }


    }
