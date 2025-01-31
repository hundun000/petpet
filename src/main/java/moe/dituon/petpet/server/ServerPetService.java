package moe.dituon.petpet.server;

import moe.dituon.petpet.share.BasePetService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerPetService extends BasePetService {
    public int port = 2333;
    public String path = "data/xmmt.dituon.petpet";
    public int webServerThreadPoolSize = 10;
    public boolean headless = true;
    private String indexJson;

    public void readConfig() {
        File configFile = new File("config.json");
        try {
            if (!configFile.exists()) { //save default config
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.json");
                assert is != null;
                Files.write(Paths.get("config.json"), is.readAllBytes());
            }

            ServerConfig config = ServerConfig.getConfig(getFileStr(configFile));
            port = config.getPort();
            path = config.getDataPath();
            webServerThreadPoolSize = config.getWebServerThreadPoolSize();
            headless = config.getHeadless();

            super.setGifMaxSize(config.getGifMaxSize());
            super.quality = config.getGifQuality();
            super.encoder = config.getGifEncoder();

            super.setGifMakerThreadPoolSize(config.getGifMakerThreadPoolSize());
            System.out.println("GifMakerThreadPoolSize: " + super.getGifMakerThreadPoolSize());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void readData(File[] files) {
        super.readData(files);
        indexJson = PetDataDTO.encodeToString(super.dataMap);
        System.out.println(keyListString);
    }

    public String getIndexJson() {
        return indexJson;
    }
}
