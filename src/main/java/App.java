import com.google.gson.Gson;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import exceptions.ApiException;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        Sql2oFoodtypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");

        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();


        post("/restaurants/new", "application/json", (req, res) -> {
            Restaurant restaurant = gson.fromJson(req.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            res.status(201);
            return gson.toJson(restaurant);
        });

        get("/restaurants", "application/json", (req, res) -> { //accept a request in format JSON from an app
            if (restaurantDao.getAll().size() == 0) {
                return "{\"message\":\"I'm sorry, but no restaurants are currently listed in the database.\"}";
            } else {
                return gson.toJson(restaurantDao.getAll());
            }
        });

        get("/restaurants/:id", "application/json", (req, res) -> { //accept a request in format JSON from an app
            int restaurantId = Integer.parseInt(req.params("id"));
            return gson.toJson(restaurantDao.findById(restaurantId));
        });

        post("/foodtypes/new", "application/json", (req, res) -> {
            Foodtype foodtype = gson.fromJson(req.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            res.status(201);
            return gson.toJson(foodtype);
        });

        get("/foodtypes", "application/json", (req, res) -> {
            return gson.toJson(foodtypeDao.getAll());
        });

        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCreatedat();
            review.setRestaurantId(restaurantId);
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

        get("/restaurants/:id/reviews", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            return gson.toJson(reviewDao.getAllReviewsByRestaurantId(restaurantId));
        });

        delete("/restaurants/delete", "application/json", (req, res) -> {
            restaurantDao.clearAll();
            return gson.toJson(restaurantDao.getAll());
        });

        post("/restaurants/:restaurantId/foodtypes/:foodtypeId", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            int foodtypeId = Integer.parseInt(req.params("foodtypeId"));
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            Foodtype foodtype = foodtypeDao.findById(foodtypeId);

            if (restaurant != null && foodtype != null){
                //both exist and can be associated - we should probably not connect things that are not here.
                foodtypeDao.addFoodtypeToRestaurant(foodtype, restaurant);
                res.status(201);
                return gson.toJson(String.format("Restaurant '%s' and Foodtype '%s' have been associated",restaurant.getName(), foodtype.getName()));
            }
            else {
                throw new ApiException(404, String.format("Restaurant or Foodtype does not exist"));
            }
        });

        get("/restaurants/:id/foodtypes", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            }
            else if (restaurantDao.getAllFoodtypesByRestaurantId(restaurantId).size()==0){
                return "{\"message\":\"I'm sorry, but no foodtypes are listed for this restaurant.\"}";
            }
            else {
                return gson.toJson(restaurantDao.getAllFoodtypesByRestaurantId(restaurantId));
            }
        });

        get("/foodtypes/:id/restaurants", "application/json", (req, res) -> {
            int foodtypeId = Integer.parseInt(req.params("id"));
            Foodtype foodtypeToFind = foodtypeDao.findById(foodtypeId);
            if (foodtypeToFind == null){
                throw new ApiException(404, String.format("No foodtype with the id: \"%s\" exists", req.params("id")));
            }
            else if (foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId).size()==0){
                return "{\"message\":\"I'm sorry, but no restaurants are listed for this foodtype.\"}";
            }
            else {
                return gson.toJson(foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId));
            }
        });

        get("/restaurants/:id/sortedReviews", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            }
            allReviews = reviewDao.getAllReviewsByRestaurantIdSortedNewestToOldest(restaurantId);
            return gson.toJson(allReviews);
        });

        exception(ApiException.class, (exception, req, res) -> {
            ApiException err = (ApiException) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatusCode());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json");
            res.status(err.getStatusCode());
            res.body(gson.toJson(jsonMap));
        });



        after((req, res) ->{
            res.type("application/json");
        });
    }
}