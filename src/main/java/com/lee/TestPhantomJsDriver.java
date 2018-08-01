package com.lee;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Set;

/**
 * PhantomJs是一个基于webkit内核的无头浏览器，即没有UI界面，即它就是一个浏览器，只是其内的点击、翻页等人为相关操作需要程序设计实现;
 * 因为爬虫如果每次爬取都调用一次谷歌浏览器来实现操作,在性能上会有一定影响,而且连续开启十几个浏览器简直是内存噩梦,
 * 因此选用phantomJs来替换chromeDriver
 *  * PhantomJs在本地开发时候还好，如果要部署到服务器，就必须下载linux版本的PhantomJs,相比window操作繁琐
 *  *
 *  * @author zhuangj
 *  * @date 2017/11/14
 */
public class TestPhantomJsDriver {


    public static PhantomJSDriver getPhantomJSDriver() {
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:\\Program Files\\phantomjs-1.9.8-windows\\phantomjs.exe");
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[]{"--web-security=no", "--ignore-ssl-errors=yes"});
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return driver;
    }

    public static void main(String[] args) throws InterruptedException {
        PhantomJSDriver driver = getPhantomJSDriver();
        String username = "93feiaichao";
        String password = "4250099a@";

        driver.get("https://login.m.taobao.com/login.htm?spm=0.0.0.0&nv=true");
        WebElement mobile = driver.findElementByName("TPL_username");
        mobile.clear();
        mobile.sendKeys(username);
        WebElement pass = driver.findElementByName("TPL_password");
        pass.clear();
        pass.sendKeys(password);
        WebElement submit = driver.findElementById("btn-submit");
        submit.click();
        Thread.sleep(2000);
//        String cookies = concatCookie(driver);
//        System.out.println(cookies);
        Set<Cookie> cookies = driver.manage().getCookies();
        driver = getPhantomJSDriver();
        driver.get("https://taojinbi.taobao.com/index.htm");
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }
        WebElement taoMoney = driver.findElementByClassName("J_GoTodayBtn");
        taoMoney.click();
        Thread.sleep(2000);
        System.out.println(driver.findElementByClassName("J_Coin").getText());
        driver.close();


//        driver.get("http://m.dongao.com/");
//        driver.get("https://www.baidu.com/");
//        System.out.println(driver.getTitle());
//        System.out.println(driver.getCurrentUrl());
    }

    public static String concatCookie(PhantomJSDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }

}