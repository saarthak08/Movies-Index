package com.sg.moviesindex.model.yts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Torrent implements Serializable, Parcelable {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("quality")
    @Expose
    private String quality;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("seeds")
    @Expose
    private Long seeds;
    @SerializedName("peers")
    @Expose
    private Long peers;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("size_bytes")
    @Expose
    private Long sizeBytes;
    @SerializedName("date_uploaded")
    @Expose
    private String dateUploaded;
    @SerializedName("date_uploaded_unix")
    @Expose
    private Long dateUploadedUnix;
    public final static Parcelable.Creator<Torrent> CREATOR = new Creator<Torrent>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Torrent createFromParcel(Parcel in) {
            return new Torrent(in);
        }

        public Torrent[] newArray(int size) {
            return (new Torrent[size]);
        }

    };
    private final static long serialVersionUID = -8840109725450133141L;

    protected Torrent(Parcel in) {
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.hash = ((String) in.readValue((String.class.getClassLoader())));
        this.quality = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.seeds = ((Long) in.readValue((Long.class.getClassLoader())));
        this.peers = ((Long) in.readValue((Long.class.getClassLoader())));
        this.size = ((String) in.readValue((String.class.getClassLoader())));
        this.sizeBytes = ((Long) in.readValue((Long.class.getClassLoader())));
        this.dateUploaded = ((String) in.readValue((String.class.getClassLoader())));
        this.dateUploadedUnix = ((Long) in.readValue((Long.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Torrent() {
    }

    /**
     * @param dateUploadedUnix
     * @param size
     * @param seeds
     * @param peers
     * @param dateUploaded
     * @param type
     * @param url
     * @param hash
     * @param quality
     * @param sizeBytes
     */
    public Torrent(String url, String hash, String quality, String type, Long seeds, Long peers, String size, Long sizeBytes, String dateUploaded, Long dateUploadedUnix) {
        super();
        this.url = url;
        this.hash = hash;
        this.quality = quality;
        this.type = type;
        this.seeds = seeds;
        this.peers = peers;
        this.size = size;
        this.sizeBytes = sizeBytes;
        this.dateUploaded = dateUploaded;
        this.dateUploadedUnix = dateUploadedUnix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSeeds() {
        return seeds;
    }

    public void setSeeds(Long seeds) {
        this.seeds = seeds;
    }

    public Long getPeers() {
        return peers;
    }

    public void setPeers(Long peers) {
        this.peers = peers;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Long getDateUploadedUnix() {
        return dateUploadedUnix;
    }

    public void setDateUploadedUnix(Long dateUploadedUnix) {
        this.dateUploadedUnix = dateUploadedUnix;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
        dest.writeValue(hash);
        dest.writeValue(quality);
        dest.writeValue(type);
        dest.writeValue(seeds);
        dest.writeValue(peers);
        dest.writeValue(size);
        dest.writeValue(sizeBytes);
        dest.writeValue(dateUploaded);
        dest.writeValue(dateUploadedUnix);
    }

    public int describeContents() {
        return 0;
    }

}
