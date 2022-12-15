package ru.ac.uniyar;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class tests {

//    public DataSource mysqlDataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/fooddrivetest?useSSL=false");
//        dataSource.setUsername("root");
//        dataSource.setPassword("12345");
//        return dataSource;
//    }
//
//    private JdbcTemplate jdbcTemplate;
//    jdbcTemplate = new JdbcTemplate(nameOfClass.mysqlDataSource());
//
//    jdbcTemplate.update("")


    @Test
    void addNewRestaurant(){
        //add rest
        //try to fetch by id
    }

    @Test
    void addNewUser(){

    }

    @Test
    void addNewOrder(){

    }

    @Test
    void addNewDish(){

    }

    @Test
    void addNewReview(){}



    @Test
    void newUserIsClientByDefault(){

    }


    @Test
    void deleteRestaurant(){
        //add rest
        //try to fetch by id
    }

    @Test
    void deleteUser(){

    }

    @Test
    void deleteOrder(){

    }

    @Test
    void deleteDish(){

    }

    @Test
    void deleteReview(){}

    @Test
    void deleteDishFromOrder(){}

}
