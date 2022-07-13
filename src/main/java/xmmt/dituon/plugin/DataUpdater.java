package xmmt.dituon.plugin;

import xmmt.dituon.share.BasePetService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataUpdater {
    final static String repositoryUrl = "https://dituon.github.io/petpet/";

    public static void autoUpdate() {
        if (!checkUpdate()) updateData();
    }

    public static void updateData() {
        System.out.println("开始更新PetData");
        UpdateIndex index = UpdateIndex.getUpdate(Objects.requireNonNull(getUrlText(repositoryUrl + "index.json")));
        List<String> newPetList = index.getDataList();
        for (String pet : newPetList) {
            if (Petpet.pluginPetService.getDataMap().containsKey(pet)) continue;
            String petDataPath = "data/xmmt.dituon.petpet/" + pet;
            if (!saveAs(petDataPath, "data.json")) {
                System.out.println("无法从远程仓库下载PetData: " + petDataPath);
                break;
            }
            short i = 0;
            while (saveAs(petDataPath, i + ".png")) i++;
            System.out.println("PetData/" + pet + "下载成功 (length:" + i + ')');
        }

        String fontsPath = "data/xmmt.dituon.petpet/" + BasePetService.FONTS_FOLDER;

        List<String> localFonts = Arrays.stream(Objects.requireNonNull(new File(fontsPath).listFiles()))
                .map(File::getName).distinct().collect(Collectors.toList());
        index.getFontList().forEach(font -> {
            if (localFonts.contains(font)) return;
            if (!saveAs(fontsPath, font)) {
                System.out.println("无法从远程仓库下载PetFont: " + fontsPath);
                return;
            }
            System.out.println("PetFont/" + font + "下载成功");
        });

        System.out.println("PetData更新完毕, 请重启Mirai");
    }

    public static boolean checkUpdate() {
        UpdateIndex update = UpdateIndex.getUpdate(Objects.requireNonNull(getUrlText(repositoryUrl + "index.json")));
        if (Petpet.VERSION != update.getVersion()) System.out.println("PetpetPlugin可更新到最新版本: " + update.getVersion());
        for (String pet : update.getDataList()) {
            if (Petpet.pluginPetService.getDataMap().containsKey(pet)) continue;
            System.out.println("发现新增PetData");
            return false;
        }
        return true;
    }

    private static String getUrlText(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) str.append(line);
            reader.close();
            connection.disconnect();
            return str.toString();
        } catch (IOException ignored) {
            System.out.println("无法连接到远程资源: " + url);
            return null;
        }
    }

    private static boolean saveAs(String path, String fileName) {
        try (InputStream ins = new URL(repositoryUrl + path + '/' + fileName).openStream()) {
            Path target = Paths.get(path, fileName);
            Files.createDirectories(target.getParent());
            Files.copy(ins, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}
