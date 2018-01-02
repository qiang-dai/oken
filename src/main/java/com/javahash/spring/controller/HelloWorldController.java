package com.javahash.spring.controller;

import com.javahash.spring.message.SimpleHttpMsg;
import com.javahash.spring.service.FileOperationService;
import com.javahash.spring.download.UrlDownloadAction;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class HelloWorldController { 

    @Autowired
    private FileOperationService fileOperationService;
    private Properties properties;

    HelloWorldController() {
        load();
    }


    private Properties load(){
        try {
            if (properties != null) {
                return properties;
            }

            properties=new Properties();
            File file = new File(System.getProperties().getProperty("user.home") + "/oken.properties");
            String absolutePath = file.getAbsolutePath();
            //读取配置文件
            FileReader reader=new FileReader(absolutePath);
            properties.load(reader);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return properties;
    }

    private String getSignedUrl(String url) {
        if (url.indexOf("?") != -1) {
            url += "&secret_key=" + properties.get("secret_key");
        } else {
            url += "?secret_key=" + properties.get("secret_key");
        }
        log.info("url:{}", url);

        List<String> items = fileOperationService.getTags(url, "\\?");
        List<String> params = fileOperationService.getTags(items.get(1), "&");
        params = params.stream()
                .map(e -> e.toLowerCase())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        String line = String.join("&", params);
        String md5 = fileOperationService.getTextMd5(line);
        md5 = md5.toUpperCase();
        url += "&sign=" + md5;
        log.info("url:{}", url);

        return url;
    }
    @RequestMapping("/hello")
    public ResponseEntity hello(HttpServletRequest request,
                                Model model) {
        load();

        String url = (String)properties.get("btc_usd_this_week_info");
        //String url = (String)properties.get("exchange_rate");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("contentType", "application/x-www-form-urlencoded");

        url = getSignedUrl(url);
        String res = UrlDownloadAction.getResponse(url, headerMap);
        model.addAttribute("res", res);
        log.info("res:{}", res);

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<SimpleHttpMsg>(new SimpleHttpMsg(0, res), httpHeaders, HttpStatus.OK);

    }

}
