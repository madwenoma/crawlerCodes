package com.lee;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 */
public class KaoLaTest {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\Program Files\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);
        JDCheckin(driver);
    }

    public static void JDCheckin(ChromeDriver driver) throws InterruptedException {
        driver.get("https://www.kaola.com/login.html");
        System.out.println(driver.getTitle());

        WebElement loginType = driver.findElement(By.className("head2"));
        loginType.click();

        List<WebElement> iframes = driver.findElementsByXPath("//iframe");

        iframes.forEach(f -> System.out.println(f.getAttribute("src")));

//        Thread.sleep(2000);
        driver.switchTo().frame(iframes.get(1));

        WebElement loginname = driver.findElementByName("email");
        WebElement password = driver.findElementByName("password");
        WebElement submit = driver.findElementById("dologin");

        loginname.sendKeys("o.noma@163.com");
        Thread.sleep(2000);
        password.sendKeys("4220282");
        Thread.sleep(2000);
        submit.click();
        Thread.sleep(2000);
        System.out.println(driver.getTitle());

        System.out.println(driver.findElementById("user163Box").getText());

//        String mainWinHandler = driver.getWindowHandle();
        driver.findElementByClassName("newnav").click();
//        for (String handler : driver.getWindowHandles()) {
//            if (handler.equals(mainWinHandler)) {
//                continue;
//            }
//            driver.switchTo().window(handler);
//        }
        Thread.sleep(2000);
        String checkInfo = driver.findElementByClassName("u-btn-checkin-end").getText();
        System.out.println(checkInfo);
        driver.close();
    }
}
