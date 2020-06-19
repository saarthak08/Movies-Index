package com.sg.moviesindex.model.yts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class APIResponse implements Serializable, Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("@meta")
    @Expose
    private Meta meta;
    public final static Parcelable.Creator<APIResponse> CREATOR = new Creator<APIResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public APIResponse createFromParcel(Parcel in) {
            return new APIResponse(in);
        }

        public APIResponse[] newArray(int size) {
            return (new APIResponse[size]);
        }

    };
    private final static long serialVersionUID = 8980920924593425494L;

    protected APIResponse(Parcel in) {
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.statusMessage = ((String) in.readValue((String.class.getClassLoader())));
        this.data = ((Data) in.readValue((Data.class.getClassLoader())));
        this.meta = ((Meta) in.readValue((Meta.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public APIResponse() {
    }

    /**
     * @param data
     * @param meta
     * @param statusMessage
     * @param status
     */
    public APIResponse(String status, String statusMessage, Data data, Meta meta) {
        super();
        this.status = status;
        this.statusMessage = statusMessage;
        this.data = data;
        this.meta = meta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeValue(statusMessage);
        dest.writeValue(data);
        dest.writeValue(meta);
    }

    public int describeContents() {
        return 0;
    }

}
