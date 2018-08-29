package com.lee.demos;

import com.lee.mail.SendQQMailUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 */
public class AutoCheckIn {

    private static String pwd = "xxxxxx@";
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "/opt/google/chrome/chromedriver");
//        System.setProperty("webdriver.chrome.driver", "D:\\Program Files\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("useAutomationExtension", false);
//        options.addArguments("lang=zh_CN.UTF-8");
//        options.addArguments("--proxy-server=http://188.131.133.231:8000");
        options.addArguments("--window-size=1980,1000");
        ChromeDriver driver = new ChromeDriver(options);
        try {
//            driver.manage().window().maximize();
            String jdResult = jdCheckIn(driver);
            System.out.println(jdResult);
            String xijiResult = xiJiCheckIn(driver);
            System.out.println(xijiResult);
            Thread.sleep(2000);
            String kaolaResult = kaolaCheckIn(driver);
            System.out.println(kaolaResult);
            Thread.sleep(1000);
            SendQQMailUtil.sendMail("AutoCheckResult", xijiResult + "\n" + kaolaResult + "\n" + jdResult);
        } catch (Exception e) {
            e.printStackTrace();
            SendQQMailUtil.sendMail("AutoCheckResult", "error:" + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    public static String jdCheckIn(ChromeDriver driver) throws InterruptedException, IOException {

        driver.get("https://passport.jd.com/uc/login");
        System.out.println(driver.getTitle());

        WebElement loginType = driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div[3]/a"));
        loginType.click();

        Thread.sleep(2000);
        WebElement loginname = driver.findElementById("loginname");
        WebElement password = driver.findElementById("nloginpwd");
        WebElement submit = driver.findElementById("loginsubmit");

        loginname.sendKeys("madwenoma");
        Thread.sleep(2000);
        password.sendKeys(pwd);
        Thread.sleep(1120);
        submit.click();
        Thread.sleep(1030);
//        FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File("/home/lee/checkin/jd.png"));
        driver.get("https://vip.jd.com/");
        System.out.println("login over");
        System.out.println(driver.getTitle());
        WebElement signIn = driver.findElementByClassName("sign-in");
        Thread.sleep(1200);
        signIn.click();
        driver.get("https://vip.jd.com/sign/index");
        System.out.println(driver.getTitle());
        Thread.sleep(1300);
        driver.get("https://bean.jd.com/myJingBean/list");
        System.out.println(driver.getTitle());
        String date = driver.findElementByXPath("//*[@id=\"main\"]/div[4]/div/div[2]/table/tbody/tr[1]/td[1]").getText();
        String point = driver.findElementByXPath("//*[@id=\"main\"]/div[4]/div/div[2]/table/tbody/tr[1]/td[2]/span").getText();
        String type = driver.findElementByXPath("//*[@id=\"main\"]/div[4]/div/div[2]/table/tbody/tr[1]/td[3]").getText();
        String result = StringUtils.joinWith("-", date, point, type);
        return "jd:" + result;


    }

    public static String xiJiCheckIn(ChromeDriver driver) throws InterruptedException, IOException {
        String xijiLogin = "http://www.xiji.com/passport-login.html";
        driver.get(xijiLogin);
        System.out.println(driver.getTitle());
        WebElement loginname = driver.findElementByName("uname");
        WebElement password = driver.findElementByName("password");
        WebElement submit = driver.findElementById("btn_login_normal");
        loginname.sendKeys("18501170204");
        Thread.sleep(1846);
        password.sendKeys(pwd);
        Thread.sleep(1997);
        submit.click();
        Thread.sleep(2004);
        driver.get("http://www.xiji.com/member-integralcenter.html");
        WebElement myCenterBtn = driver.findElementByClassName("singnup-btn");
        Thread.sleep(1234);
        myCenterBtn.click();
        driver.switchTo().defaultContent();
        Thread.sleep(999);
        driver.get("http://www.xiji.com/point-point_detail.html");
        try {
            WebElement checkContent = driver.findElementByXPath("//*[@id=\"member_credits\"]/div[2]/table/tbody/tr[1]/td[1]");
            String date = checkContent.getText();
            checkContent = driver.findElementByXPath("//*[@id=\"member_credits\"]/div[2]/table/tbody/tr[1]/td[2]/span");
            String point = checkContent.getText();
            checkContent = driver.findElementByXPath("//*[@id=\"member_credits\"]/div[2]/table/tbody/tr[1]/td[3]");
            String type = checkContent.getText();
            String result = "xiji:";
            result = StringUtils.joinWith("-", result, date, point, type);
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
//        FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File("/home/lee/checkin/xiji.png"));

    }

    public static String kaolaCheckIn(ChromeDriver driver) throws InterruptedException, IOException {
        driver.get("https://www.kaola.com/login.html");
        System.out.println(driver.getTitle());
        Thread.sleep(1232);
        WebElement loginType = driver.findElement(By.className("head2"));
        loginType.click();

        Thread.sleep(1232);
        List<WebElement> iframes = driver.findElementsByXPath("//iframe");

        iframes.forEach(f -> System.out.println(f.getAttribute("src")));

        driver.switchTo().frame(iframes.get(1));

        Thread.sleep(1232);
        WebElement loginname = driver.findElementByName("email");
        WebElement password = driver.findElementByName("password");
        WebElement submit = driver.findElementById("dologin");
        String uname = "killgov@163.com";
        loginname.sendKeys(uname);
        System.out.println(loginname.getText());
        Thread.sleep(1846);
        password.sendKeys(pwd);
        Thread.sleep(1234);

        submit.click();
        Thread.sleep(1200);
        driver.get("https://www.kaola.com/activity/flashSaleIndex/show.html?navindex=2&zn=top");
        System.out.println(driver.getTitle());
//        FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File("/home/lee/checkin/kaola-order.png"));

        Thread.sleep(1100);
//        System.out.println(driver.findElementById("user163Box").getText());

        driver.findElementByClassName("newnav").click();
//        for (String handler : driver.getWindowHandles()) {
//            if (handler.equals(mainWinHandler)) {
//                continue;
//            }
//            driver.switchTo().window(handler);
//        }
        Thread.sleep(2000);
        driver.get("https://www.kaola.com/personal/my_point/index.html?");
        System.out.println(driver.getCurrentUrl());
        FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE), new File("/home/lee/checkin/kaola-point.png"));
        try {
            WebElement checkContent = driver.findElementByXPath("//*[@id=\"content\"]/div/div[4]/div/div[3]/div/table/tbody/tr[1]/td[1]");
            String date = checkContent.getText();
            checkContent = driver.findElementByXPath("//*[@id=\"content\"]/div/div[4]/div/div[3]/div/table/tbody/tr[1]/td[2]");
            String point = checkContent.getText();
            checkContent = driver.findElementByXPath("//*[@id=\"content\"]/div/div[4]/div/div[3]/div/table/tbody/tr[1]/td[3]");
            String type = checkContent.getText();
            String result = "kaolao:";
            result = StringUtils.joinWith("-", result, date, point, type);
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}