package com.example.uitests.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TranslationModal extends BasePage {
  private final By closeModalButton = By.cssSelector("button[aria-label='Close']");

  public TranslationModal(WebDriver driver) {
    super(driver);
  }

  public void closeModalIfPresent(){
    try {
      WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3)); // Short wait for the popup
      shortWait.until(ExpectedConditions.visibilityOfElementLocated(closeModalButton));
      driver.findElement(closeModalButton).click();
    } catch (Exception ignored) {
    }
  }

}
