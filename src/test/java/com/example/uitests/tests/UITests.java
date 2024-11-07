package com.example.uitests.tests;

import com.example.uitests.pom.HomePage;
import com.example.uitests.pom.MoreFiltersModal;
import com.example.uitests.pom.PropertyPage;
import com.example.uitests.pom.SearchResultsPage;
import com.example.uitests.utils.DateUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.uitests.pom.HomePage.LOCATION_FOR_AIRBNB;

public class UITests {
  private WebDriver driver;
  private HomePage homePage;

  @BeforeEach
  void setUp() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.get("https://airbnb.com");
    homePage = new HomePage(driver);
    homePage.basicSearch();
  }

  @Test
  void searchTest() {
    SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
    searchResultsPage.clickSearchDestinationButton();

    Assert.assertEquals(searchResultsPage.getDestinationText(),LOCATION_FOR_AIRBNB);
    Assert.assertEquals(searchResultsPage.getCheckInDateText(), DateUtils.formatDateAsPattern(DateUtils.CURRENT_DATE, DateUtils.MONTH_AND_DAY_PATTERN));
    Assert.assertEquals(searchResultsPage.getCheckOutDateText(), DateUtils.formatDateAsPattern(DateUtils.ONE_WEEK_FROM_NOW_DATE, DateUtils.MONTH_AND_DAY_PATTERN));
    Assert.assertEquals(searchResultsPage.getNumberOfGuestsText(), "3");

    for (Integer count : searchResultsPage.getBedroomCounts()) {
      Assert.assertTrue(count < 4, "Expected bedroom count to be smaller than 4, but found: " + count);
    }

  }

  @Test
  void moreFiltersTest() {
    homePage.clickMoreFiltersButton();

    MoreFiltersModal moreFiltersModal = new MoreFiltersModal(driver);
    moreFiltersModal.increaseBedroomsByNumber(5);

    moreFiltersModal.checkPoolAmenitiesButton();
    moreFiltersModal.applyFilters();

    SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
    searchResultsPage.waitForListingsToLoad();

    for (Integer count : searchResultsPage.getBedroomCounts()) {
      Assert.assertTrue(count >= 5, "Expected bedrooms number to be higher than 5 but found: " + count);
    }

    searchResultsPage.clickFirstProperty();

    String originalWindow = driver.getWindowHandle();
    // switch to the new opened window
    for (String windowHandle : driver.getWindowHandles()) {
      if (!originalWindow.contentEquals(windowHandle)) {
        driver.switchTo().window(windowHandle);
        break;
      }
    }

    PropertyPage propertyPage = new PropertyPage(driver);
    propertyPage.clickShowAmenitiesButton();

    Assert.assertTrue(propertyPage.isPoolElementDisplayed(), "There is no pool in the amenities list for this property.");
  }

  @Test
  void hoverTest() {
    SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
    searchResultsPage.hoverOverFirstProperty();

    Assert.assertEquals(searchResultsPage.getPinBackgroundColor(), "rgb(34, 34, 34)", "Expected background color of button to be black");
    Assert.assertEquals(searchResultsPage.getFirstPropertyPrice(), searchResultsPage.getPriceFromPin(),
        "Price on first property is expected to be " + searchResultsPage.getPriceFromPin() +
            " but found " + searchResultsPage.getFirstPropertyPrice());

    searchResultsPage.clickPinButton();

    Assert.assertEquals(searchResultsPage.getPinCardText(0), searchResultsPage.getFirstPropertyText(0),
        "Expected property title displayed on the pin to be " + searchResultsPage.getFirstPropertyText(0) +
            " but found " + searchResultsPage.getPinCardText(0));
    Assert.assertEquals(searchResultsPage.getPinCardText(1), searchResultsPage.getFirstPropertyText(1),
        "Expected property description displayed on the pin to be " + searchResultsPage.getFirstPropertyText(1) +
            " but found " + searchResultsPage.getPinCardText(1));

    //find the price per night on property card and pin
    Pattern pattern = Pattern.compile("(\\d+)\\s*lei");
    Matcher firstPropertyPrice = pattern.matcher(searchResultsPage.getPinCardText(3));
    firstPropertyPrice.find();

    Matcher cardPropertyPrice = pattern.matcher(searchResultsPage.getFirstPropertyText(3));
    cardPropertyPrice.find();

    Assert.assertEquals(firstPropertyPrice.group(0), cardPropertyPrice.group(0),
        "Expected property price displayed on the pin to be " + firstPropertyPrice.group(0) +
            " but found " + cardPropertyPrice.group(0));
  }


  @AfterEach
  void tearDown() {
    driver.quit();
  }
}

