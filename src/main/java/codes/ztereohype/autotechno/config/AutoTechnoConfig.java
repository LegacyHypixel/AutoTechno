package codes.ztereohype.autotechno.config;

import codes.ztereohype.autotechno.AutoTechno;
import com.esotericsoftware.yamlbeans.SafeYamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AutoTechnoConfig {
    private static final Path CONFIG_FILE = Paths.get("config", "autotechno.yml");

    private static final Map<String, Object> DEFAULT_CONFIG = new LinkedHashMap<String, Object>() {{
        put("SendEndMessages", true);
        put("SendStartMessages", true);
        put("SendKillMessages", true);
        put("MessageWaitTime", 3000);
        put("EndMessages", new String[]{"gg e z",
                "good game",
                "Rest in Peace Technoblade",
                "Technoblade never dies",
                "so long nerds",
                "as Sun Tzu wanted"});
        put("StartMessages", new String[]{"Good luck, and may Techno's unmatched skill be with you",
                "RIP Techno, you will be missed.",
                "Let's drop kick some children!",
                "Technoblade never dies!",
                "So, what do you guys know about anarchy?"});
        put("KillMessages", new String[]{"Blood for the Blood God",
                "In the name of techno",
                "This ones for technoblade",
                "Officer, I drop-kicked them in self defense!",
                "This is the second-worst thing to happen to these orphans.",
                "Sometimes it's tough being the best",
                "die nerd (/j)",
                "chin up king, your crown is falling"});
    }};

    private static Map<String, Object> CONFIG = new LinkedHashMap<>();

    public static Object getProperty(String property) {
        return CONFIG.get(property);
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        Map<String, Object> config = null;

        // try reading config
        if (Files.isRegularFile(CONFIG_FILE)) {
            try (YamlReader reader = new YamlReader(Files.newBufferedReader(CONFIG_FILE), new SafeYamlConfig())) {
                config = (Map<String, Object>) reader.read(Map.class, Object.class);

                // update config file keys
                if (config != null) {
                    for (Map.Entry<String, Object> entry : DEFAULT_CONFIG.entrySet()) {
                        config.putIfAbsent(entry.getKey(), entry.getValue());
                    }
                    writeConfig(config);
                }
            } catch (IOException e) {
                AutoTechno.LOGGER.error("Error reading config file.", e);
            }
        }

        // if no config, use & write default
        if (config == null) {
            config = DEFAULT_CONFIG;

            try {
                Files.createDirectories(CONFIG_FILE.getParent());
                writeConfig(config);
            } catch (IOException e) {
                AutoTechno.LOGGER.error("Error creating config file.", e);
            }
        }

        CONFIG = config;
    }

    private static void writeConfig(Map<String, Object> config) {
        try {
            Path tempFile = Files.createTempFile("autotechno", "config");

            try (YamlWriter writer = new YamlWriter(Files.newBufferedWriter(tempFile), new SafeYamlConfig())) {
                writer.write(config);
            }

            try (InputStream in = Files.newInputStream(tempFile)) {
                Files.copy(in, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            AutoTechno.LOGGER.error("Error writing config file.", e);
        }
    }
}