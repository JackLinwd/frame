package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Component("frame.util.image")
public class ImageImpl implements Image {

    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;

    @Override
    public BufferedImage read(byte[] bytes) throws IOException {
        return read(new ByteArrayInputStream(bytes));
    }

    @Override
    public BufferedImage read(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        return image;
    }

    @Override
    public BufferedImage thumbnail(BufferedImage image, int maxWidth, int maxHeight) {
        if (image == null || maxWidth <= 0 || maxHeight <= 0)
            return image;

        int width = image.getWidth();
        int height = image.getHeight();
        if (width > maxWidth) {
            height = height * maxWidth / width;
            width = maxWidth;
        }
        if (height > maxHeight) {
            width = width * maxHeight / height;
            height = maxHeight;
        }
        if (width <= 0 || height <= 0)
            return image;

        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        thumbnail.getGraphics().drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0, 0, width, height, null);

        return thumbnail;
    }

    @Override
    public BufferedImage subimage(BufferedImage image, int x, int y, int width, int height) {
        if (x < 0 || y < 0 || width <= 0 || height <= 0)
            return image;

        int w = image.getWidth();
        int h = image.getHeight();
        if (x >= w || y >= h || (x == 0 && y == 0 && width >= w && height >= h))
            return image;

        return image.getSubimage(x, y, Math.min(width, w - x), Math.min(height, h - y));
    }

    @Override
    public BufferedImage scale(BufferedImage image, double scale) {
        return image == null || scale <= 0 || scale == 1.0D ? image :
                scale(image, numeric.toInt(image.getWidth() * scale), numeric.toInt(image.getHeight() * scale));
    }

    @Override
    public BufferedImage scale(BufferedImage image, int width, int height) {
        if (image == null || width <= 0 || height <= 0)
            return image;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0, 0, width, height, null);

        return bufferedImage;
    }

    @Override
    public byte[] write(BufferedImage image, Format format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(image, format, outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public void write(BufferedImage image, Format format, OutputStream outputStream) throws IOException {
        ImageIO.write(image, toString(format), outputStream);
        outputStream.close();
    }

    private String toString(Format format) {
        switch (format) {
            case Jpeg:
                return "JPEG";
            case Gif:
                return "GIF";
            default:
                return "PNG";
        }
    }

    @Override
    public Format formatFromContentType(String contentType) {
        switch (contentType) {
            case "image/jpeg":
            case "image/jpg":
                return Format.Jpeg;
            case "image/gif":
                return Format.Gif;
            default:
                return Format.Png;
        }
    }

    @Override
    public boolean is(String contentType, String name) {
        int indexOf;
        if (validator.isEmpty(contentType) || validator.isEmpty(name) || !contentType.startsWith("image/")
                || (indexOf = name.lastIndexOf('.')) == -1)
            return false;

        String suffix = name.substring(indexOf);
        return ((contentType.equals("image/jpeg") && (suffix.equals(".jpg") || suffix.equals(".jpeg"))) ||
                (contentType.equals("image/png") && suffix.equals(".png"))
                || (contentType.equals("image/gif") && suffix.equals(".gif")));
    }
}