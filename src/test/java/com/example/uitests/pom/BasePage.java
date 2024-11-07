package com.example.uitests.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public abstract class BasePage {
  protected WebDriver driver;
  //wait for a maximum 15 seconds, checking every 0.5 sec
  private final FluentWait<WebDriver> wait;

  protected BasePage(WebDriver driver) {
    this.driver = driver;
    wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(15))
        .pollingEvery(Duration.ofMillis(500))
        .ignoring(NoSuchElementException.class);
  }

  protected void clickElementByLocator(By locator) {
    // wait until element is visible, then clickable
    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    wait.until(ExpectedConditions.elementToBeClickable(element));
    element.click();
  }

  protected WebElement waitUntilVisibilityOfElementByLocator(By locator){
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  protected void sendKeysToElementByLocator(By locator, String keys) {
    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    element.sendKeys(keys);
  }

  protected void wait(int t) {
    try {
      Thread.sleep(t);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
