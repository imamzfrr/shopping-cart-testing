package com.periplus.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class PeriplusCartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://www.periplus.com/";
    private final String EMAIL = "imamzfrr08@gmail.com"; // Replace with your test account email
    private final String PASSWORD = "Rt0011rw007"; // Replace with your test account password
    private final String SEARCH_QUERY = "Python Programming";

    @BeforeTest
    public void setup() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testAddProductToCart() {
        // Navigate to Periplus
        driver.get(BASE_URL);
        System.out.println("Navigated to " + BASE_URL);

        // Click Sign In
        WebElement signInLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign In")));
        signInLink.click();

        // Enter login credentials
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailField.sendKeys(EMAIL);
        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.sendKeys(PASSWORD);
        WebElement loginButton = driver.findElement(By.id("button-login"));
        loginButton.click();
        System.out.println("Logged in with email: " + EMAIL);


        // Search for a product directly from Your Account page
        try {
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("filter_name")));
            searchBox.sendKeys(SEARCH_QUERY);
            searchBox.submit();
            System.out.println("Searched for: " + SEARCH_QUERY + " from Your Account page");
        } catch (Exception e) {
            System.err.println("Search box not found on Your Account page: " + e.getMessage());
            // Fallback: Navigate to homepage
            System.out.println("Falling back to homepage");
            driver.get(BASE_URL);
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
            searchBox.sendKeys(SEARCH_QUERY);
            searchBox.submit();
            System.out.println("Searched for: " + SEARCH_QUERY + " from homepage");
        }

        // Click the image of the "Python Programming" book
        try {
            // Locate the parent <a> tag containing the image with class="hover-img" and the book's ISBN
            WebElement productImageLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href, '9798890026538') and .//img[@class='hover-img']]")
            ));
            // Get the product name from the text link for verification later
            WebElement productTextLink = driver.findElement(By.xpath("//a[contains(@href, '9798890026538') and contains(text(), 'Python Programming')]"));
            String productName = productTextLink.getText().trim();
            System.out.println("Product name found: " + productName);
            productImageLink.click();
            System.out.println("Clicked product image for: " + productName);
        } catch (Exception e) {
            System.err.println("Failed to select product image: " + e.getMessage());
            System.out.println("Page source: " + driver.getPageSource());
            throw new RuntimeException("Product image selection failed");
        }

        // Verify product page
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Python Programming')]")));
        Assert.assertTrue(productTitle.isDisplayed(), "Product page not loaded");
        System.out.println("Confirmed on product page");

        // Add to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-add-to-cart")));
        addToCartButton.click();
        System.out.println("Clicked Add to Cart");

        // Navigate to cart
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/checkout/cart')]")));
        cartLink.click();
        System.out.println("Navigated to cart");

        // Verify product in cart
        WebElement cartProduct = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='shopping-item']//a[contains(@href, '9798890026538')]")
        ));
        Assert.assertTrue(cartProduct.isDisplayed(), "Product 'Python Programming' not found in cart");
        System.out.println("Verified product 'Python Programming' in cart");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}