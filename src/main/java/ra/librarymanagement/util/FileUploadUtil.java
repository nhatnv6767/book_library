package ra.librarymanagement.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public static String saveFile(MultipartFile file, String subDirectory) throws IOException {
        // Create directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR + subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // create file name if not exists
        // unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // thats mean we will save file in uploads/subDirectory/fileName
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subDirectory + "/" + fileName;
        } catch (IOException e) {
            throw new IOException("Could not save file: " + fileName, e);
        }

    }

    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
