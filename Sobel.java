import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sobel {

    static int[] filter_x = {-1, 0, 1, 
                            -2, 0, 2, 
                            -1, 0, 1};

    static int[] filter_y = {1, 2, 1, 
                             0, 0, 0, 
                            -1, -2, -1};

    private static BufferedImage sobel(BufferedImage edgesX, BufferedImage edgesY) {
        BufferedImage result = new BufferedImage(edgesX.getWidth(), edgesX.getHeight(), BufferedImage.TYPE_INT_RGB);
        int height = result.getHeight();
        int width = result.getWidth();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int tmp = Math.abs(new Color(edgesX.getRGB(x, y)).getRed()) + 
                          Math.abs(new Color(edgesY.getRGB(x, y)).getRed());
                
                tmp = Math.min(tmp, 255); 
                int rgb = ((tmp & 0xff) << 16) | ((tmp & 0xff) << 8) | (tmp & 0xff);
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }

    private static BufferedImage edgeDetector(BufferedImage img, int[] kernel) {
        int height = img.getHeight();
        int width = img.getWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int[] tmp = {
                    new Color(img.getRGB(x - 1, y - 1)).getRed(), new Color(img.getRGB(x, y - 1)).getRed(),
                    new Color(img.getRGB(x + 1, y - 1)).getRed(), new Color(img.getRGB(x - 1, y)).getRed(),
                    new Color(img.getRGB(x, y)).getRed(), new Color(img.getRGB(x + 1, y)).getRed(),
                    new Color(img.getRGB(x - 1, y + 1)).getRed(), new Color(img.getRGB(x, y + 1)).getRed(),
                    new Color(img.getRGB(x + 1, y + 1)).getRed()
                };

                int value = convolution(kernel, tmp);
                int rgb = ((value & 0xff) << 16) | ((value & 0xff) << 8) | (value & 0xff);
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }

    private static int convolution(int[] kernel, int[] pixel) {
        int result = 0;

        for (int i = 0; i < pixel.length; i++) {
            result += kernel[i] * pixel[i];
        }
        return Math.abs(result) / 9;
    }

    private static BufferedImage greyscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color mycolor = new Color(img.getRGB(x, y));
                int r = mycolor.getRed();
                int g = mycolor.getGreen();
                int b = mycolor.getBlue();
                int rgb = ((g&0x0ff) << 16) | ((g&0x0ff) << 8) | (g&0x0ff);
                img.setRGB(x, y, rgb);
            }
        }
        return img;
    }

    public static void main(String[] args) {
        BufferedImage img = null, edgesX = null, edgesY = null, sobelResult = null;
        try {
            img = ImageIO.read(new File("dubs.jpg"));
        } catch (IOException e) {
            System.out.println("Read in image error...");
            return;
        }


        BufferedImage greyImage = greyscale(img);
        try {
            File outputfile = new File("greyscale.png");
            ImageIO.write(greyImage, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Exception in saving...");
        }


        edgesX = edgeDetector(greyImage, filter_x);
        try {
            File outputfile = new File("edgesX.png");
            ImageIO.write(edgesX, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Exception in saving...");
        }


        edgesY = edgeDetector(greyImage, filter_y);
        try {
            File outputfile = new File("edgesY.png");
            ImageIO.write(edgesY, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Exception in saving...");
        }


        sobelResult = sobel(edgesX, edgesY);
        try {
            File outputfile = new File("sobel.png");
            ImageIO.write(sobelResult, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Exception in saving...");
        }
    }
}
