package com.example.views;

import java.util.ArrayList;

import com.example.models.BaseEntity;
import com.example.models.Player;

//class for conecting to the player db
public class PlayersDB extends Mysql {

    /*//create the new player if he does not exist
    private void createPlayer(Player player) {
        try {
            player.setId(this.result.getInt("playerId"));
            player.setUsername(this.result.getString("username"));
        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    @Override
    public ArrayList<BaseEntity> select() {
        ArrayList<BaseEntity> players = new ArrayList<BaseEntity>();
        try {
            this.result = this.statement.executeQuery();
            while (result.next()) {
                Player player = new Player();
                create(player);
                players.add(player);
            }

            return players;
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    //create the new player if he does not exist
    @Override
    public void create(BaseEntity player) {
        try {
            ((Player)player).setId(this.result.getInt("playerId"));
            ((Player)player).setUsername(this.result.getString("username"));
        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //select all the players 
    public ArrayList<BaseEntity> selectAll() {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players");
            ArrayList<BaseEntity> players = this.select();
            return players;
        } 
        
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //select all the players by id
    public Player selectById(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players WHERE playerId = ?");
            this.statement.setInt(1, id);
            ArrayList<BaseEntity> players = this.select();
            
            return ((Player)players.get(0));
        }
        
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //select all the player info by username
    public Player selectByUsername(String username) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players WHERE username = ?");
            this.statement.setString(1, username);
            ArrayList<BaseEntity> players = this.select();
            
            return ((Player)players.get(0));
        }
        
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //insert new player to the database
    public void insert(Player player) {
        try {
            this.statement = this.connection.prepareStatement("INSERT INTO players (username) VALUES (?)");
            this.statement.setString(1, player.getUsername());
            this.inserts.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update player where id
    public void update(Player player) {
        try {
            this.statement = this.connection.prepareStatement("UPDATE players SET username = ? WHERE playerId = ?");
            this.statement.setString(1, player.getUsername());
            this.statement.setInt(2, player.getId());
            this.updates.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delete player where id
    public void delete(int id) {
        try {
            this.statement = this.connection.prepareStatement("DELETE FROM players WHERE playerId = ?");
            this.statement.setInt(1, id);
            this.deletes.add(this.statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}