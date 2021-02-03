package za.botneil.bungeecore.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import za.botneil.bungeecore.MatchME;

import java.io.IOException;
import java.util.Arrays;

public class matchme extends Command {
    private MatchME me;
    public matchme(MatchME matchME) {
        super("gmatchme", "matchme.matchme");
        me = matchME;
    }

    @Override
    public void execute(CommandSender commandSender, String[] string) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender; // you cast with (<type>) <object>
            try {
                me.matchMeProxiedPlayer(player, Arrays.stream(string).skip(0).iterator().next());
            }catch (Exception e){
                System.out.println(e);
            }

        }
        /*try {
            me.matchMe(commandSender.getName(), Arrays.stream(string).skip(0).iterator().next());
        }catch (Exception e){
            System.out.println(e);
        }*/

    }
}
