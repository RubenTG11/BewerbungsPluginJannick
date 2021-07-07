package de.ruben.bewerbungsplugin.util;

import com.mojang.authlib.GameProfile;
import de.ruben.bewerbungsplugin.BewerbungsPlugin;
import de.ruben.bewerbungsplugin.object.TeamGroup;
import de.ruben.bewerbungsplugin.object.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamPlayerProvider {

    private BewerbungsPlugin plugin;

    private List<TeamPlayer> teamPlayers;

    private Player player;

    public TeamPlayerProvider(BewerbungsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.teamPlayers = plugin.getTeamPlayers();
    }

    public TeamPlayerProvider(BewerbungsPlugin plugin){
        this.plugin = plugin;
        this.teamPlayers = plugin.getTeamPlayers();
        this.player = null;
    }


    public void addTeamPlayer(TeamGroup teamGroup){
        System.out.println("Log > Spieler "+player.getName()+" ("+player.getUniqueId().toString()+") wurde zur Teamliste hinzugefügt!");
        teamPlayers.add(new TeamPlayer(player.getUniqueId().toString(), teamGroup));
    }

    public void removeTeamPlayer(Player player){
        System.out.println("Log > Spieler "+player.getName()+" ("+player.getUniqueId().toString()+") wurde zur Teamliste entfernt!");
        teamPlayers.stream().filter(teamPlayer -> teamPlayer.getUuid().equals(player.getUniqueId().toString())).forEach(teamPlayer -> teamPlayers.remove(teamPlayer));
    }

    public TeamPlayer getTeamPlayer(Player player){
        Optional<TeamPlayer> teamPlayerOptional = teamPlayers.stream().filter(teamPlayer -> teamPlayer.getUuid().equals(player.getUniqueId().toString())).findFirst();
        return teamPlayerOptional.isPresent() ? teamPlayerOptional.get() : null;
    }

    public boolean isTeamPlayer(Player player){
        return teamPlayers.stream().anyMatch(teamPlayer -> teamPlayer.getUuid().equals(player.getUniqueId()));
    }

    public ItemStack[] getSortedTeamPlayersConent(){
        List<TeamPlayer> teamPlayers = getSortedTeamPlayers();

        List<ItemStack> contents = new ArrayList<>();

        teamPlayers.forEach(teamPlayer -> contents.add(getSkull(teamPlayer)));

        return contents.toArray(new ItemStack[contents.size()]);
    }

    public List<TeamPlayer> getSortedTeamPlayers(){
        return teamPlayers.stream().sorted((o1, o2) ->
            o2.getTeamGroup().getValue().compareTo(o1.getTeamGroup().getValue())
        ).collect(Collectors.toList());
    }

    private ItemStack getSkull(TeamPlayer teamPlayer){
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

        try {
            ItemMeta stackMeta = itemStack.getItemMeta();
            GameProfile gameProfile = new GameProfile(UUID.fromString(teamPlayer.getUuid()), Bukkit.getOfflinePlayer(UUID.fromString(teamPlayer.getUuid())).getName());
            Field profileField = stackMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(stackMeta, gameProfile);

            stackMeta.setDisplayName(gameProfile.getName());

            itemStack.setItemMeta(stackMeta);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return itemStack;

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
