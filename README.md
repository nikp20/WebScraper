# WebScraper

## Description
A web scraper written in Java, using Selenium framework (Firefox geckodriver used as web driver), that scrapes <b>nba.com</b> for the <b>average three pointers attempted (3PA) each season </b>by a player given in program arguments.  
The program prints the results into the terminal line by line in the format<b>: [season year] [3PA]</b>

## How to run
### Requirements
- Java 7 or higher (https://www.oracle.com/java/technologies/javase-downloads.html)
- latest apache maven (http://maven.apache.org/download.cgi)
### Running the program
- Clone this repository into a local directory (<b>git clone https://github.com/nikp20/WebScraper.git</b>)
- Open the terminal in given directory
- Build the maven project and run tests (<b>mvn clean package</b>); This sould run around 20 seconds
- *The tests may fail because the <b>nba.com</b> website is not very reliable and sometimes the player stats can not be reached, this may also cause tha actual program to fail.*
- If the tests are failing you can skip the tests and build the project using (<b>mvn -Dmaven.test.skip=true clean package</b>)
- Run the generated jar file with player name as arguments (<b>java -jar target/web-scraping-1.0-SNAPSHOT.jar [player name]</b>); This should also run around 20 seconds
