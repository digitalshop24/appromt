package by.digitalshop.quests.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by CoolerBy on 27.12.2016.
 */

@Entity
public class MapQuestMarker {
    @Id(autoincrement = true)
    private Long id;
    private double latitude;
    private double longitude;
    private String title;
    private String text;

    @Keep
    public MapQuestMarker(double latitude, double longitude, String title,
                          String text) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.text = text;
    }

    @Generated(hash = 995391519)
    public MapQuestMarker() {
    }

    @Generated(hash = 1976098379)
    public MapQuestMarker(Long id, double latitude, double longitude, String title,
                          String text) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.text = text;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MapQuestMarker{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
