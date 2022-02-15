package de.timecode.explodechallange.listener;

import de.timecode.explodechallange.ExplodeChallange;
import de.timecode.explodechallange.config.ConfigLoader;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Random;

public class ExplodeListener implements Listener {

    public ConfigLoader loader = new ConfigLoader();
    public ArrayList<Player> cooldown = new ArrayList<>();
    public boolean enough = false;

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(Bukkit.getOnlinePlayers().size() < Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())){
            e.setCancelled(true);
            p.sendMessage("§7Du kannst dich erst bewegen wenn §c"+Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())+" §7Spieler den Server betreten haben!");
            enough = false;
        }else{
            enough = true;
        }

        if(enough && !ExplodeChallange.failed && !ExplodeChallange.completed && !ExplodeChallange.stopped) {
            if (loader.getClearLocation(p) != null && !cooldown.contains(p)) {
                Location pl = p.getLocation();
                Location ll = loader.getClearLocation(p);
                if (!(pl.getBlockX() == ll.getBlockX() && (pl.getBlockY() - 1) == ll.getBlockY() && pl.getBlockZ() == ll.getBlockZ())) {
                    //Start generator and blow up tnt
                    Random r = new Random();
                    Integer rn = r.nextInt(Integer.valueOf(loader.getValue("ChanceNumber").toString()));
                    if (rn <= Integer.valueOf(loader.getValue("Chance").toString())) {
                        Integer chance = Integer.valueOf(loader.getValue("Chance").toString());
                        ll.getBlock().getWorld().spawn(pl, TNTPrimed.class);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (ll.getBlock().getLocation().distance(all.getLocation()) <= 5) {
                                all.playSound(all, Sound.ENTITY_TNT_PRIMED, 2, 2);
                            }
                        }
                    }
                    Integer cd = Integer.valueOf(loader.getValue("Cooldown").toString());
                    if (cd > 0) {
                        if(!cooldown.contains(p)) {
                            cooldown.add(p);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ExplodeChallange.pl, new Runnable() {
                            @Override
                            public void run() {
                                cooldown.remove(p);
                            }
                        }, cd * 20);
                    }
                }
            } else {
                loader.setLocation(p);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        //Start generator and blow up tnt
        if(enough && !ExplodeChallange.failed && !ExplodeChallange.completed && !ExplodeChallange.stopped) {
            Location ll = e.getBlock().getLocation();
            Random r = new Random();
            if (!cooldown.contains(p)) {
                Integer rn = r.nextInt(Integer.valueOf(loader.getValue("ChanceNumber").toString()));
                if (rn <= Integer.valueOf(loader.getValue("Chance").toString())) {
                    Integer chance = Integer.valueOf(loader.getValue("Chance").toString());
                    e.setCancelled(true);
                    ll.getBlock().setType(Material.AIR);
                    ll.getBlock().getWorld().spawn(ll.getBlock().getLocation(), TNTPrimed.class);
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (ll.getBlock().getLocation().distance(all.getLocation()) <= 5) {
                            all.playSound(all, Sound.ENTITY_TNT_PRIMED, 2, 2);
                        }
                    }
                }
                Integer cd = Integer.valueOf(loader.getValue("Cooldown").toString());
                if (cd > 0) {
                    if(!cooldown.contains(p)) {
                        cooldown.add(p);
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ExplodeChallange.pl, new Runnable() {
                        @Override
                        public void run() {
                            cooldown.remove(p);
                        }
                    }, cd * 20);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().hasPlayedBefore()){
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }else{
            Bukkit.getScheduler().scheduleSyncDelayedTask(ExplodeChallange.pl, new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
                }
            }, 2);
        }
        e.setJoinMessage("§7[§a+§7] §f"+e.getPlayer().getName());
        if(Bukkit.getOnlinePlayers().size() < Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())){
            enough = false;
        }else{
            enough = true;
            for(Player all : Bukkit.getOnlinePlayers()){
                all.playSound(all, Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                all.getInventory().clear();
                all.setGameMode(GameMode.SURVIVAL);
                all.setHealth(20.0);
                all.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        e.setDeathMessage("");
        for(Player all : Bukkit.getOnlinePlayers()){
            all.playSound(all.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4,4);
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("§7Ihr habt die Challange verkackt! \n§7Bedankt euch bei §b§l"+p.getName()+" §7den Minecraft 'Profi'! \n§cServer schließt... Viel Glück beim nächsten Mal! \n§c§lZeit: "+ExplodeChallange.endcounter);
            ExplodeChallange.failed = true;
            all.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage("§7[§c-§7] §f"+e.getPlayer().getName());
        if(Bukkit.getOnlinePlayers().size() < Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())){
            enough = false;
        }else{
            enough = true;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        //Start generator and blow up tnt
        Location ll = e.getBlock().getLocation();
        Random r = new Random();
        if(enough && !ExplodeChallange.failed && !ExplodeChallange.completed && !ExplodeChallange.stopped){
        if(!cooldown.contains(p)) {
            Integer rn = r.nextInt(Integer.valueOf(loader.getValue("ChanceNumber").toString()));
            if (rn <= Integer.valueOf(loader.getValue("Chance").toString())) {
                Integer chance = Integer.valueOf(loader.getValue("Chance").toString());
                e.setCancelled(true);
                ll.getBlock().setType(Material.AIR);
                ll.getBlock().getWorld().spawn(ll.getBlock().getLocation(), TNTPrimed.class);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (ll.getBlock().getLocation().distance(all.getLocation()) <= 5) {
                        all.playSound(all, Sound.ENTITY_TNT_PRIMED, 2, 2);
                    }
                }
            }
            Integer cd = Integer.valueOf(loader.getValue("Cooldown").toString());
            if (cd > 0) {
                if(!cooldown.contains(p)) {
                    cooldown.add(p);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(ExplodeChallange.pl, new Runnable() {
                    @Override
                    public void run() {
                        cooldown.remove(p);
                    }
                }, cd * 20);
            }
        }
        }
    }

    @EventHandler
    public void onWorldEnter(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        for(Player all : Bukkit.getOnlinePlayers()){
            all.teleport(p);
        }
    }
}
