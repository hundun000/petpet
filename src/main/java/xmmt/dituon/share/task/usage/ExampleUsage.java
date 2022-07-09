package xmmt.dituon.share.task.usage;

import kotlin.Pair;
import xmmt.dituon.share.task.*;
import xmmt.dituon.share.task.PetpetBlock.RuntimeContext;
import xmmt.dituon.share.task.provider.FileBaseImageProvider;
import xmmt.dituon.share.task.provider.IImageProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExampleUsage {

    static final int DEFAULT_BUFFER_SIZE = 8192;

    PetpetBlockHandler handler = new PetpetBlockHandler();

    public void work() throws IOException, PetpetBlockException {
        String blockPath = "./example-data/petpetBlock/PetpetBlockDTO.json";
        String imageInputFolderPath = "./example-data/petpetBlock/input";
        String imageOutputFilePathAndNameStart = "./example-data/petpetBlock/output.";

        PetpetBlockDTO dto = PetpetBlockDataKt.decodePetpetBlockDTO(getFileStr(new File(blockPath)));
        PetpetBlock petpetBlock = PetpetBlockFactory.buildPetpetBlockFromDTO(dto);

        Map<ImageProviderType, IImageProvider> imageProviderMap = new HashMap<>();
        imageProviderMap.put(ImageProviderType.FILE_BASE_IMAGE_PROVIDER, new FileBaseImageProvider(imageInputFolderPath));

        RuntimeContext runtimeContext = RuntimeContext.builder()
                .imageProviderMap(imageProviderMap)
                .build();

        Pair<InputStream, String> result = handler.handle(petpetBlock, runtimeContext);
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

    public static void main(String[] args) {
        ExampleUsage exampleUsage = new ExampleUsage();
        try {
            exampleUsage.work();
        } catch (IOException | PetpetBlockException e) {
            e.printStackTrace();
        }
    }

}
