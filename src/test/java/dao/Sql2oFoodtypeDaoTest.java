package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.*;
import org.junit.*;

import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {
    private Sql2oFoodtypeDao foodtypeDao;
    private Sql2oRestaurantDao restaurantDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    public Foodtype setupNewFoodtype(){
        return new Foodtype("donuts");
    }

    public Restaurant setupNewRestaurant() {
        return new Restaurant("IHOb", "123 burger ln.", "12345", "555-555-5555", "www.ihob.com", "burgers@ihob.com");
    }

    public Restaurant setupAltRestaurant() {
        return new Restaurant("Wendy's", "124 burger lane", "97214", "503-333-4444", "www.wendys.com", "info@wendys.com");
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void add() {
        Foodtype foodtype = setupNewFoodtype();
        int idofOriginal = foodtype.getId();
        foodtypeDao.add(foodtype);
        assertNotEquals(idofOriginal, foodtype.getId());
    }

    @Test
    public void getAll() {
        Foodtype foodtype = setupNewFoodtype();
        Foodtype foodtype2 = setupNewFoodtype();
        foodtypeDao.add(foodtype);
        foodtypeDao.add(foodtype2);
        assertEquals(2, foodtypeDao.getAll().size());

    }

    @Test
    public void deleteById() {
        Foodtype foodtype = setupNewFoodtype();
        Foodtype foodtype2 = setupNewFoodtype();
        foodtypeDao.add(foodtype);
        foodtypeDao.add(foodtype2);
        foodtypeDao.deleteById(1);
        assertEquals(1, foodtypeDao.getAll().size());
    }

    @Test
    public void clearAll() {
        Foodtype foodtype = setupNewFoodtype();
        Foodtype foodtype2 = setupNewFoodtype();
        foodtypeDao.add(foodtype);
        foodtypeDao.add(foodtype2);
        foodtypeDao.clearAll();
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupNewRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }



    @Test
    public void deleteingFoodTypeAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype testFoodtypeTwo =new Foodtype("American");
        foodtypeDao.add(testFoodtypeTwo);

        Restaurant testRestaurant = setupNewRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtypeTwo, altRestaurant);

        foodtypeDao.deleteById(testFoodtype.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }
}