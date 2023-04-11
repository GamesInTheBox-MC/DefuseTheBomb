package me.hsgamer.gamesinthebox.defusethebomb;

import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public interface MessageConfig {
    @ConfigPath("display-name")
    default String getDisplayName() {
        return "Defuse The Bomb";
    }

    @ConfigPath("default-hologram-lines")
    default Map<String, Object> getDefaultHologramLines() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("description", Arrays.asList(
                "&c&lDEFUSE THE BOMB",
                "&fBomb will drop from the sky",
                "&fYou need to defuse it to get points",
                "&fThe player with the most points will win"
        ));
        map.put("points", Arrays.asList(
                "&fPoints when you defuse the bomb: &a{game_point_plus}",
                "&fPoints when a bomb explodes near you: &c{game_point_minus}"
        ));
        map.put("top", Arrays.asList(
                "&a#1 &f{game_top_name_1} &7- &f{game_top_value_1}",
                "&a#2 &f{game_top_name_2} &7- &f{game_top_value_2}",
                "&a#3 &f{game_top_name_3} &7- &f{game_top_value_3}",
                "&a#4 &f{game_top_name_4} &7- &f{game_top_value_4}",
                "&a#5 &f{game_top_name_5} &7- &f{game_top_value_5}"
        ));
        map.put("status", Arrays.asList(
                "&fStatus: &a{planner_game_state}",
                "&fTime left: &a{game_time_left}"
        ));
        return map;
    }

    void reloadConfig();
}
