package dao;

import models.Review;
import org.sql2o.*;
import org.junit.*;
import java.util.List;

import static org.junit.Assert.*;

public class Sql2oReviewDaoTest {
    private Sql2oReviewDao reviewDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
    }

    public Review setupNewReview(){
        return new Review("This place is great.","Bob Smith", 5, 1);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void add() {
        Review review = setupNewReview();
        int originalReviewId = review.getId();
        reviewDao.add(review);
        assertNotEquals(originalReviewId, review.getId());
    }

    @Test
    public void getAll() {
        Review review = setupNewReview();
        Review review2 = setupNewReview();
        Review review3 = setupNewReview();
        reviewDao.add(review);
        reviewDao.add(review2);
        reviewDao.add(review3);
        assertEquals(3, reviewDao.getAll().size());
    }

    @Test
    public void getAllReviewsByRestaurantId() {
        Review review = setupNewReview();
        int restuarantId = review.getRestaurantId();
        Review review1 = setupNewReview();
        Review review2 = new Review("This place is awful.","Bill Smith", 1, restuarantId);
        Review review3 = new Review("This place is ok.","Brad Smith", 3, restuarantId);
        reviewDao.add(review3);
        reviewDao.add(review2);
        assertEquals(2, reviewDao.getAllReviewsByRestaurantId(restuarantId).size());
        assertTrue(reviewDao.getAllReviewsByRestaurantId(restuarantId).contains(review3));
        assertFalse(reviewDao.getAllReviewsByRestaurantId(restuarantId).contains(review1));

    }

    @Test
    public void deleteById() {
        Review review = setupNewReview();
        Review review2 = setupNewReview();
        Review review3 = setupNewReview();
        reviewDao.add(review);
        reviewDao.add(review2);
        reviewDao.add(review3);
        reviewDao.deleteById(2);
        assertEquals(2, reviewDao.getAll().size());
        assertTrue(reviewDao.getAll().contains(review));
        assertFalse(reviewDao.getAll().contains(review2));

    }

    @Test
    public void clearAll() {
        Review review = setupNewReview();
        Review review2 = setupNewReview();
        Review review3 = setupNewReview();
        reviewDao.add(review);
        reviewDao.add(review2);
        reviewDao.add(review3);
        reviewDao.clearAll();
        assertEquals(0, reviewDao.getAll().size());
    }
}