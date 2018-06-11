package dao;

import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
    private Sql2oRestaurantDao restaurantDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    public Restaurant setupNewRestaurant() {
        return new Restaurant("IHOb", "123 burger ln.", "12345", "555-555-5555", "www.ihob.com", "burgers@ihob.com");
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void add() {
        Restaurant restaurant = setupNewRestaurant();
        int initialId = restaurant.getId();
        restaurantDao.add(restaurant);
        assertNotEquals(initialId, restaurant.getId());
    }

    @Test
    public void getAll() {
        Restaurant restaurant = setupNewRestaurant();
        Restaurant restaurant2 = setupNewRestaurant();
        restaurantDao.add(restaurant);
        restaurantDao.add(restaurant2);
        assertEquals(2, restaurantDao.getAll().size());
    }

    @Test
    public void findById() {
        Restaurant restaurant = setupNewRestaurant();
        restaurantDao.add(restaurant);
        Restaurant foundRestaurant = restaurantDao.findById(restaurant.getId());
        assertEquals(restaurant, foundRestaurant);
    }

    @Test
    public void update() {
        Restaurant restaurant = setupNewRestaurant();
        restaurantDao.add(restaurant);
        int idOfRestaurant = restaurant.getId();
        restaurantDao.update(idOfRestaurant, "Wendy's", "124 burger lane", "97214", "503-333-4444", "www.wendys.com", "info@wendys.com");
        assertEquals("Wendy's", restaurantDao.findById(idOfRestaurant).getName());
    }

    @Test
    public void deleteById() {
        Restaurant restaurant = setupNewRestaurant();
        Restaurant restaurant2 = setupNewRestaurant();
        Restaurant restaurant3 = setupNewRestaurant();
        restaurantDao.add(restaurant);
        restaurantDao.add(restaurant2);
        restaurantDao.add(restaurant3);
        restaurantDao.deleteById(2);
        assertEquals(2, restaurantDao.getAll().size());
        assertTrue(restaurantDao.getAll().contains(restaurant));
        assertFalse(restaurantDao.getAll().contains(restaurant2));
    }

    @Test
    public void clearAll() {
        Restaurant restaurant = setupNewRestaurant();
        Restaurant restaurant2 = setupNewRestaurant();
        Restaurant restaurant3 = setupNewRestaurant();
        restaurantDao.add(restaurant);
        restaurantDao.add(restaurant2);
        restaurantDao.add(restaurant3);
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }
}