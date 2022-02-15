package de.timecode.explodechallange.command;

import de.timecode.explodechallange.ExplodeChallange;
import de.timecode.explodechallange.config.ConfigLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TNTTimeCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigLoader loader = new ConfigLoader();
        if (sender.hasPermission(loader.getValue("Permission").toString())) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("pause")) {
                    if(!ExplodeChallange.failed) {
                        if (ExplodeChallange.stopped) {
                            ExplodeChallange.stopped = false;
                            sender.sendMessage(loader.getStringValue("Message.UnPauseChallange"));
                        } else {
                            ExplodeChallange.stopped = true;
                            sender.sendMessage(loader.getStringValue("Message.PauseChallange"));
                        }
                    }else{
                        sender.sendMessage(loader.getStringValue("Message.AlreadyStopped"));
                    }
                }else if(args[0].equalsIgnoreCase("start")){
                    if(!ExplodeChallange.canstart && !ExplodeChallange.completed && !ExplodeChallange.failed) {
                        ExplodeChallange.canstart = true;
                        sender.sendMessage(loader.getStringValue("Message.StartChallange"));
                    }else{
                        sender.sendMessage(loader.getStringValue("Message.AlreadyStarted"));
                    }
                }else if(args[0].equalsIgnoreCase("stop")){
                        if (!ExplodeChallange.canstart || !ExplodeChallange.completed || !ExplodeChallange.failed) {
                            if(!ExplodeChallange.failed) {
                                sender.sendMessage(loader.getStringValue("Message.StopChallange"));
                                ExplodeChallange.failed = true;
                            }else{
                                sender.sendMessage(loader.getStringValue("Message.AlreadyStopped"));
                            }
                        } else {
                            sender.sendMessage(loader.getStringValue("Message.AlreadyStopped"));
                        }
                }
            }else{
                sender.sendMessage(loader.getStringValue("Message.WrongSyntax"));
            }
            } else {
                sender.sendMessage(loader.getStringValue("Message.NoPermission"));
            }
        return false;
    }
}
