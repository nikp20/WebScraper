package com.example.scraping;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collections;
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

            /*options.addArguments("-private");
            FirefoxProfile firefoxProfile = new FirefoxProfile();
            firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);*/

            driver = new FirefoxDriver(options);
            driver.get("https://www.basketball-reference.com/leagues/NBA_2020_per_game.html");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            WebDriverWait wait = new WebDriverWait(driver, 10);

            navigateToPlayerStats(wait, playerName, driver);

            int yearColIndex = colIndex("Season", driver);
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
    /*
    public static void setParameter(WebDriverWait wait, FirefoxDriver driver){

        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//select[@name=\"PerMode\"]")))).click();

        try {
            Thread.sleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@label=\"Per 40 Minutes\"]"))).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nba-stat-table")));
        }
        catch(NoSuchElementException | TimeoutException | InterruptedException e){
            System.out.println("Error, table not found: check if the desired table displays correctly in your browser");
            driver.quit();
            System.exit(0);
        }
    }*/

    /**
     * Prints the end stats
     * @param yearColIndex - index of column that contains seasons
     * @param threePAColIndex - index of column that contains 3PA stat
     */
    public static void printResults(int yearColIndex, int threePAColIndex, FirefoxDriver driver){
        List<WebElement> dataRows = driver.findElements(By.xpath("//table[@id=\"per_minute\"]//tbody//tr[count(td) > 1]"));
        Collections.reverse(dataRows);
        Stream<WebElement> dataStream = dataRows.stream();
        System.out.printf("%-7s %s  %s", "Season", "3PA/36", "3PA/40");
        System.out.println();


        dataStream.forEach(
                data -> {
                    String[] split = data.getText().split(" ");
                    double threePA40=transform(Double.parseDouble(split[threePAColIndex]));
                    System.out.printf("%s %6s %7s", split[yearColIndex], split[threePAColIndex], threePA40);
                    System.out.println();
                }
        );
    }

    /**
     * Transformed 3PA stat from per 36 minutes to per 40 minutes
     * @param number - number to be transformed
     * @return 3PA per 40 minutes
     */
    public static double transform(double number){
        double result = 40.0*number/36.0;
        result = Math.round((result) * 10) / 10.0;

        return result;
    }

    /**
     * Finds index of column in nba traditional stats table searched by text that the column contains
     * @param colText - Text that is contained in the column
     * @return index of searched column
     */
    public static int colIndex(String colText, FirefoxDriver driver){
        Stream<WebElement> dataColElements = driver.findElements(By.xpath("//table[@id=\"per_minute\"]//th")).stream();
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
        WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name=\"search\"]")));

        searchField.sendKeys(playerName);
        searchField.sendKeys(Keys.RETURN);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class=\"search-item-name\"]//a[contains(@href, \"/players/\")]"))).click();

        }
        catch (NoSuchElementException | TimeoutException e){
            System.out.println("Error, player not found: check if you input a correct player name");
            driver.quit();
            System.exit(0);
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
