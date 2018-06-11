package dao;

import models.Foodtype;
import org.sql2o.*;
import org.junit.*;

import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {
    private Sql2oFoodtypeDao foodtypeDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        conn = sql2o.open();
    }

    public Foodtype setupNewFoodtype(){
        return new Foodtype("donuts");
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
}