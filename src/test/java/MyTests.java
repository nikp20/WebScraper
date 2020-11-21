import com.example.scraping.WebScraper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
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
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        options.addArguments("-private");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);

        options.setAcceptInsecureCerts(true);
        driver = new FirefoxDriver(options);
        wait=new WebDriverWait(driver, 10);
    }

    @Test
    @Order(1)
    void getPlayerNameTest() {
        StringBuilder actual = WebScraper.getPlayerName(new String[]{"Goran", "Dragic"});
        assertEquals("Goran Dragic", actual.toString());
    }

    @Test
    @Order(2)
    void colIndexTest(){
        driver.get("https://www.nba.com/stats/player/2544/");
        WebScraper.cookieButtonClicker(wait, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));

        int expColIndex = 0;
        int actual = WebScraper.colIndex("BY YEAR", driver);
        assertEquals(expColIndex, actual);

    }

    @Test
    @Order(3)
    void printTest(){
        driver.get("https://www.nba.com/stats/player/2544/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));


        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        WebScraper.printResults(0, 9, driver);

        String expectedOutput = "2019-20 8.9\r\n2018-19 7.1";

        assertEquals(expectedOutput, outContent.toString().substring(0,outContent.size()-2));

    }

    @Test
    @Order(4)
    void navigateToPlayerStatsTest(){
        driver.get("https://www.nba.com/players");


        String expected ="https://www.nba.com/stats/player/1629029/";
        StringBuilder name = new StringBuilder("Luka Doncic");

        WebScraper.navigateToPlayerStats(wait, name, driver);
        String actual = driver.getCurrentUrl();
        assertEquals(expected, actual);

    }

    @Test
    @Order(5)
    void setParameterTest(){
        driver.get("https://www.nba.com/stats/player/2544/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));

        WebScraper.setParameter(wait, driver);

        String expected = "https://www.nba.com/stats/player/1629029/?Season=2019-20&SeasonType=Regular%20Season&PerMode=Per40";

        assertEquals(expected, driver.getCurrentUrl());

    }

    @AfterAll
    static void shutdown(){
        driver.quit();
    }
}
