
package com.ww.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "noun",
    "verb"
})
public class ThesaurusResponse {

    @JsonProperty("noun")
    private Noun noun;
    @JsonProperty("verb")
    private Verb verb;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("noun")
    public Noun getNoun() {
        return noun;
    }

    @JsonProperty("noun")
    public void setNoun(Noun noun) {
        this.noun = noun;
    }

    @JsonProperty("verb")
    public Verb getVerb() {
        return verb;
    }

    @JsonProperty("verb")
    public void setVerb(Verb verb) {
        this.verb = verb;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
