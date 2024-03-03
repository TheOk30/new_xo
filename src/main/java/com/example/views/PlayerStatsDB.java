package com.example.views;

import java.util.ArrayList;

import com.example.models.BaseEntity;
import com.example.models.Stats;

public class PlayerStatsDB extends Mysql {

    @Override
    public ArrayList<BaseEntity> select() {
        ArrayList<BaseEntity> stats = new ArrayList<BaseEntity>();
        try {
            this.result = this.statement.executeQuery();
            while (result.next()) {
                Stats stat = new Stats();
                create(stat);
                stats.add(stat);
            }

            return stats;
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    //create the new stats if he does not exist
    @Override
    public void create(BaseEntity stats) {
        try {
            ((Stats)stats).setId(this.result.getInt("playerId"));
            ((Stats)stats).setWins(this.result.getInt("wins"));
            ((Stats)stats).setLosses(this.result.getInt("losses"));
            ((Stats)stats).setDraws(this.result.getInt("draws"));
        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //select all the players 
    public ArrayList<BaseEntity> selectAll() {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM playerStats");
            ArrayList<BaseEntity> stats = this.select();
            return stats;
        } 
        
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } 
    
    //Get Stats by playerID
    public Stats selectByPlayerId(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM playerStats WHERE playerId = ?");
            this.statement.setInt(1, id);
            ArrayList<BaseEntity> stats = this.select();
            
            return ((Stats)stats.get(0));
        } 
        
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //insert stats into stats table by player id
    public void insert(Stats stats) {
        try {
            this.statement = this.connection
                    .prepareStatement("INSERT INTO playerStats (playerId, wins, losses, draws) VALUES (?, ?, ?, ?)");
            this.statement.setInt(1, stats.getId());
            this.statement.setInt(2, stats.getWins());
            this.statement.setInt(3, stats.getLosses());
            this.statement.setInt(4, stats.getDraws());
            this.inserts.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update stats into stats table by player id
    public void update(Stats stats) {
        try {
            this.statement = this.connection
                    .prepareStatement("UPDATE playerStats SET wins = ?, losses = ?, draws = ? WHERE playerId = ?");
            this.statement.setInt(1, stats.getWins());
            this.statement.setInt(2, stats.getLosses());
            this.statement.setInt(3, stats.getDraws());
            this.statement.setInt(4, stats.getId());
            this.updates.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete stats from stats table by player id
    public void delete(Stats stats) {
        try {
            this.statement = this.connection.prepareStatement("DELETE FROM playerStats WHERE playerId = ?");
            this.statement.setInt(1, stats.getId());
            this.deletes.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}