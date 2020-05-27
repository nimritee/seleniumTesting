
//Project:         To demonstrate best selenium Practices.
//Author:          Nimritee Sirsalewala
//Primary use:     Informative
//Last updated on: 28th May 2020
//Copyright:       © 2020 Nimritee All rights reserved.
package seleniumTest;

import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.Test;

public class TestMain {

    WebDriver driver;

    
    public void invoke_browser(JsonObject data) {
        try {
            String url = data.get("url").getAsString();
            String browserName = data.get("browserName").getAsString();
            String browserType = data.get("browserType").getAsString();
            set_browser(browserName,browserType,url);
            driver.get(url);
            performActions(data);
        } catch (Exception e) {
        }
    }

    public void set_browser(String browserName, String browserType, String url) {
        try{
            if(browserName.equals("Chrome")) { 
            String driverPath = "Drivers/chromedriver";
            System.setProperty("webdriver.chrome.driver", driverPath);
            if(browserType.equals("headless"))
            {
                ChromeOptions chromeoptions = new ChromeOptions();
                chromeoptions.addArguments("--no-sandbox");
                chromeoptions.addArguments("--window-size=1920,1080");
                chromeoptions.addArguments("--disable-gpu");
                chromeoptions.addArguments("--disable-extensions");
                chromeoptions.setExperimentalOption("useAutomationExtension", false);
                chromeoptions.addArguments("--proxy-server='direct://'");
                chromeoptions.addArguments("--proxy-bypass-list=*");
                chromeoptions.addArguments("--start-maximized");
                chromeoptions.addArguments("--headless");
                driver = new ChromeDriver(chromeoptions);
            }
            else
                driver = new ChromeDriver();
            
            driver.manage().deleteAllCookies();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
            }
            else if(browserName.equals("ie")) {
                System.setProperty("webdriver.ie.driver", "Drivers/IEDriverServer.exe");
                driver= new InternetExplorerDriver();
                driver.get(url);
                driver.manage().deleteAllCookies();
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
            }
            System.out.println("Browser loaded sucessfully!");
        }catch(Exception e)
        {
            String error_message = e.getMessage();
            if(error_message.contains("Timed out waiting"))
                error_message = "Error: Unable to Invoke Browser";
        
            driver.quit();
        }
    }

    public void performActions(JsonObject data)
    {
        try{
            String blogTitle = data.get("blogTitle").getAsString();
            System.out.println("Landed on Nimritee's Blog successfully!");
            driver.findElement(By.linkText(blogTitle)).click();
            System.out.println("Blog loaded successfully!");
            WebDriverWait wait = new WebDriverWait(driver,30);
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.linkText("Technical Blogs"))));
            driver.findElement(By.linkText("Technical Blogs")).click();
            System.out.println("Test completed successfully!");
            driver.quit();
        }catch(Exception e){
            System.out.println("Error: While performing actions!");
        }
    }


    @Test
    public void main()
    {
        try 
        {
            String file_name = String.valueOf(System.getProperty("filename"));
            file_name = file_name.length()>-1 && file_name!="null" ? file_name : "commandFile.json";
            FileReader reader = new FileReader(file_name);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(reader);
            if(jsonObject != null)
                System.out.println("Command File has loaded successfully!");
            else
                System.out.println("Error: Command File cannot be loaded!");
            
            TestMain testObj = new TestMain();
            testObj.invoke_browser(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
