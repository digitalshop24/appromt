package by.digitalshop.quests.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by CoolerBy on 27.12.2016.
 */

@Entity
public class MapQuestMarker {
    private double latitude;
    private double longitude;
    private String title;
    private String text;

    @Generated(hash = 419292350)
    public MapQuestMarker(double latitude, double longitude, String title, String text) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.text = text;
    }

    @Generated(hash = 995391519)
    public MapQuestMarker() {
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

    @Override
    public String toString() {
        return "MapQuestMarker{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
