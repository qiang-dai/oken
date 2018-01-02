package com.javahash.spring.service;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service("fileOperationService")
public class FileOperationServiceImpl implements FileOperationService {

    @Override
    public List<String> getTags(String tags, String comm) {
        List<String> tagList = new ArrayList<>();
        if (tags == null) {
            return tagList;
        }

        if (comm.length() == 0) {
            comm = ",";
        }
        List<String> resList = Arrays.asList(tags.split(comm)).stream().collect(Collectors.toList());
        return resList;
    }

    @Override
    public String getLogDir(String subDir) {
        String dir = "/home/pubsrv/logs/java/backend-picture-api/";

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        RollingFileAppender techical = config.getAppender("RollingFileINFO");
        if (techical != null && techical.getFileName() != null) {
            dir = techical.getFileName().replaceFirst("[^\\/]+$", "");
        }
        //for travis test
        File file = new File(dir);
        if (!file.exists()){
            File directory = new File("");
            dir = directory.getAbsolutePath();
        }

        dir += "/";
        dir += subDir;
        //travis over
        if (! dir.endsWith("/")) {
            dir += "/";
        }
        //创建子目录
        file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        return dir;
    }

    @Override
    public String getTextMd5(String text) {
        String value = null;
        try {
            ByteBuffer sendBuffer=ByteBuffer.wrap(text.getBytes("UTF-8"));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(sendBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //pass
        }
        return value;
    }

    @Override
    public Boolean createDir(String dir) {
        File file = new File(dir);
        if (! file.exists() ) {
            file.mkdirs();
        }
        return file.exists();
    }

}
