package xmmt.dituon.share.task.provider;

import xmmt.dituon.share.task.PetpetBlockException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class FileBaseImageProvider implements IImageProvider {

    final String baseFolder;

    public FileBaseImageProvider(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    @Override
    public BufferedImage apply(String key) throws PetpetBlockException {
        String filePath = baseFolder + File.separator + key;
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw PetpetBlockException.fromIOException(e);
        }
    }
}
