package xmmt.dituon.share.task;

import lombok.*;
import xmmt.dituon.share.Type;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PetpetBlock {

    Type type;
    ContextModifyTask initTask;
    List<FrameBlock> frameBlocks;
    PetpetBlockContext context;

    public static class PetpetTaskFactory  {


        public static PetpetBlock buildPetpetBlockFromDTO(PetpetBlockDTO dto) {

            PetpetBlock petpetBlock = PetpetBlock.builder().build();
            return petpetBlock;

        }




    }


    @Setter
    @Getter
    public static class PetpetBlockContext {
        int width;
        int height;
        int imageType;

        boolean antialias;
        Color color;
        Font font;

        DrawBackgroundTask backgroundTask;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawBackgroundTask {
        boolean transparent;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FrameBlock {
        List<IDrawTask> tasks;

    }

    public interface IDrawTask {

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ContextModifyTask implements IDrawTask {
        Boolean antialias;
        Color color;
        Font font;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawZoomImageTask implements IDrawTask {
        BufferedImage image;
        float angle;
        boolean round;
        int[] anchorPos;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawDeformImageTask implements IDrawTask {
        BufferedImage image;
        List<int[]> vertexPosList;
        int[] anchorPos;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawTextTask implements IDrawTask {
        String text;
        int[] anchorPos;

    }
}
