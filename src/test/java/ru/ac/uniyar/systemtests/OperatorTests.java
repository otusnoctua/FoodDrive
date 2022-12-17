package ru.ac.uniyar.systemtests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.http4k.server.Http4kServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ac.uniyar.FoodDriveTestAppKt.createApplication;
import static ru.ac.uniyar.FoodDriveTestAppKt.fillingTables;

public class OperatorTests {
    private WebDriver driver;
    private Http4kServer app;

    @BeforeEach
    void initializeWebDriver(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        fillingTables();

    }
    @BeforeEach
    void startFoodDriveApp(){

        app = createApplication();
        app.start();


    }

    @AfterEach
    void finalizeWebDriver(){
        driver.quit();
        //deletingTables();
    }
    @AfterEach
    void closeFoodDriveApp(){
        app.close();

    }
    void createOperator(){
        driver.get("http://localhost:5000/login");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("admin");
        WebElement password = driver.findElement(By.id("password-input-field"));
        password.sendKeys("123");
        driver.findElement(By.id("submit-button")).click();
        ///////////////////////////////////////////////////////
        driver.findElement(By.id("addOperator")).click();
        driver.findElement(By.id("name")).sendKeys("TestOperator");
        driver.findElement(By.id("phone")).sendKeys("88005553535");
        driver.findElement(By.id("email")).sendKeys("operator@mail.com");
        driver.findElement(By.id("pass1")).sendKeys("123");
        driver.findElement(By.id("pass2")).sendKeys("123");
        driver.findElement(By.id("submit-button")).click();
        /////////////////////////////////////////////////////
        driver.get("http://localhost:5000/logout");
        driver.get("http://localhost:5000/login");
        driver.findElement(By.id("login-input-field")).sendKeys("TestOperator");
        driver.findElement(By.id("password-input-field")).sendKeys("123");
        driver.findElement(By.id("submit-button")).click();

    }
    void login(){
        driver.get("http://localhost:5000/login");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("Alice");
        WebElement password = driver.findElement(By.id("password-input-field"));
        password.sendKeys("123");
        driver.findElement(By.id("submit-button")).click();
    }
    void AcceptOrder(){
        login();
        driver.findElement(By.id("basket")).click();
        driver.findElement(By.id("acceptOrder")).click();
        driver.get("http://localhost:5000/logout");
        createOperator();

    }


    @Test
    /**
     * Оператор может просматривать список ресторанов и каждый ресторан по отдельности
     */
    void canWatchRestaurants(){
        createOperator();
        assertThat(driver.findElement(By.id("restaurants")).isDisplayed()).isEqualTo(true);
        driver.findElement(By.id("restaurant")).click();
        assertThat(driver.findElement(By.id("restaurantName")).isDisplayed()).isEqualTo(true);
    }
    @Test
    /**
     * Оператор может просматривать список блюд и каждое блюдо по отдельности
     */
    void canWatchDishes(){
       createOperator();
        assertThat(driver.findElement(By.id("restaurants")).isDisplayed()).isEqualTo(true);
        driver.findElement(By.id("restaurant")).click();
        assertThat(driver.findElement(By.id("dishes")).isDisplayed()).isEqualTo(true);
    }
    @Test
    /**
     * Оператор может просматривать список заказов и каждый заказ по отдельности
     */
    void canWatchOrders(){
        AcceptOrder();
        driver.findElement(By.id("orders")).click();
        assertThat(driver.findElement(By.id("orders")).isDisplayed()).isEqualTo(true);
        driver.findElement(By.id("info")).click();
        assertThat(driver.findElement(By.id("order")).isDisplayed()).isEqualTo(true);
    }
    @Test
    /**
     * Оператор может изменять статус заказа
     */
    void canEditStatus(){
        AcceptOrder();
        driver.findElement(By.id("orders")).click();
        driver.findElement(By.id("info")).click();
        driver.findElement(By.id("table")).click();
        driver.findElement(By.id("3")).click();
        driver.findElement(By.id("submit-button")).click();

    }
    @Test
    /**
     * Оператор может редактировать стоп-лист ресторана
     */
    void canEditStopList(){
        createOperator();
        driver.findElement(By.id("restaurant")).click();
        driver.findElement(By.id("remove")).click();
        driver.findElement(By.id("adding")).click();
    }


}
