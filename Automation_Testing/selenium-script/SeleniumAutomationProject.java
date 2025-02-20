package com.test;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class AutomationProjectSelenium {
    private static WebDriver driver;
    private static final String URL = "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";
    private static final String DRIVER_PATH = "C://drivers//chromedriver.exe";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    
    private static final By MAIN_HEADING = By.className("mainHeading");
    private static final By LOGIN_BUTTON = By.xpath("//button[@ng-click='customer()']");
    private static final By USER_DROPDOWN = By.id("userSelect");
    private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit']");
    private static final By WELCOME_NAME = By.cssSelector(".fontBig.ng-binding");
    private static final By CURRENCY_DISPLAY = By.xpath("//div[@ng-hide='noAccount']");
    private static final By DEPOSIT_BUTTON = By.xpath("//button[@ng-class='btnClass2']");
    private static final By AMOUNT_INPUT = By.xpath("//input[@type='number']");
    private static final By SUCCESS_MESSAGE = By.cssSelector(".error.ng-binding");
    private static final By TRANSACTIONS_BUTTON = By.xpath("//button[@ng-click='transactions()']");
    private static final By TRANSACTION_ROWS = By.xpath("//tbody//tr");
    private static final By BACK_BUTTON = By.xpath("//button[@ng-click='back()']");
    private static final By WITHDRAWAL_BUTTON = By.xpath("//button[@ng-click='withdrawl()']");

    public static void main(String[] args) {
        setupDriver();
        loginAsCustomer("Hermoine Granger");
        performDeposit("100");
        performWithdrawal("100");
        driver.quit();
    }

    private static void setupDriver() {
        System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
        ChromeOptions options = new ChromeOptions().addArguments("--disable-search-engine-choice-screen", "--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(TIMEOUT);
        driver.get(URL);
    }

    private static void loginAsCustomer(String customerName) {
        Assert.assertEquals(getElementText(MAIN_HEADING), "XYZ Bank", "Title is mismatched");
        driver.findElement(LOGIN_BUTTON).click();
        selectCustomerName(customerName);
        driver.findElement(SUBMIT_BUTTON).click();
        Assert.assertEquals(getElementText(WELCOME_NAME), customerName, "Name is mismatched");
        Assert.assertTrue(getElementText(CURRENCY_DISPLAY).contains("Dollar"), "Currency is not Dollar");
    }

    private static void selectCustomerName(String customerName) {
        Select userSelect = new Select(driver.findElement(USER_DROPDOWN));
        userSelect.selectByVisibleText(customerName);
    }

    private static void performDeposit(String amount) {
        clickTransactions();
        clickDeposit();
        enterAmount(amount);
        submitDeposit();
        assertSuccessMessage("Deposit Successful");
        validateTransaction(amount, "Credit");
    }

    private static void performWithdrawal(String amount) {
        clickBack();
        clickWithdrawal();
        enterAmount(amount);
        submitDeposit();
        assertSuccessMessage("Transaction successful");
        validateTransaction(amount, "Debit");
    }

    private static void clickTransactions() {
        driver.findElement(TRANSACTIONS_BUTTON).click();
    }

    private static void clickDeposit() {
        driver.findElement(DEPOSIT_BUTTON).click();
    }

    private static void enterAmount(String amount) {
        WebElement amountInput = driver.findElement(AMOUNT_INPUT);
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    private static void submitDeposit() {
        driver.findElement(SUBMIT_BUTTON).click();
    }

    private static void assertSuccessMessage(String expectedMessage) {
        String actualMessage = getElementText(SUCCESS_MESSAGE);
        Assert.assertTrue(actualMessage.contains(expectedMessage), "Success message is not displayed as expected");
    }

    private static void validateTransaction(String amount, String type) {
        clickTransactions();
        List<WebElement> rows = driver.findElements(TRANSACTION_ROWS);
        Assert.assertTrue(rows.stream().anyMatch(row -> row.getText().contains(amount) && row.getText().contains(type)),
                "Transaction not found in the records.");
    }

    private static void clickBack() {
        driver.findElement(BACK_BUTTON).click();
    }

    private static void clickWithdrawal() {
        driver.findElement(WITHDRAWAL_BUTTON).click();
    }

    private static String getElementText(By locator) {
        return new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }
}
