package com.example.uitests.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PropertyPage extends BasePage {
  private final By propertyHeaderPath = By.cssSelector("h1");
  private final By showAmenitiesButtonPath = By.xpath("//button[contains(.,'amenities')]");
  private final By poolAmenitiesTextPath = By.xpath("//h2[text()='Parking and facilities']//following::div[contains(.,'pool') or contains(.,'Pool')]");
  private TranslationModal translationModal;

  public PropertyPage(WebDriver driver) {
    super(driver);
    waitUntilVisibilityOfElementByLocator(propertyHeaderPath);
    //close modal right after loading the page as this modal is not needed
    translationModal = new TranslationModal(driver);
    translationModal.closeModalIfPresent();
  }

  public void clickShowAmenitiesButton() {
    clickElementByLocator(showAmenitiesButtonPath);
  }

  public boolean isPoolElementDisplayed() {
    return waitUntilVisibilityOfElementByLocator(poolAmenitiesTextPath).isDisplayed();
  }
}
