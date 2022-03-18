package ru.job4j;

import java.util.Properties;

public class ImportRabbitPr {
    private Properties cfg;

    public ImportRabbitPr(Properties cfg) {
        this.cfg = cfg;
    }
    public int getPr() {
        return Integer.parseInt(cfg.getProperty("rabbit.interval"));
    }
}
