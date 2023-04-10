package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.entity.Inventory;
import seedu.address.model.entity.Mob;
import seedu.address.model.entity.Name;
import seedu.address.model.entity.Stats;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Mob}
 */
public class JsonAdaptedMob {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Character's %s field is missing!";

    private final String name;
    private final JsonAdaptedStats stats;
    private final JsonAdaptedInventory inventory;
    private final float challengeRating;
    private final boolean isLegendary;
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedMob} with the given mob details.
     */
    @JsonCreator
    JsonAdaptedMob(@JsonProperty("name") String name, @JsonProperty("stats") JsonAdaptedStats stats,
            @JsonProperty("inventory") JsonAdaptedInventory inventory,
            @JsonProperty("challengeRating") float challengeRating,
            @JsonProperty("isLegendary") boolean isLegendary,
            @JsonProperty("tagged") List<JsonAdaptedTag> tagged) {
        this.name = name;
        this.stats = stats;
        this.inventory = inventory;
        this.challengeRating = challengeRating;
        this.isLegendary = isLegendary;
        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
    }

    /**
     * Converts a given {@code Mob} into this class for Jackson use.
     */
    public JsonAdaptedMob(Mob source) {
        name = source.getName().fullName;
        stats = new JsonAdaptedStats(source.getStats());
        inventory = new JsonAdaptedInventory(source.getInventory());
        challengeRating = source.getChallengeRating();
        isLegendary = source.getLegendaryStatus();
        tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted Item object into the model's {@code Mob} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted item.
     */
    public Mob toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }

        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }

        final Name modelName = new Name(name);

        Stats stat = stats.toModalType();

        Inventory inventory = this.inventory.toModelType();

        final List<Tag> tags = new ArrayList<>();
        for (JsonAdaptedTag tag : tagged) {
            tags.add(tag.toModelType());
        }

        final Set<Tag> modelTags = new HashSet<>(tags);
        return new Mob(modelName, stat, challengeRating, isLegendary, inventory, modelTags);
    }
}