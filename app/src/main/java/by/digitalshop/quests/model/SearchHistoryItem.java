package by.digitalshop.quests.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by CoolerBy on 09.02.2017.
 */
@Entity
public class SearchHistoryItem {
    @Id(autoincrement = true)
    private Long id;
    private double latitude;
    private double longitude;
    private String title;
    private String placeId;
    private long createDate;

    @Keep
    public SearchHistoryItem(double latitude, double longitude, String title,
                             String text,long createDate) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.placeId = text;
        this.createDate = createDate;
    }


    @Generated(hash = 1637555123)
    public SearchHistoryItem(Long id, double latitude, double longitude,
            String title, String placeId, long createDate) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.placeId = placeId;
        this.createDate = createDate;
    }


    @Generated(hash = 1958512265)
    public SearchHistoryItem() {
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


    public long getCreateDate() {
        return this.createDate;
    }


    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
