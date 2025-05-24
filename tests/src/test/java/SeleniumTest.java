import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.net.MalformedURLException;

public class SeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "https://admin-demo.nopcommerce.com";

    private final By emailInput = By.id("Email");
    private final By passwordInput = By.id("Password");
    private final By loginButton = By.xpath("//button[text()='Log in']");

    private final By logoutLink = By.xpath(
        "//li[contains(@class,'nav-item')]" +
        "//a[@href='/logout' and text()='Logout']"
    );

    private final String dashboardTitle = "Dashboard / nopCommerce administration";

    private final String addCustomerUrl = baseUrl + "/Admin/Customer/Create";

    private final By custEmailInput = By.id("Email");
    private final By custPasswordInput = By.id("Password");
    private final By firstNameInput = By.id("FirstName");
    private final By lastNameInput = By.id("LastName");
    private final By genderMaleRadio = By.id("Gender_Male");
    private final By companyInput = By.id("Company");
    private final By taxExemptCheckbox = By.id("IsTaxExempt");
    private final By adminCommentTextarea = By.id("AdminComment");
    private final By customerSaveButton = By.name("save");
    private final By successMessage = By.cssSelector(".alert-success");

    @Before
    public void setup() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        driver = new RemoteWebDriver(new URL("http://selenium:4444/wd/hub"), options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 10);
    }

    private WebElement waitAndFind(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElement(locator);
    }

    private void doLogin() {
        driver.get(baseUrl + "/login?ReturnUrl=%2Fadmin%2F");
        WebElement email = waitAndFind(emailInput);
        email.clear();
        email.sendKeys("admin@yourstore.com");

        WebElement pw = waitAndFind(passwordInput);
        pw.clear();
        pw.sendKeys("admin");

        waitAndFind(loginButton).click();
        wait.until(ExpectedConditions.titleIs(dashboardTitle));
    }

    @Test
    public void testLogin() {
        doLogin();
        assertEquals(dashboardTitle, driver.getTitle());
    }

    @Test
    public void testAddCustomerForm() {
        doLogin();

        driver.get(addCustomerUrl);

        waitAndFind(custEmailInput)
            .sendKeys("selenium+" + System.currentTimeMillis() + "@example.com");
        waitAndFind(custPasswordInput)
            .sendKeys("P@ssw0rd!");
        waitAndFind(firstNameInput)
            .sendKeys("Selenium");
        waitAndFind(lastNameInput)
            .sendKeys("Tester");
        waitAndFind(genderMaleRadio).click();
        waitAndFind(companyInput)
            .sendKeys("Acme Corp");
        WebElement taxExempt = waitAndFind(taxExemptCheckbox);
        if (!taxExempt.isSelected()) {
            taxExempt.click();
        }

        waitAndFind(adminCommentTextarea)
            .sendKeys("Automated customer created by Selenium test.");

        waitAndFind(customerSaveButton).click();

        WebElement alert = waitAndFind(successMessage);
        assertTrue(alert.getText()
            .contains("The new customer has been added successfully."));
    }

    @Test
    public void testLogout() {
        doLogin();
        waitAndFind(logoutLink).click();
        wait.until(ExpectedConditions.titleContains("Login"));
        assertTrue(driver.getTitle().toLowerCase().contains("login"));
    }

    @Test
    public void testStaticDashboardPage() {
        doLogin();
        driver.get(baseUrl + "/");
        assertEquals(dashboardTitle, driver.getTitle());
        WebElement systemMenu =
            waitAndFind(By.xpath("//p[contains(text(),'System')]"));
        assertTrue(systemMenu.isDisplayed());
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
