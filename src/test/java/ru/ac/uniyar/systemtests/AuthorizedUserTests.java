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

public class AuthorizedUserTests {
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

    @Test
    /**
     * Авторизованный пользователь может просматривать список ресторанов и каждый ресторан по отдельности
     */
    void canWatchRestaurants(){
        login();
        assertThat(driver.findElement(By.id("restaurants")).isDisplayed()).isEqualTo(true);
        driver.findElement(By.id("restaurant")).click();
        assertThat(driver.findElement(By.id("restaurantName")).isDisplayed()).isEqualTo(true);
    }
    @Test
    /**
     * Авторизованный пользователь может просматривать список блюд и каждое блюдо по отдельности
     */
    void canWatchDishes(){
        login();
        assertThat(driver.findElement(By.id("restaurants")).isDisplayed()).isEqualTo(true);
        driver.findElement(By.id("restaurant")).click();
        assertThat(driver.findElement(By.id("dishes")).isDisplayed()).isEqualTo(true);
    }

    @Test
    /**
     * Авторизованный пользователь может создать заказ и подтвердить его в корзине
     * Потвержденный заказ будет находиться в профиле заказчика
     */
    void canAddOrder(){
        login();
        driver.findElement(By.id("restaurant")).click();
        driver.findElement(By.id("addToBasket")).click();
        driver.findElement(By.id("basket")).click();
        driver.findElement(By.id("acceptOrder")).click();
        driver.findElement(By.id("current-username")).click();
        driver.findElement(By.id("orders")).click();
        assertThat(driver.findElement(By.id("listOfOrders")).isDisplayed()).isEqualTo(true);
    }

    @Test
    /**
     * Авторизованный пользователь может отказаться от подтвержденного заказа
     *
     *
     */
    void canRejectionOrder(){
        login();
        driver.findElement(By.id("restaurant")).click();
        driver.findElement(By.id("addToBasket")).click();
        driver.findElement(By.id("basket")).click();
        driver.findElement(By.id("acceptOrder")).click();
        driver.findElement(By.id("current-username")).click();
        driver.findElement(By.id("orders")).click();
        driver.findElement(By.id("expectedOrder")).click();
        driver.findElement(By.id("rejection-button")).click();
        driver.findElement(By.id("current-username")).click();
        driver.findElement(By.id("orders")).click();
        assertThat(driver.findElement(By.id("emptyList")).isDisplayed()).isEqualTo(true);
    }
    @Test
    /**
     * Авторизованный пользователь может смотреть корзину
     */
    void canWatchBasket(){
        login();
        driver.findElement(By.id("basket")).click();
    }
    @Test
    /**
     * Авторизованный пользователь может смотреть список всех прошлых заказов в профиле
     */
    void canWatchOrders(){
        login();
        driver.findElement(By.id("current-username")).click();
        driver.findElement(By.id("orders")).click();
    }
    @Test
    /**
     * Авторизованный пользователь может смотреть профиль
     */
    void canWatchProfile(){
        login();
        driver.findElement(By.id("current-username")).click();
    }
    @Test
    /**
     * Авторизованный пользователь может оставлять отзыв
     */
    void canGiveReview(){
        login();
        driver.findElement(By.id("restaurant")).click();
        driver.findElement(By.id("addToBasket")).click();
        driver.findElement(By.id("basket")).click();
        driver.findElement(By.id("acceptOrder")).click();
        driver.findElement(By.id("listofrestaurants")).click();
        driver.findElement(By.id("restaurant")).click();
        driver.findElement(By.id("review")).click();
        driver.findElement(By.id("giveReview")).click();
        WebElement text = driver.findElement(By.id("text"));
        text.sendKeys("Тестовый отзыв");
        driver.findElement(By.id("tableRating")).click();
        driver.findElement(By.id("3")).click();
        driver.findElement(By.id("submit-button")).click();

    }
    @Test
    /**
     * Авторизованный пользователь может редактировать свои данные
     */
    void canEditProfile(){
        login();
        driver.findElement(By.id("current-username")).click();
        driver.findElement(By.id("edit")).click();
        WebElement name = driver.findElement(By.id("name"));
        name.sendKeys("NewUser");
        WebElement phone = driver.findElement(By.id("phone"));
        phone.sendKeys("88005553535");
        WebElement email = driver.findElement(By.id("email"));
        email.sendKeys("new@mail.com");
        driver.findElement(By.id("submit-button")).click();
        assertThat(driver.findElement(By.id("current-username")).getText()).isEqualTo("NewUser");
    }


    void login(){
        driver.get("http://localhost:5000/login");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("Alice");
        WebElement password = driver.findElement(By.id("password-input-field"));
        password.sendKeys("123");
        driver.findElement(By.id("submit-button")).click();
    }
}
