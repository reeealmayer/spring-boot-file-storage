package kz.shyngys.springbootfilestorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String upload(MultipartFile file) throws IOException;

    void delete(String location);
}
