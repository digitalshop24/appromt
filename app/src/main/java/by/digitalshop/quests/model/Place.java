package by.digitalshop.quests.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by CoolerBy on 08.04.2017.
 */
@Entity
public class Place {

    @Id(autoincrement = true)
    private Long id;
    private String placeId;

    @Keep
    public Place(String placeId) {
        this.placeId = placeId;
    }

    @Generated(hash = 8649721)
    public Place(Long id, String placeId) {
        this.id = id;
        this.placeId = placeId;
    }

    @Generated(hash = 1170019414)
    public Place() {
    }

    public String getPlaceId() {
        return placeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return placeId.equals(place.placeId);

    }

    @Override
    public int hashCode() {
        return placeId.hashCode();
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
