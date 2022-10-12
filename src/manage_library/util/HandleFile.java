package manage_library.util;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HandleFile {
    public static File handleSelectAvatar(ImageView readerAvatar, Label labelClickNone) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.svg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            FileInputStream inputStream = new FileInputStream(String.valueOf(file));
            labelClickNone.setText(null);
            readerAvatar.setImage(new Image(inputStream));
            inputStream.close();
            return file;
        }
        return null;
    }
    public static String saveImage(File file, String phone, String oldFile) throws IOException {
        String newNameFile = null;
        try {
            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            newNameFile = System.currentTimeMillis() + "_" + phone + "." + extension;
            BufferedImage bImage = null;
            bImage = ImageIO.read(file);
            File newFile = new File( System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/" + newNameFile);

            ImageIO.write(bImage, extension, newFile);

            if (oldFile != null ) {
                new File(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/"+oldFile).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newNameFile;
    }
    public static String saveBookImage(File file, String phone, String oldFile) throws IOException {
        String newNameFile = null;
        try {
            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            newNameFile = System.currentTimeMillis() + "_" + phone + "." + extension;
            BufferedImage bImage = null;
            bImage = ImageIO.read(file);
            File newFile = new File( System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/" + newNameFile);

            ImageIO.write(bImage, extension, newFile);

            if (oldFile != null ) {
                new File(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/"+oldFile).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newNameFile;
    }
}
