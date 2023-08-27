package com.sg.moviesindex.model.yts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Meta implements Serializable, Parcelable {

    @SerializedName("server_time")
    @Expose
    private Long serverTime;
    @SerializedName("server_timezone")
    @Expose
    private String serverTimezone;
    @SerializedName("api_version")
    @Expose
    private Long apiVersion;
    @SerializedName("execution_time")
    @Expose
    private String executionTime;
    public final static Parcelable.Creator<Meta> CREATOR = new Creator<Meta>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Meta createFromParcel(Parcel in) {
            return new Meta(in);
        }

        public Meta[] newArray(int size) {
            return (new Meta[size]);
        }

    };
    private final static long serialVersionUID = 5939424027146566642L;

    protected Meta(Parcel in) {
        this.serverTime = ((Long) in.readValue((Long.class.getClassLoader())));
        this.serverTimezone = ((String) in.readValue((String.class.getClassLoader())));
        this.apiVersion = ((Long) in.readValue((Long.class.getClassLoader())));
        this.executionTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Meta() {
    }

    /**
     * @param executionTime
     * @param apiVersion
     * @param serverTimezone
     * @param serverTime
     */
    public Meta(Long serverTime, String serverTimezone, Long apiVersion, String executionTime) {
        super();
        this.serverTime = serverTime;
        this.serverTimezone = serverTimezone;
        this.apiVersion = apiVersion;
        this.executionTime = executionTime;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

    public String getServerTimezone() {
        return serverTimezone;
    }

    public void setServerTimezone(String serverTimezone) {
        this.serverTimezone = serverTimezone;
    }

    public Long getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(Long apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(serverTime);
        dest.writeValue(serverTimezone);
        dest.writeValue(apiVersion);
        dest.writeValue(executionTime);
    }

    public int describeContents() {
        return 0;
    }

}
