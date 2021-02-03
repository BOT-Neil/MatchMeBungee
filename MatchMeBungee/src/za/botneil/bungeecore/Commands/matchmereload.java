package za.botneil.bungeecore.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import za.botneil.bungeecore.MatchME;

public class matchmereload extends Command {
    private MatchME me;
    public matchmereload(MatchME me) {
        super("gmatchmereload", "matchme.admin");
        this.me = me;
    }
    @Override
    public void execute(CommandSender commandSender, String[] string) {
        if (commandSender instanceof ProxiedPlayer) {
            if (commandSender.hasPermission("matchme.admin")){
                me.loadData();
            }else{
                ((ProxiedPlayer) commandSender).sendMessage("ur not admin");
            }
        }
    }
}
