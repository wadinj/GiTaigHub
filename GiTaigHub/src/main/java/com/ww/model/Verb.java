
package com.ww.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "syn",
    "ant"
})
public class Verb {

    @JsonProperty("syn")
    private List<String> syn = null;
    @JsonProperty("ant")
    private List<String> ant = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("syn")
    public List<String> getSyn() {
        return syn;
    }

    @JsonProperty("syn")
    public void setSyn(List<String> syn) {
        this.syn = syn;
    }

    @JsonProperty("ant")
    public List<String> getAnt() {
        return ant;
    }

    @JsonProperty("ant")
    public void setAnt(List<String> ant) {
        this.ant = ant;
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
