package de.timecode.explodechallange.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigLoader {

    private File file = new File("plugins//Challenge", "explodesettings.yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    private HashMap<Player, Location> locs = new HashMap<>();

    public ConfigLoader(){
        cfg.options().header("ChanceNumber = The Main Chance for a TNT drop \n Chance = Chance that the block turns into a TNT-Block (1-100%) \n Cooldown = Set 0 to disable the cooldown (Cooldown that the block turns into TNT)");
        cfg.options().copyDefaults(true);
        saveFile();
        //If I need plugin instance or something like this
    }

    public ArrayList<Location> getLocations(){
        ArrayList<Location> list = new ArrayList<>();
        for(Location loc : locs.values()){
            loc.subtract(0,1,0);
            list.add(loc);
        }
        return list;
    }

    public Location getClearLocation(Player p){
        if(locs.containsKey(p)){
            Location edited = locs.get(p);
            edited.subtract(0,1,0);
            return edited;
        }
        return null;
    }

    public void setLocation(Player p){
        locs.put(p, p.getLocation());
    }

    public boolean keyExists(String key){
        if(cfg.get(key) != null){
            return true;
        }
        return false;
    }

    public void setValue(String key, Object value){
        cfg.set(key, value);
        saveFile();
    }

    public Object getValue(String key){
        if(keyExists(key)){
            return cfg.get(key);
        }
        return null;
    }

    public void saveFile(){
        //Save file
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
