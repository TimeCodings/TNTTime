package de.timecode.explodechallange;

import de.timecode.explodechallange.command.PauseChallangeCMD;
import de.timecode.explodechallange.config.ConfigLoader;
import de.timecode.explodechallange.listener.ExplodeListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExplodeChallange extends JavaPlugin {

    public static Plugin pl;
    private Integer seconds = 0;
    private Integer minutes = 0;
    private Integer hours = 0;
    public Integer point = 0;
    public static boolean failed = false;
    public static boolean completed = false;
    public static boolean stopped = false;
    public static String endcounter = "";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§aDie Challange §cExplodechallange §a kann nun beginnen!");

        //TODO Enderdragon Rockets

        //Enable Instance
        pl = this;

        //Enable Commands
        getCommand("pause").setExecutor(new PauseChallangeCMD());

        //Load Config
        ConfigLoader loader = new ConfigLoader();
        if(!loader.keyExists("Chance")) {
            loader.setValue("Chance", 25);
        }
        if(!loader.keyExists("ChanceNumber")) {
            loader.setValue("ChanceNumber", 500);
        }
        if(!loader.keyExists("Cooldown")) {
            loader.setValue("Cooldown", 10);
        }

        if(!loader.keyExists("MinPlayers")) {
            loader.setValue("MinPlayers", 2);
        }

        //Send Timer

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() >= Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())) {
                    if(!stopped) {
                        seconds++;
                        if (seconds == 60) {
                            minutes++;
                            seconds = 0;
                        }
                        if (minutes == 60) {
                            minutes = 0;
                            hours++;
                        }
                    }
                        String sec = "";
                        if (seconds.toString().length() == 2) {
                            sec = seconds.toString();
                        } else {
                            sec = "0" + seconds.toString();
                        }
                        String min = "";
                        if (minutes.toString().length() == 2) {
                            min = minutes.toString();
                        } else {
                            min = "0" + minutes.toString();
                        }
                        String h = "";
                        if (hours.toString().length() == 2) {
                            h = hours.toString();
                        } else {
                            h = "0" + hours.toString();
                        }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if(completed){
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§l"+endcounter));
                        }else if(failed){
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4§l"+endcounter));
                        }else if(stopped){
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§l"+endcounter));
                        }else{
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§e§l" + h + ":" + min + ":" + sec));
                            endcounter = "" + h + ":" + min + ":" + sec;
                        }
                    }
                }else{
                    String p = "";
                    point++;
                    if(point == 0){
                        p = "";
                    }else if(point == 1){
                        p = ".";
                    }else if(point == 2){
                        p = "..";
                    }else if(point == 3){
                        p = "...";
                    }else{
                        point = 0;
                    }
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        int i = (Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())-Bukkit.getOnlinePlayers().size());
                        String s = "§a§lweitere";
                        if(i == 1){
                            s = "§a§lweiteren";
                        }
                        all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lWarte auf §c§l"+(Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())-Bukkit.getOnlinePlayers().size())+" "+s+" Spieler"+p.toString()));
                    }
                }
            }
        },0, 20);

        getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    }

}
