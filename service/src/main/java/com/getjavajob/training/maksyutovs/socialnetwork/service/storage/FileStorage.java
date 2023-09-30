package com.getjavajob.training.maksyutovs.socialnetwork.service.storage;

import java.io.File;

public interface FileStorage<T> {

    File store(T object);

    T load(File file);

}
