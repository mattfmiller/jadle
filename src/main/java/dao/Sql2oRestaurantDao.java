package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

public class Sql2oRestaurantDao implements RestaurantDao {

    private final Sql2o sql2o;
    public Sql2oRestaurantDao(Sql2o sql2o) { this.sql2o = sql2o; }

    @Override
    public void add(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, address, zipcode, phone, website, email) VALUES (:name, :address, :zipcode, :phone, :website, :email)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .bind(restaurant)
                    .executeUpdate()
                    .getKey();
            restaurant.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Restaurant> getAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM restaurants")
                    .executeAndFetch(Restaurant.class);
        }
    }

    @Override
    public Restaurant findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM restaurants WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Restaurant.class);
        }
    }

    @Override
    public void update(int id, String name, String address, String zipcode, String phone, String website, String email) {
        String sql = "UPDATE restaurants SET (name, address, zipcode, phone, website, email) = (:name, :address, :zipcode, :phone, :website, :email) WHERE id=:id";
        try(Connection con = sql2o.open()){
            con.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("address", address)
                    .addParameter("zipcode", zipcode)
                    .addParameter("phone", phone)
                    .addParameter("website", website)
                    .addParameter("email", email)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from restaurants WHERE id=:id";
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
        String sql = "DELETE from restaurants";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void addRestaurantToFoodtype(Restaurant restaurant, Foodtype foodtype) {

    }

    @Override
    public List<Foodtype> getAllFoodtypesByRestaurantId(int restaurantId) {
        List<Foodtype> foodtypes = new ArrayList(); //empty list
        return foodtypes;
    }
}
