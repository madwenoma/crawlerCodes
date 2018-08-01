package com.lee;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;

class JeipaiImageHandler implements WorkHandler<JiepaiImageEvent> {
    //"http://wx2.sinaimg.cn/wap180/69b97550gy1ftmdm99rsdj21bw2bcb2b.jpg"
    @Override
    public void onEvent(JiepaiImageEvent event) throws Exception {
        String imageUrl = event.getImageUrl();
        System.out.println("take job from disruptor: " + imageUrl);
        String fileName = event.getFileName() + ".jpg";
        File imageFile = new File("C:\\jiepai\\", fileName);
        FileUtils.write(imageFile, event.getContent());
        System.out.println("保存图片 " + event.getImageUrl() + " 到 " + imageFile.getAbsolutePath());
    }
}

class JiepaiImageEvent {
    private String imageUrl;
    private String fileName;
    private byte[] content;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

public class JiepaiCrawler extends RamCrawler {

    private static Disruptor disruptor;
    static String cookie = "SUB=_2A252X29rDeRhGedO4lMZ8CjKzz2IHXVVoHEjrDV6PUJbkdAKLWzakW1NXJwkxIfR4ltW6WJSrrY3g7sMfIHVw9sP;SUHB=02M-UCGWTLOEoO;SCF=AkBjeSia3eV3GoY72BMoVXJMLoDviM0OhNvcn58n_INCidcGOv0e6Tq7ylPc4qPb9DDGL199woAGUxg6cPBw0jY.;SSOLoginState=1532698427;_T_WM=2245f7cf36e308b331fd5fbb4eba2a7c;WEIBOCN_FROM=1110006030;MLOGIN=1;M_WEIBOCN_PARAMS=uicode%3D20000174;";

    static {
        ThreadFactory threadFactory = r -> new Thread(r, "simpleThread");

        EventFactory<JiepaiImageEvent> factory = () -> new JiepaiImageEvent();
        SleepingWaitStrategy strategy = new SleepingWaitStrategy();
        int bufferSize = 1024 * 8;
        disruptor = new Disruptor(factory, bufferSize, threadFactory,
                ProducerType.SINGLE, strategy);
        JeipaiImageHandler[] handlers = new JeipaiImageHandler[4];
        for (int i = 0; i < 4; i++) {
            handlers[i] = new JeipaiImageHandler();
        }
        disruptor.handleEventsWithWorkerPool(handlers);
        System.out.println("disruptor init over...");
        disruptor.start();
    }

    private ConcurrentMap<String,String> urlFileNameMap;

    public JiepaiCrawler(ConcurrentMap<String,String> urlFileNameMap) {
        this.urlFileNameMap = urlFileNameMap;
        this.setAutoParse(true);
        this.addRegex("-.*#.*");
        this.addRegex("-.*\\?.*");
        this.getConf().setExecuteInterval(1000);
        this.getConf().setAutoDetectImg(true);
        this.setThreads(2);
        for (String imgUrl : urlFileNameMap.keySet()) {
            this.addSeed(imgUrl);
        }
    }

    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(cookie);
        return request.responsePage();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String contentType = page.contentType();
        //根据Content-Type判断是否为图片
        if (contentType != null && contentType.startsWith("image")) {
            System.out.println("prepare to publish event: " + page.url());
            publishImageDownloadEvent(page);
        }

    }

    private void publishImageDownloadEvent(Page page) {
        RingBuffer<JiepaiImageEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            JiepaiImageEvent event = ringBuffer.get(sequence);
            event.setImageUrl(page.url());
            event.setFileName(urlFileNameMap.get(page.url()));
            event.setContent(page.content());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public static void main(String[] args) throws Exception {
    }

}

