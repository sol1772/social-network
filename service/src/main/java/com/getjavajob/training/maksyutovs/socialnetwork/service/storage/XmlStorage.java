package com.getjavajob.training.maksyutovs.socialnetwork.service.storage;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XmlStorage<T> implements FileStorage<T> {

    private Class<T> tClass;

    public XmlStorage() {
    }

    public XmlStorage(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public File store(T object) {
        File file;
        String tmpDir = System.getProperty("java.io.tmpdir");
        String fileName = tClass.getSimpleName() + ".xml";
        try {
            JAXBContext context = JAXBContext.newInstance(tClass);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            file = new File(tmpDir, fileName);
            mar.marshal(object, file);
        } catch (JAXBException e) {
            throw new DaoRuntimeException(e);
        }
        return file;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T load(File file) {
        T object;
        try {
            JAXBContext context = JAXBContext.newInstance(tClass);
            object = (T) context.createUnmarshaller().unmarshal(file);
        } catch (JAXBException e) {
            throw new DaoRuntimeException(e);
        }
        return object;
    }

}
