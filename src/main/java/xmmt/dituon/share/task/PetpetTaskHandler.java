package xmmt.dituon.share.task;


import xmmt.dituon.share.*;
import xmmt.dituon.share.task.PetpetBlock.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class PetpetTaskHandler {


    public interface IDrawTaskHandler<T extends IDrawTask> {
        void apply(Graphics2D g2d, PetpetBlockContext sharedInfo, T task) throws IOException;
    }

    public static class DrawZoomImageHandler implements IDrawTaskHandler<DrawZoomImageTask> {
        static DrawZoomImageHandler INSTANCE = new DrawZoomImageHandler();
        @Override
        public void apply(Graphics2D g2d, PetpetBlockContext sharedInfo, DrawZoomImageTask task) throws IOException {
            BufferedImage image;
            if (task.isRound()) {
                image = ImageSynthesis.convertCircular(task.getImage(), sharedInfo.isAntialias());
            } else {
                image = task.getImage();
            }
            ImageSynthesis.g2dDrawZoomAvatar(g2d, image, task.getAnchorPos(), task.getAngle(), task.isRound());
        }

    }

    public static class DrawDeformImageHandler implements IDrawTaskHandler<DrawDeformImageTask> {
        static DrawDeformImageHandler INSTANCE = new DrawDeformImageHandler();
        @Override
        public void apply(Graphics2D g2d, PetpetBlockContext sharedInfo, DrawDeformImageTask task) {

            List<Point2D> vertexPoints = task.getVertexPosList().stream()
                    .map(pos -> new Point2D.Double(pos[0], pos[1]))
                    .collect(Collectors.toList());

            ImageSynthesis.g2dDrawDeformAvatar(g2d, task.getImage(), vertexPoints.toArray(Point2D[]::new), task.getAnchorPos());
        }
    }

    public static class DrawTextHandler implements IDrawTaskHandler<DrawTextTask> {
        static DrawTextHandler INSTANCE = new DrawTextHandler();

        @Override
        public void apply(Graphics2D g2d, PetpetBlockContext sharedInfo, DrawTextTask task) throws IOException {
            ImageSynthesis.g2dDrawText(g2d, task.getText(), task.getAnchorPos(), sharedInfo.getColor(), sharedInfo.getFont());
        }
    }

    public static class SharedInfoModifyHandler implements IDrawTaskHandler<ContextModifyTask> {
        static SharedInfoModifyHandler INSTANCE = new SharedInfoModifyHandler();

        @Override
        public void apply(Graphics2D g2d, PetpetBlockContext sharedInfo, ContextModifyTask task) throws IOException {
            if (task.getAntialias() != null) {
                sharedInfo.setAntialias(task.getAntialias());
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            if (task.getColor() != null) {
                sharedInfo.setColor(task.getColor());
            }
            if (task.getFont() != null) {
                sharedInfo.setFont(task.getFont());
            }
        }
    }

    public InputStream handle(PetpetBlock petpetBlock) throws IOException {

        if (petpetBlock.getType() == Type.GIF) {
            return makeGIF(petpetBlock);
        } else {
            BufferedImage frameImage = drawOneFrame(petpetBlock.getContext(), petpetBlock.frameBlocks.get(0));
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(frameImage, "png", os);
                return new ByteArrayInputStream(os.toByteArray());
            }
        }
    }

    public InputStream makeGIF(PetpetBlock petpetBlock) {
        try {
            GifBuilder gifBuilder = new GifBuilder(petpetBlock.getContext().getImageType(), 65, true);

            for (FrameBlock frameBlock : petpetBlock.getFrameBlocks()) {
                BufferedImage frameImage = drawOneFrame(petpetBlock.getContext(), frameBlock);
                gifBuilder.writeToSequence(frameImage);
            }

            gifBuilder.close();
            return gifBuilder.getOutput();
        } catch (IOException ex) {
            System.out.println("构造GIF失败，请检查 PetData");
            ex.printStackTrace();
        }
        return null;
    }


    private BufferedImage drawOneFrame(PetpetBlockContext sharedInfo, FrameBlock frameBlock) throws IOException {
        BufferedImage output = new BufferedImage(sharedInfo.getWidth(), sharedInfo.getHeight(), sharedInfo.getImageType());
        Graphics2D g2d = output.createGraphics();

        // TODO 研究output和g2d创建后立刻指向新的实例，是否合理
        // 背景
        if (sharedInfo.getBackgroundTask().isTransparent()) {
            output = g2d.getDeviceConfiguration().createCompatibleImage(
                    sharedInfo.getWidth(), sharedInfo.getHeight(), Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = output.createGraphics();
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, sharedInfo.getWidth(), sharedInfo.getHeight());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0F));
        }

        for (IDrawTask imageTask : frameBlock.getTasks()) {
            if (imageTask instanceof DrawZoomImageTask) {
                var implTask = (DrawZoomImageTask)imageTask;
                DrawZoomImageHandler.INSTANCE.apply(g2d, sharedInfo, implTask);
            } else if (imageTask instanceof DrawDeformImageTask) {
                var implTask = (DrawDeformImageTask)imageTask;
                DrawDeformImageHandler.INSTANCE.apply(g2d, sharedInfo, implTask);
            } else if (imageTask instanceof DrawTextTask) {
                var implTask = (DrawTextTask)imageTask;
                DrawTextHandler.INSTANCE.apply(g2d, sharedInfo, implTask);
            } else if (imageTask instanceof ContextModifyTask) {
                var implTask = (ContextModifyTask)imageTask;
                SharedInfoModifyHandler.INSTANCE.apply(g2d, sharedInfo, implTask);
            }
        }

        g2d.dispose();
        return output;
    }

}