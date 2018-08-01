package com.lee;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/7/13.
 */
public class ChromeDriverTest {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\Program Files\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);
        JDCheckin(driver);
    }

    public static void TaobaoCheckin(ChromeDriver driver) throws InterruptedException {
        driver.get("https://www.etao.com/login.htm?redirect_url=http%3A%2F%2Fwww.etao.com%2Fmy%2Forder.htm%3Fspm%3D1002.8274268.2698717.1.23ec2c34M2eTHe");
        System.out.println(driver.getTitle());
        System.out.println(driver.getPageSource());
        Thread.sleep(2200);
        WebElement frame = driver.findElementByXPath("//*[@id=\"J_content\"]/iframe");
        driver.switchTo().frame(frame);
        driver.findElementById("J_Static2Quick").clear();
        Thread.sleep(1423);
        WebElement loginname = driver.findElementByName("TPL_username");
        WebElement password = driver.findElementByName("TPL_password");
        WebElement submit = driver.findElementById("btn-submit");

        loginname.sendKeys("93feiaichao");
        Thread.sleep(2132);
        password.sendKeys("4250099a@");
        Thread.sleep(2344);
        submit.click();
        Thread.sleep(3111);
        driver.get("https://taojinbi.taobao.com/index.htm");
        WebElement taoMoney = driver.findElementByClassName("J_GoTodayBtn");
        Thread.sleep(3000);
        taoMoney.click();
        Thread.sleep(1220);
        driver.close();
    }

    public static void JDCheckin(ChromeDriver driver) throws InterruptedException {
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
        password.sendKeys("4220282a@");
        Thread.sleep(2000);
        submit.click();
        Thread.sleep(2000);
        driver.get("https://vip.jd.com/");
        WebElement signIn = driver.findElementByClassName("sign-in");
        Thread.sleep(3000);
        signIn.click();
        driver.get("https://vip.jd.com/sign/index");
        System.out.println(driver.findElementByClassName("title").getText());
        driver.close();
    }
}
