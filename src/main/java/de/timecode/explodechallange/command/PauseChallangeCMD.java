package de.timecode.explodechallange.command;

import de.timecode.explodechallange.ExplodeChallange;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PauseChallangeCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp()){
            if(ExplodeChallange.stopped){
                ExplodeChallange.stopped = false;
                sender.sendMessage("§aDie Challange wurde erfolgreich reaktiviert!");
            }else{
                ExplodeChallange.stopped = true;
                sender.sendMessage("§cDie Challange wurde erfolgreich deaktiviert!");
            }
        }else{
            sender.sendMessage("§cDazu hast du keine Berechtigung!");
        }
        return false;
    }
}
