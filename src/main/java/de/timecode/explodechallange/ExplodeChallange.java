package de.timecode.explodechallange;

import de.timecode.explodechallange.command.TNTTimeCMD;
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
    public static boolean canstart = false;
    public static String endcounter = "";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§aDie Challange §cExplodechallange §a kann nun beginnen!");

        //Enable Instance
        pl = this;

        //Enable Commands
        getCommand("tnttime").setExecutor(new TNTTimeCMD());

        //Load Config
        ConfigLoader loader = new ConfigLoader();
        loader.addDefault("Permission", "tnttime.use");
        loader.addDefault("IgnorePermission", "tnttime.ignore");
        loader.addDefault("Chance", 25);
        loader.addDefault("ChanceNumber", 500);
        loader.addDefault("Cooldown", 10);
        loader.addDefault("MinPlayers", 2);
        loader.addDefault("WaitForPlayers", true);
        loader.addDefault("AutoStart", true);
        loader.addDefault("AutoTeleport", true);

        loader.addDefault("Message.UnPauseChallange", "&cYou canceled the break!");
        loader.addDefault("Message.PauseChallange", "&4You activated pause mode!");
        loader.addDefault("Message.StartChallange", "&a&lYou've started the TNTTime Challange!");
        loader.addDefault("Message.AlreadyStarted", "&cThe game is already running!");
        loader.addDefault("Message.StopChallange", "&4You've stopped the challange!");
        loader.addDefault("Message.AlreadyStopped", "&cThe game is already stopped!");
        loader.addDefault("Message.WrongSyntax", "&cTry /tnttime help");
        loader.addDefault("Message.NoPermission", "&cYou do not have permission to do this!");
        loader.addDefault("Message.YouCanOnlyMove", "&7You can only move if &c%missing% &7people joined the game!");
        loader.addDefault("Message.Failed", "&f&lYou failed the challange! &7(%name% died) &c&lIt took %time%");

        //Send Timer

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(canstart) {
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
