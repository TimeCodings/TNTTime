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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Random;

public class ExplodeListener implements Listener {

    public ConfigLoader loader = new ConfigLoader();
    public ArrayList<Player> cooldown = new ArrayList<>();
    public static boolean enough = false;

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        String blockperm = loader.getValue("IgnorePermission").toString();

        if(ExplodeChallange.canstart) {
            if (Bukkit.getOnlinePlayers().size() < Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString())) {
                if(Boolean.valueOf(loader.getValue("WaitForPlayers").toString()) && !p.hasPermission(blockperm)) {
                    e.setCancelled(true);
                    int i = Integer.valueOf(new ConfigLoader().getValue("MinPlayers").toString());
                    p.sendMessage(loader.getStringValue("Message.YouCanOnlyMove").replace("%missing%", String.valueOf((i-Bukkit.getOnlinePlayers().size()))).replace("%players%", String.valueOf(i)));
                }
                enough = false;
            } else {
                enough = true;
            }
        }else{
            enough = false;
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
        String blockperm = loader.getValue("IgnorePermission").toString();

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
        if(!Boolean.valueOf(loader.getValue("WaitForPlayers").toString()) && !p.hasPermission(blockperm)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
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
            all.sendMessage(loader.getStringValue("Message.Failed").replace("%name%", p.getName()).replace("%time%", ExplodeChallange.endcounter));
            ExplodeChallange.failed = true;
            all.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if(!ExplodeChallange.canstart || ExplodeChallange.stopped || ExplodeChallange.failed || ExplodeChallange.completed){
            e.setCancelled(true);
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
        String blockperm = loader.getValue("IgnorePermission").toString();
        if(!Boolean.valueOf(loader.getValue("WaitForPlayers").toString()) && !p.hasPermission(blockperm)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldEnter(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        if(Boolean.valueOf(loader.getValue("AutoTeleport").toString())) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.teleport(p);
            }
        }
    }
}
