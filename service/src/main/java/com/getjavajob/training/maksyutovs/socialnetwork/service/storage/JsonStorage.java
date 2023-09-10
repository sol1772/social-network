package com.getjavajob.training.maksyutovs.socialnetwork.service.storage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;

import java.io.File;
import java.io.IOException;

public class JsonStorage<T> implements FileStorage<T> {

    private Class<T> tClass;

    public JsonStorage() {
    }

    public JsonStorage(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public File store(T object) {
        File file;
        String tmpDir = System.getProperty("java.io.tmpdir");
        String fileName = tClass.getSimpleName() + ".json";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            file = new File(tmpDir, fileName);
            mapper.writeValue(file, object);
        } catch (IOException e) {
            throw new DaoRuntimeException(e);
        }
        return file;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T load(File file) {
        T object;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            object = (T) mapper.readValue(file, Account.class);
        } catch (IOException e) {
            throw new DaoRuntimeException(e);
        }
        return object;
    }

}
