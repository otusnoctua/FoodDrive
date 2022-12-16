package ru.ac.uniyar.systemtests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.http4k.server.Http4kServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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
    void canAddRecord(){

    }
}
