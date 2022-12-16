package ru.ac.uniyar.systemtests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.http4k.server.Http4kServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static ru.ac.uniyar.FoodDriveTestAppKt.*;

public class UnauthorizedUserTests {
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
        deletingTables();
    }
    @AfterEach
    void closeFoodDriveApp(){
        app.close();

    }
    /**
     * Вход пользователя в аккаунт не выполнен
     * При переходе по адресу /login
     * есть возможность ввести действительные логин и пароль и стать авторизованным пользователем
     */
    @Test
    void canAuthorize(){
        driver.get("http://localhost:5000/login");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("Alice");
        WebElement password = driver.findElement(By.id("password-input-field"));
        password.sendKeys("123");
        driver.findElement(By.id("submit-button")).click();

        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5000/restaurants");
        assertThat(driver.findElement(By.id("current-username")).getText()).isEqualTo("Alice");

    }

    @Test
    /**
     * Вход пользователя в аккаунт не выполнен
     * При переходе по адресу /register
     * есть возможность создать новый аккаунт,
     * чтобы в дальнейшем зайти в него и стать авторизованным пользователем
     */

    void canRegister(){
      registerTestUser();
        driver.get("http://localhost:5000/login");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("TestUser1");
        WebElement password = driver.findElement(By.id("password-input-field"));
        password.sendKeys("user");
        driver.findElement(By.id("submit-button")).click();
        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5000/restaurants");
        assertThat(driver.findElement(By.id("current-username")).getText()).isEqualTo("TestUser1");
    }
    @Test
    /**
     *Неавторизованный пользователь не видит корзину
     */
    void notVisibleBasket(){
        driver.get("http://localhost:5000/restaurants");
        assertThatThrownBy(() -> {
            driver.findElement(By.id("basket"));
        }).isInstanceOf(NoSuchElementException.class);

    }

    @Test
    /**
     * Неавторизованный пользователь не может добавлять в корзину блюда
     */
    void canNotAddDish(){
        driver.get("http://localhost:5000/restaurants");
        driver.findElement(By.id("restaurant")).click();
        assertThatThrownBy(() -> {
            driver.findElement(By.id("addToBasket"));
        }).isInstanceOf(NoSuchElementException.class);
        assertThat(driver.findElement(By.id("imageUrl")).isDisplayed()).isEqualTo(true);
    }

    void registerTestUser(){
        driver.get("http://localhost:5000/register");
        WebElement login = driver.findElement(By.id("login-input-field"));
        login.sendKeys("TestUser1");
        WebElement phone = driver.findElement(By.id("phone-input-field"));
        phone.sendKeys("89159230760");
        WebElement mail = driver.findElement(By.id("mail-input-field"));
        mail.sendKeys("testmail@mail.ru");
        WebElement password = driver.findElement(By.id("pass-input-field"));
        password.sendKeys("user");
        WebElement passwordRepeat = driver.findElement(By.id("replay-pass-input-field"));
        passwordRepeat.sendKeys("user");
        driver.findElement(By.id("submit-button")).click();

    }
}
