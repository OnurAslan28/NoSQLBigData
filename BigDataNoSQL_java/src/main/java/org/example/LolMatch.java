package org.example;

import java.util.List;

public class LolMatch {
    private final long match_id;
    private final String summoner;
    private  String account_id;
    private  String summoner_id;
    private final String champion;
    private final boolean win;
    private  String keystone;
    private String d_spell;
    private  String f_spell;
    private final int kills;
    private  int assists;
    private  int deaths;
    private double kda_ratio;
    private  long damage_dealt;
    private  int creep_score;
    private  int vision_score;
    private  boolean first_blood;
    private  int penta_kills;
    private  long gold_earned;
    private  double game_duration;
    private  List<String> items;

    public LolMatch(long match_id, String summoner, String account_id, String summoner_id, String champion, boolean win,
                    String keystone, String d_spell, String f_spell, int kills, int assists, int deaths,
                    double kda_ratio, long damage_dealt, int creep_score, int vision_score, boolean first_blood,
                    int penta_kills, long gold_earned, double game_duration, List<String> items) {
        this.match_id = match_id;
        this.summoner = summoner;
        this.account_id = account_id;
        this.summoner_id = summoner_id;
        this.champion = champion;
        this.win = win;
        this.keystone = keystone;
        this.d_spell = d_spell;
        this.f_spell = f_spell;
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.kda_ratio = kda_ratio;
        this.damage_dealt = damage_dealt;
        this.creep_score = creep_score;
        this.vision_score = vision_score;
        this.first_blood = first_blood;
        this.penta_kills = penta_kills;
        this.gold_earned = gold_earned;
        this.game_duration = game_duration;
        this.items = items;
    }

    public LolMatch(long matchId, String summoner, String champion, boolean win, int kills) {
        this.match_id = matchId;
        this.summoner = summoner;
        this.champion = champion;
        this.win = win;
        this.kills = kills;
    }


    public long getMatch_id() {
        return match_id;
    }

    public String getSummoner() {
        return summoner;
    }

    public String getAccount_id() {
        return account_id;
    }

    public String getSummoner_id() {
        return summoner_id;
    }

    public String getChampion() {
        return champion;
    }

    public boolean isWin() {
        return win;
    }

    public String getKeystone() {
        return keystone;
    }

    public String getDSpell() {
        return d_spell;
    }

    public String getFSpell() {
        return f_spell;
    }

    public int getKills() {
        return kills;
    }

    public int getAssists() {
        return assists;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getKda_ratio() {
        return kda_ratio;
    }

    public long getDamage_dealt() {
        return damage_dealt;
    }

    public int getCreep_score() {
        return creep_score;
    }

    public int getVision_score() {
        return vision_score;
    }

    public boolean isFirst_blood() {
        return first_blood;
    }

    public int getPenta_kills() {
        return penta_kills;
    }

    public long getGold_earned() {
        return gold_earned;
    }

    public double getGame_duration() {
        return game_duration;
    }

    public List<String> getItems() {
        return items;
    }
// Getter-Methoden hier einfügen

    @Override
    public String toString() {
        return "LolMatch{" +
                "matchId=" + match_id +
                ", summoner='" + summoner + '\'' +
                ", accountId='" + account_id + '\'' +
                ", summonerId='" + summoner_id + '\'' +
                ", champion='" + champion + '\'' +
                ", win=" + win +
                ", keystone='" + keystone + '\'' +
                ", dSpell='" + d_spell + '\'' +
                ", fSpell='" + f_spell + '\'' +
                ", kills=" + kills +
                ", assists=" + assists +
                ", deaths=" + deaths +
                ", kdaRatio=" + kda_ratio +
                ", damageDealt=" + damage_dealt +
                ", creepScore=" + creep_score +
                ", visionScore=" + vision_score +
                ", firstBlood=" + first_blood +
                ", pentaKills=" + penta_kills +
                ", goldEarned=" + gold_earned +
                ", gameDuration=" + game_duration +
                ", items=" + items +
                '}';
    }



}
