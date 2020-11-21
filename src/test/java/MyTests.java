import com.example.scraping.WebScraper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTests {
    static FirefoxDriver driver;
    static WebDriverWait wait;

    @BeforeAll
    static void init(){
        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        FirefoxOptions chromeOptions = new FirefoxOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--window-size=1920,1080");


        chromeOptions.setAcceptInsecureCerts(true);
        driver = new FirefoxDriver(chromeOptions);
        wait=new WebDriverWait(driver, 10);
    }

    @Test
    void getPlayerNameTest() {
        StringBuilder actual = WebScraper.getPlayerName(new String[]{"Goran", "Dragic"});
        assertEquals("Goran Dragic", actual.toString());
    }

    @Test
    void colIndexTest(){
        driver.get("https://www.nba.com/stats/player/201609/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));

        int expColIndex = 0;
        int actual = WebScraper.colIndex("BY YEAR", driver);
        assertEquals(expColIndex, actual);
       // driver.quit();

    }

    @Test
    void printTest(){
        driver.get("https://www.nba.com/stats/player/1629029/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));


        //System.out.println(driver.getCurrentUrl());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        WebScraper.printResults(0, 9, driver);

        String expectedOutput = "2019-20 8.9\r\n2018-19 7.1";

        assertEquals(expectedOutput, outContent.toString().substring(0,outContent.size()-2));
        //driver.quit();

    }

    @Test
    private static void navigateToPlayerStatsTest(){
        driver.get("https://www.nba.com/players");

        String expected ="https://www.nba.com/stats/player/2544/";
        StringBuilder name = new StringBuilder("Lebron James");

        WebScraper.navigateToPlayerStats(wait, name, driver);
        String actual = driver.getCurrentUrl();
        assertEquals(expected, actual);

    }

    @AfterAll
    static void shutdown(){
        driver.quit();
    }
}
