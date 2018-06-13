package dao;

import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;

public class Sql2oReviewDao implements ReviewDao {
    private final Sql2o sql2o;
    public Sql2oReviewDao(Sql2o sql2o) { this.sql2o = sql2o; }

    @Override
    public void add(Review review) {
        String sql = "INSERT INTO reviews (writtenby, content, rating, restaurantid, createdat) VALUES (:writtenBy, :content, :rating, :restaurantId, :createdat)"; //if you change your model, be sure to update here as well!
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .bind(review)
                    .executeUpdate()
                    .getKey();
            review.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Review> getAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM reviews")
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> getAllReviewsByRestaurantId(int restaurantId) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM reviews WHERE restaurantId = :restaurantId")
                    .addParameter("restaurantId", restaurantId)
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from reviews WHERE id=:id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void clearAll() {
        String sql = "DELETE from reviews";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql).executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Review> getAllReviewsByRestaurantIdSortedNewestToOldest(int restaurantId) {
        List<Review> reviews = getAllReviewsByRestaurantId(restaurantId);
        if ( reviews.size() > 1) {
            for (Integer i=0; i < reviews.size(); i++) {
                for (Integer index = 0; index < reviews.size() -1; index ++) {
                    Review review1 = reviews.get(index);
                    Review review2 = reviews.get(index + 1);
                    int compareResult = review1.compareTo(reviews.get(index + 1));
                    if (compareResult == -1) {
                        reviews.set(index, review2);
                        reviews.set(index + 1, review1);
                    }
                }
            }
        }
        return reviews;
    }
}
