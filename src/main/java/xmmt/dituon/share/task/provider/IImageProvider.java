package xmmt.dituon.share.task.provider;

import xmmt.dituon.share.task.PetpetBlockException;

import java.awt.image.BufferedImage;

public interface IImageProvider {
    BufferedImage apply(String key) throws PetpetBlockException;
}
