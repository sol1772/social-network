package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class AccountStorage implements FileStorage<Account> {

    @Override
    public File store(Account account, String extension) {
        File file = null;
        String tmpDir = System.getProperty("java.io.tmpdir");
        if ("xml".equals(extension)) {
            try {
                JAXBContext context = JAXBContext.newInstance(Account.class);
                Marshaller mar = context.createMarshaller();
                mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                file = new File(tmpDir + "/account.xml");
                mar.marshal(account, file);
            } catch (JAXBException e) {
                throw new DaoRuntimeException(e);
            }
        } else if ("json".equals(extension)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules();
                file = new File(tmpDir + "/account.json");
                mapper.writeValue(file, account);
            } catch (IOException e) {
                throw new DaoRuntimeException(e);
            }
        }
        return file;
    }

    public void writeFileToOutputStream(File file, OutputStream out) {
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

    @Override
    public Account load(File file) {
        Account account = null;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if ("text/xml".equals(mimeType)) {
                JAXBContext context = JAXBContext.newInstance(Account.class);
                account = (Account) context.createUnmarshaller().unmarshal(file);
            } else if ("application/json".equals(mimeType)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                account = mapper.readValue(file, Account.class);
            }
        } catch (IOException | JAXBException e) {
            throw new DaoRuntimeException(e);
        }
        return account;
    }

}
