package seleniumTest;

import java.io.BufferedWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class TestMain {

    WebDriver driver;
    String static_path = "";
    String dataset_case;

    //Test Type can be commented here.
    public void invoke_browser(JsonObject data, String testType) {
        try {
            if(testType == "null")
                testType = "chron";
            
            System.out.println(testType);
            helperObj.log("Test Type: "+testType,"Core",true,false,"","");

            dataset_case = data.get("dataset_case").getAsString();
            helperObj.get_api_url =  data.get("db_api_url").getAsString();
            helperObj.testType = testType;
            if(dataset_case.equals("true"))
            {
                helperObj.siteId =data.get("siteId").getAsString();
                helperObj.toolId = data.get("toolId").getAsString();
            }
            helperObj.print_status =data.get("print_log_terminal").getAsBoolean();
            String url_formed = data.get("url").getAsString();
            helperObj.domain = url_formed;
            String browser_name = data.get("browser_name").getAsString();
            String browser_type = data.get("browser_type").getAsString();
            set_browser(browser_name,browser_type,url_formed);
            if(browser_name.equals("Chrome"))
                driver.get(url_formed);
            
            navigateObj.driver = driver;
            adminObj.driver = driver;
            uploadObj.driver =driver;
            adhocObj.driver = driver;
            helperObj.driver =driver;
            helperObj.data = data;
            helperObj.log("Browser Invoked","Core",true,false,"","");
            login_page(data);
        } catch (Exception e) {
            helperObj.data =data;
            helperObj.log("Browser Invoked","Core",false,true,"","Could not Invoke the Browser");
        }
    }

    public void set_browser(String browser_name, String browser_type, String url_formed) {
        try{
            if (browser_name.equals("Chrome")) { 
            String driverPath = "Drivers/chromedriver_linux";
            driverPath = static_path.concat(driverPath);
            System.setProperty("webdriver.chrome.driver", driverPath);
            if(browser_type.equals("headless"))
            {
                ChromeOptions chromeoptions = new ChromeOptions();
                chromeoptions.addArguments("--no-sandbox");
                //chromeoptions.addArguments("--disable-dev-shm-usage");
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
        else if (browser_name.equals("ie")) {
            System.setProperty("webdriver.ie.driver", "Drivers/IEDriverServer.exe");
            driver= new InternetExplorerDriver();
            driver.get(url_formed);
            driver.manage().deleteAllCookies();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        }
    }catch(Exception e)
    {
        String error_message = e.getMessage();
        if(error_message.contains("Timed out waiting"))
            error_message = "Unable to Invoke Browser";
        
        helperObj.log("Invoke Browser","Core",false,true,"",error_message);
        driver.quit();
    }
 }

    public void login_page(JsonObject data) {
        try {
            loginObj.driver = driver;
            loginObj.dataset_case = dataset_case;
            Boolean login_status = loginObj.login_type(data);
            if(login_status.equals(true))
            {
                helperObj.log("Login","Core",true,false,"","");
                data_set(data);
            }
            else {
                helperObj.log("Login","Core",false,true,"","");
                LocalDateTime now = LocalDateTime.now();  
                String end_time = "Execution End Time: "+dtf.format(now)+"\n" + "\n";
                BufferedWriter out = new BufferedWriter(new FileWriter("log.txt", true)); 
                out.write(end_time); 
                out.close(); 
                driver.quit();
            }
        } catch (Exception e) {
            helperObj.log("Login","Core",false,true, "","");
            driver.quit();
        }
    }

    //TO DO: Get DataSet Name
    public void data_set(JsonObject data) {
        try {
            if(dataset_case.equals("true"))
            {
                String tool_id = data.get("tool_id").getAsString();
                Thread.sleep(3000);
                JavascriptExecutor js = (JavascriptExecutor)driver;
                String script = "return document.getElementById('"+tool_id+"').textContent";
                String dataset_name = js.executeScript(script).toString();
                driver.findElement(By.id(tool_id)).click();
                helperObj.dataset_name = dataset_name;
                helperObj.log("DataSet Select","Core",true,false, "","");
            }
            if(data.get("operations")== null)
            {
                helperObj.log("Login Test Completed!","Core",true,true,"","");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();  
                String end_time = "Execution End Time: "+dtf.format(now)+"\n" + "\n";
                BufferedWriter out = new BufferedWriter(new FileWriter("log.txt", true)); 
                out.write(end_time); 
                out.close(); 
                driver.quit();
            }
            else
            {
                JsonArray operations = (JsonArray) data.get("operations");
                for (int i = 0; i < operations.size(); i++) {
                    JsonObject action = (JsonObject) operations.get(i);
                    String feature_name = (String) action.get("feature").getAsString();
                    navigateObj.helperObj = helperObj;
                    tableObj.helperObj = helperObj;
                    sumarryObj.helperObj = helperObj;
                    uploadObj.helperObj =helperObj;
                    uploadObj.adminObj = adminObj;
                    adminObj.helperObj =helperObj;
                    adhocObj.helperObj = helperObj;
                    switch (feature_name) {
                        case "report":
                        navigateObj.navigate_to_report(action);
                        break;
                    case "chart":
                        navigateObj.navigate_to_chart(action);
                        break;
                    case "summary":
                        navigateObj.navigate_to_summary(action);
                        break;
                    case "alfred":
                        navigateObj.navigate_to_alfred(action);
                        break;
                    case "uploadData":
                        uploadObj.upload_data(action);
                        break;
                    case "appendData":
                        uploadObj.append_data(action);
                        break;
                    case "overwriteData":
                        uploadObj.overwrite_data(action);
                        break;
                    case "dashboard":
                        navigateObj.navigate_to_dashBoard(action);
                        break;
                    case "createDataset":
                        adminObj.make_dataset(action);
                        break;   
                    case "columDefinition":
                        adhocObj.column_definition(action);
                        break;
                    case "metric":
                        adhocObj.dataset_changes(action,"Metric","Add Metrics");
                        break;
                    case "property":
                        adhocObj.dataset_changes(action,"Property","Add Segments");
                        break;
                    case "youtube":
                        {
                            driver.findElement(By.id("search")).sendKeys("rockmetric");
                            driver.findElement(By.id("search-icon-legacy")).click();
                            driver.findElement(By.xpath("/html/body/ytd-app/div/ytd-page-manager/ytd-search/div[1]/ytd-two-column-search-results-renderer/div/ytd-section-list-renderer/div[2]/ytd-item-section-renderer/div[3]/ytd-video-renderer[1]/div[1]/div/div[1]/div/h3/a")).click();
                        }
                        break;
                    default:
                        System.out.println("Error: Invalid Dataset");
                    }
                }
                helperObj.log("Test Status","Core",true,true,"","");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();  
                String end_time = "Execution End Time: "+dtf.format(now)+"\n" + "\n";
                BufferedWriter out = new BufferedWriter(new FileWriter("log.txt", true));
                out.write(end_time); 
                out.close(); 
                driver.quit();
            }
        } catch (Exception e) {
            helperObj.log("DataSet Select","Core",false,true,"","");
            driver.quit();
        }
    }

    @Test
    public void main()
    {
        try 
        {
            String testType = String.valueOf(System.getProperty("testType"));
            String file_name = String.valueOf(System.getProperty("filename"));
            file_name = file_name.length()>-1 && file_name!="null" ? file_name : "test.json";
            LocalDateTime now = LocalDateTime.now();  
            String time = "Execution Start Time: "+dtf.format(now)+"\n";
            BufferedWriter out = new BufferedWriter(new FileWriter("log.txt", true)); 
            out.write("Application Test Report" + "\n");
            out.write(time); 
            out.close();
            file_name = static_path.concat(file_name);
            FileReader reader = new FileReader(file_name);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(reader);
            if(jsonObject != null)
                System.out.println("Json File loaded Successfully!");
            else
                System.out.println("Error: Json File did not load!");
            
            TestMain myObj = new TestMain();
            myObj.invoke_browser(jsonObject,testType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
