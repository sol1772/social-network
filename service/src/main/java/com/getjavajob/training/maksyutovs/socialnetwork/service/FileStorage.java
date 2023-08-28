package com.getjavajob.training.maksyutovs.socialnetwork.service;

import java.io.File;

public interface FileStorage<T> {

    File store(T object, String extension);

    T load(File file);

}
