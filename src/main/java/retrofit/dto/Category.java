package retrofit.dto;

import lombok.Getter;

@Getter
public enum Category {

    FOOD(1, "Food"),
    ELECTRONIC(2, "Electronic");

    private final long id;
    private final String title;

    Category(long id, String title) {
        this.id = id;
        this.title = title;
    }

}
