package kz.shyngys.springbootfilestorage.service;

import java.io.IOException;

public interface S3Service {
    String upload(String filename, String contentType, byte[] bytes) throws IOException;

    void delete(String location);
}
