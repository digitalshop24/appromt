package by.digitalshop.quests.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by CoolerBy on 09.02.2017.
 */
@Entity
public class SearchPopularItem {
    @Id(autoincrement = true)
    private Long id;
    private double latitude;
    private double longitude;
    private String title;
    private String placeId;

    @Keep
    public SearchPopularItem(double latitude, double longitude, String title,
                          String text) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.placeId = text;
    }

    @Generated(hash = 1910484267)
    public SearchPopularItem(Long id, double latitude, double longitude,
            String title, String placeId) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.placeId = placeId;
    }

    @Generated(hash = 1000083601)
    public SearchPopularItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
