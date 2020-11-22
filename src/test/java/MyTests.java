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
import java.util.concurrent.TimeUnit;

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

        /*FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);*/

        options.setAcceptInsecureCerts(true);
        driver = new FirefoxDriver(options);
        driver.manage().deleteAllCookies();
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
        driver.get("https://www.basketball-reference.com/players/d/doncilu01.html");

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id=\"per_minute\"]")));

        int expColIndex = 0;

        int actual = WebScraper.colIndex("Season", driver);

        assertEquals(expColIndex, actual);
    }

    @Test
    @Order(3)
    void printTest(){
        driver.get("https://www.basketball-reference.com/players/d/doncilu01.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id=\"per_minute\"]")));


        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        WebScraper.printResults(0, 12, driver);

        String expectedOutput = "Season  3PA/36  3PA/40\r\n"+"2019-20    9.5    10.6\r\n2018-19    8.0     8.9";

        assertEquals(expectedOutput, outContent.toString().substring(0,outContent.size()-2));

    }

    @Test
    @Order(4)
    void navigateToPlayerStatsTest(){
        driver.get("https://www.basketball-reference.com/leagues/NBA_2020_per_game.html");


        String expected ="https://www.basketball-reference.com/players/d/doncilu01.html";

        StringBuilder name = new StringBuilder("Luka Doncic");

        WebScraper.navigateToPlayerStats(wait, name, driver);

        String actual = driver.getCurrentUrl();

        assertEquals(expected, actual);

    }

    @Test
    @Order(5)
    void transformationTest(){
        double expected = 10.6;
        double actual = WebScraper.transform(9.5);
        assertEquals(expected, actual);
    }

    @AfterAll
    static void shutdown(){
        driver.quit();
    }
}
