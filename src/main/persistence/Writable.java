package persistence;

import org.json.JSONObject;

/**
 * Represents an interface for objects that can be converted to JSON format.
 * Any class implementing this interface should define how its data
 * is transformed into a JSON object.
 */

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
