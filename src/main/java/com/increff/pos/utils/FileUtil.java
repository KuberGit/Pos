package com.increff.pos.utils;

import com.increff.pos.service.ApiException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

public class FileUtil {

    public static FileReader processUploadedFiles(MultipartFile file) throws ApiException {
        if(file.isEmpty()) {
            throw new ApiException("File is empty!");
        }

        try {
            byte[] bytes = file.getBytes();

            // Creating the directory to store file
            File dir = createDirectory("fileUploads");

            // Create the file on server
            File serverFile = new File(dir.getAbsolutePath() + File.separator + "upload.tsv");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();

            serverFile.createNewFile();
            FileReader serverFileReader = new FileReader(serverFile);

            return serverFileReader;
        }
        catch(Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public static void createErrorFile(List<String> errorMsg) throws ApiException {
        File dir = createDirectory("errorFiles");

        try {
            FileWriter writer = new FileWriter(dir.getAbsolutePath() + File.separator + "error.txt");
            for (String error : errorMsg) {
                writer.write(error + System.lineSeparator());
            }
            writer.close();
        }
        catch(Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public static File createDirectory(String dirName) {
        String rootPath = System.getProperty("user.dir");
        File dir = new File(rootPath + File.separator + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
