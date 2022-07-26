package me.lunaluna.find.fabric.config;

import java.awt.*;

public record Config(
        Color color
) {
    public static final Config INSTANCE = new Config(
            new Color(0x77000000, true)
    );
}