package xmmt.dituon.share.task;

import xmmt.dituon.share.task.PetpetBlock.ImageMeta;

import java.io.IOException;

public class PetpetBlockException extends Exception {
    public PetpetBlockException (String msg) {
        super(msg);
    }

    public static PetpetBlockException fromImageSupplierNotFound(ImageMeta imageMeta) {
        return new PetpetBlockException(String.format("未找到imageMeta=%s的ImageSupplier", imageMeta));
    }

    public static PetpetBlockException fromIOException(IOException e) {
        return new PetpetBlockException(String.format("PetpetBlock执行期间发生IOException：", e.getMessage()));
    }

    public static PetpetBlockException fromImageSupplierNotFound(ImageProviderType providerType, String key) {
        return new PetpetBlockException(String.format("未找到 providerType = %s 的ImageSupplier for key = %s", providerType, key));
    }
}
