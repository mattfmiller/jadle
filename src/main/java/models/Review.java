package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Review implements Comparable<Review> {
    private String content;
    private String writtenBy;
    private int rating;
    private int id;
    private int restaurantId;
    private long createdat;
    private String formattedCreatedAt;

    public Review(String content, String writtenBy, int rating, int restaurantId) {
        this.content = content;
        this.writtenBy = writtenBy;
        this.rating = rating;
        this.restaurantId = restaurantId;
        this.createdat = System.currentTimeMillis();
        setFormattedCreatedAt();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWrittenBy() {
        return writtenBy;
    }

    public void setWrittenBy(String writtenBy) {
        this.writtenBy = writtenBy;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public long getCreatedat() {
        return createdat;
    }

    public void setCreatedat() {
        this.createdat = System.currentTimeMillis();
    }

    public String getFormattedCreatedAt(){
        Date date = new Date(createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a"; //see https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        return sdf.format(date);
    }

    public void setFormattedCreatedAt(){
        Date date = new Date(this.createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        this.formattedCreatedAt = sdf.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating &&
                id == review.id &&
                restaurantId == review.restaurantId &&
                Objects.equals(content, review.content) &&
                Objects.equals(writtenBy, review.writtenBy);
    }

    @Override
    public int hashCode() {

        return Objects.hash(content, writtenBy, rating, id, restaurantId);
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;

    }

    @Override
    public int compareTo(Review reviewObject) {
        if (this.createdat < reviewObject.createdat)
        {
            return -1; //this object was made earlier than the second object.
        }
        else if (this.createdat > reviewObject.createdat){ //this object was made later than the second object
            return 1;
        }
        else {
            return 0; //they were made at the same time, which is very unlikely, but mathematically not impossible.
        }
    }
}