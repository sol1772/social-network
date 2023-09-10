package com.getjavajob.training.maksyutovs.socialnetwork.service.storage;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public interface FileStorage<T> {

    File store(T object);

    T load(File file);

    default void writeFileToOutputStream(File file, OutputStream out) {
        try (FileInputStream fis = new FileInputStream(file)) {
            final byte[] bytes = fis.readAllBytes();
            out.write(bytes);
            out.flush();
            out.close();
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new DaoRuntimeException(e);
        }
    }

}
