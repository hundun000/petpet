package xmmt.dituon.share.task.usage;

import kotlin.Pair;
import xmmt.dituon.share.task.*;
import xmmt.dituon.share.task.provider.FileBaseImageProvider;
import xmmt.dituon.share.task.provider.IImageProvider;
import xmmt.dituon.share.task.provider.SimpleGeometricImageProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExampleUsage {

    static final int DEFAULT_BUFFER_SIZE = 8192;

    PetpetBlockHandler handler = new PetpetBlockHandler();

    public void work(String folder) throws IOException, PetpetBlockException {
        String blockPath = folder + "/PetpetBlockDTO.json";
        String imageInputFolderPath = folder;
        String imageOutputFilePathAndNameStart = folder + "/output.";

        PetpetBlockDTO dto = PetpetBlockDataKt.decodePetpetBlockDTO(getFileStr(new File(blockPath)));
        PetpetBlock petpetBlock = PetpetBlockFactory.buildPetpetBlockFromDTO(dto);

        Map<ImageProviderType, IImageProvider> imageProviderMap = new HashMap<>();
        imageProviderMap.put(ImageProviderType.FILE_BASE_IMAGE_PROVIDER, new FileBaseImageProvider(imageInputFolderPath));
        imageProviderMap.put(ImageProviderType.SIMPLE_GEOMETRIC_IMAGE_PROVIDER, new SimpleGeometricImageProvider());

        HandlerContext handlerContext = HandlerContext.builder()
                .imageProviderMap(imageProviderMap)
                .build();

        Pair<InputStream, String> result = handler.handle(petpetBlock, handlerContext);
        copyInputStreamToFile(result.getFirst(), new File(imageOutputFilePathAndNameStart + result.getSecond()));

    }


    private String getFileStr(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        br.close();
        return sb.toString();
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) {
        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
