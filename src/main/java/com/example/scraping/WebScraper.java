package com.example.scraping;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WebScraper {

    private static FirefoxDriver driver;


    public static void main(String[] args){
        StringBuilder playerName = getPlayerName(args);
        try{
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
            driver.get("https://www.nba.com/players");
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

            WebDriverWait wait = new WebDriverWait(driver, 10);


            cookieButtonClicker(wait, driver);

            navigateToPlayerStats(wait, playerName, driver);

            setParameter(wait, driver);

            int yearColIndex = colIndex("BY YEAR", driver);
            int threePAColIndex = colIndex("3PA", driver);

            printResults(yearColIndex, threePAColIndex, driver);


            driver.quit();

        }
        catch (Exception e){
            e.printStackTrace();
            driver.quit();
        }
    }

    /**
     * Sets the per mode parameter to per 40 minutes
     * @param wait - selenium's wait object, halts the WebDriver until expectation is true
     */
    public static void setParameter(WebDriverWait wait, FirefoxDriver driver){

        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//select[@name=\"PerMode\"]")))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@label=\"Per 40 Minutes\"]"))).click();

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));
        }
        catch(NoSuchElementException | TimeoutException e){
            System.out.println("Error, table not found: check if the desired table displays correctly in your browser");
            driver.quit();
            System.exit(0);
        }
    }

    /**
     * Prints the end stats
     * @param yearColIndex - index of column that contains seasons
     * @param threePAColIndex - index of column that contains 3PA stat
     */
    public static void printResults(int yearColIndex, int threePAColIndex, FirefoxDriver driver){
        List<WebElement> dataRows = driver.findElements(By.xpath("//nba-stat-table[@template=\"player/player-traditional\"]//tr[count(td) > 1]"));
        Stream<WebElement> dataStream = dataRows.stream();

        dataStream.forEach(
                data -> {
                    String[] split = data.getText().split(" ");
                    System.out.println(split[yearColIndex]+" "+split[threePAColIndex]);
                }
        );
    }

    /**
     * Finds index of column in nba traditional stats table searched by text that the column contains
     * @param colText - Text that is contained in the column
     * @return index of searched column
     */
    public static int colIndex(String colText, FirefoxDriver driver){
        Stream<WebElement> dataColElements = driver.findElements(By.xpath("//nba-stat-table[@template=\"player/player-traditional\"]//th")).stream();
        //dataColElements.forEach(data -> System.out.println(data.getText()));
        List<String> dataColNames = dataColElements.map(WebElement::getText)
                .collect(Collectors.toList());

        return dataColNames.indexOf(colText);
    }

    /**
     *  Navigates from the driver's starting point to the player's personal stats page, where
     *  the first table contains his 3PA for individual seasons.
     *
     * @param wait - selenium's wait object, halts the WebDriver until expectation is true
     * @param playerName - StringBuilder object of the program arguments, represents player's full name
     */
    public static void navigateToPlayerStats(WebDriverWait wait, StringBuilder playerName, FirefoxDriver driver){
        WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder=\"Search Players\"]")));

        searchField.sendKeys(playerName);
        try {

            wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.xpath("//table[@class=\"players-list\"]//tr"), 20));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class=\"players-list\"]//a"))).click();
        }
        catch (NoSuchElementException | TimeoutException e){
            System.out.println("Error, player not found: check if you input a correct player name");
            driver.quit();
            System.exit(0);
        }


        wait.until(ExpectedConditions.elementToBeClickable(driver.findElementByXPath("//a[contains(@href, '/stats/player')]"))).click();

    }

    /**
     *  Accepts cookies if a popup is shown.
     *
     * @param wait - selenium's wait object, halts the WebDriver until expectation is true
     */
    public static void cookieButtonClicker(WebDriverWait wait, FirefoxDriver driver){
        try {
            WebElement cookieButton = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("onetrust-accept-btn-handler"))).get(0);
            cookieButton.click();
            driver.navigate().refresh();
            waitForPageLoaded(wait);
        }
        catch (NoSuchElementException  | StaleElementReferenceException ignored) {
        }
    }


    /**
     * Waits for page to be refreshed.
     *
     * @param wait - selenium's wait object, halts the WebDriver until expectation is true
     *
     */
    public static void waitForPageLoaded(WebDriverWait wait) {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver)
                    {
                        return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
                    }
        };

        try {
            wait.until(expectation);
        }
        catch(Throwable error) {
            System.out.println("Timeout waiting for Page Load Request to complete.");
        }
    }

    /**
     * Parses program arguments into a StringBuilder.
     *
     * @param args - program arguments
     * @return StringBuilder object of the program arguments, represents player's full name
     */
    public static StringBuilder getPlayerName(String[] args){
        StringBuilder playerName = new StringBuilder();
        for(String s : args){
            playerName.append(s).append(" ");
        }
        playerName.deleteCharAt(playerName.length()-1);
        return playerName;
    }

}
