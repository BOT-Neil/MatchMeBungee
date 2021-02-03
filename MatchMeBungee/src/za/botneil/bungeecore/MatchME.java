package za.botneil.bungeecore;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import za.botneil.bungeecore.Commands.matchme;
import za.botneil.bungeecore.Commands.matchmereload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatchME extends Plugin implements Listener {
    public Configuration config;
    public static TreeMap<String, TreeMap<ServerStatus, String>> groupMap = new TreeMap<>();
    public static List<String> motd = new LinkedList<>();
    public MatchME() throws IOException {
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel("matchme:matchme");
        getProxy().getPluginManager().registerListener(this,this);
        getLogger().info("MatchMeBungee Loaded");
        try {
            this.saveDefaultConfig();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        loadData();

        //  System.out.println("KEYS:"+this.config.getSection("motd").getKeys());
       /* for (int i = 0; i < mod2.length; i++) {
            System.out.println(cars[i]);
        }*/
        //motd.addAll(this.config.getSection("motd").getKeys().);
        //this.config.getSection("motd").getKeys().forEach((mtd)->{motd.iterator().; });
        //getProxy().getScheduler().runAsync(this, new Pinger(this));
        getProxy().getScheduler().schedule(this,new Pinger(this),0,this.config.getInt("updatespeedms"), TimeUnit.MILLISECONDS);
        getProxy().getPluginManager().registerCommand(this, new matchme(this));
        getProxy().getPluginManager().registerCommand(this, new matchmereload(this));
    }
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("matchme:matchme")) {
            ByteArrayDataInput byteArray = ByteStreams.newDataInput(event.getData());
            //System.out.println("BCore debug:");
            //matchme

            String mm = byteArray.readUTF();
            String group = byteArray.readUTF();
            String name = byteArray.readUTF();
            //getLogger().info(mm);
            //getLogger().info(group);
            //getLogger().info(name);
            this.matchMe(name,group);
        }
        //Server server = (Server) event.getSender();
        //ServerInfo info = server.getInfo();
    }
    public void loadData(){
        this.reloadConfig();
        motd.clear();
        //motd = null;
        groupMap.clear();
        //groupMap=null;
        this.config.getSection("motd").getKeys().forEach(x->{
            // System.out.println("KEYSSS:"+x);
            motd.add(x);
        });
        this.config.getSection("groups").getKeys().forEach(x->{
            groupMap.put(x, new TreeMap<>());
            this.config.getSection("groups."+x).getKeys().forEach(y ->{
                //System.out.println("x:"+x+" y:"+y);
                //System.out.println(this.config.getSection("groups."+x+"."+y).getString("ip")+this.config.getSection("groups."+x+"."+y).getInt("port"));
                groupMap.get(x).put(new ServerStatus(y, this.config.getSection("groups."+x+"."+y).getString("ip"), this.config.getSection("groups."+x+"."+y).getInt("port"),this.config.getInt("timeoutms") ), y);
            });
            //else {this.config.getSection("motd").getKeys().forEach((mtd)->{motd.add(mtd); });}
        });
    }
    public void matchMeProxiedPlayer(ProxiedPlayer pp, String servergroup){
        try {
            pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().getName()));
            getLogger().info("is open:"+groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().isOpen());
            //pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName()));
        }catch (Exception e){
            BaseComponent baseComponent = new TextComponent("No Server Available.");
            pp.sendMessage(ChatMessageType.CHAT, baseComponent);
            //pp.sendMessage("No Server Available.");
        }
    }
    public void matchMe(String player, String servergroup){
        ProxiedPlayer pp = this.getProxy().getPlayer(player);
        try {
            //todo randomize instead of sort by name
            pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().getName()));
            getLogger().info("is open:"+groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).reversed().thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().isOpen());
            //pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName()));
        }catch (Exception e){
            BaseComponent baseComponent = new TextComponent("No Server Available.");
            pp.sendMessage(ChatMessageType.CHAT, baseComponent);
            //pp.sendMessage("No Server Available.");
        }
    }
    protected Configuration getConfig() {
        return this.config;
    }
    protected void reloadConfig() {
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException var4) {
            throw new RuntimeException("Unable to load configuration", var4);
        }
    }

    private void saveDefaultConfig() throws Throwable {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Throwable var2 = null;
                Object var3 = null;

                try {
                    InputStream is = this.getResourceAsStream("config.yml");

                    try {
                        FileOutputStream os = new FileOutputStream(configFile);

                        try {
                            ByteStreams.copy(is, os);
                        } finally {
                            if (os != null) {
                                os.close();
                            }

                        }
                    } catch (Throwable var19) {
                        if (var2 == null) {
                            var2 = var19;
                        } else if (var2 != var19) {
                            var2.addSuppressed(var19);
                        }

                        if (is != null) {
                            is.close();
                        }

                        throw var2;
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable var20) {
                    if (var2 == null) {
                        var2 = var20;
                    } else if (var2 != var20) {
                        var2.addSuppressed(var20);
                    }

                    throw var2;
                }
            } catch (IOException var21) {
                throw new RuntimeException("Unable to create configuration file", var21);
            }
        }

    }
}
