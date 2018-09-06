package com.lawulu.crawler.model;


public class Stats {
    Double homePossession;
    Double awayPossession;
    String homeShots;
    String awayShots;


    public Double getHomePossession() {
        return homePossession;
    }

    public void setHomePossession(Double homePossession) {
        this.homePossession = homePossession;
    }

    public Double getAwayPossession() {
        return awayPossession;
    }

    public void setAwayPossession(Double awayPossession) {
        this.awayPossession = awayPossession;
    }

    public String getHomeShots() {
        return homeShots;
    }

    public void setHomeShots(String homeShots) {
        this.homeShots = homeShots;
    }

    public String getAwayShots() {
        return awayShots;
    }

    public void setAwayShots(String awayShots) {
        this.awayShots = awayShots;
    }

    public String getHomeGates() {
        return homeGates;
    }

    public void setHomeGates(String homeGates) {
        this.homeGates = homeGates;
    }

    public String getAwayGates() {
        return awayGates;
    }

    public void setAwayGates(String awayGates) {
        this.awayGates = awayGates;
    }

    public String getHomePasses() {
        return homePasses;
    }

    public void setHomePasses(String homePasses) {
        this.homePasses = homePasses;
    }

    public String getAwayPasses() {
        return awayPasses;
    }

    public void setAwayPasses(String awayPasses) {
        this.awayPasses = awayPasses;
    }

    String homeGates;
    String awayGates;
    String homePasses ;
    String awayPasses ;


    @Override
    public String toString() {
        return "Stats{" +
                "homePossession=" + homePossession +
                ", awayPossession=" + awayPossession +
                ", homeShots='" + homeShots + '\'' +
                ", awayShots='" + awayShots + '\'' +
                ", homeGates='" + homeGates + '\'' +
                ", awayGates='" + awayGates + '\'' +
                ", homePasses='" + homePasses + '\'' +
                ", awayPasses='" + awayPasses + '\'' +
                '}';
    }
}
