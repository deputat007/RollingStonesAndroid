package com.rolling_stones.rollingstonesandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Group {

    @SerializedName("Id")
    private int mId;
    @SerializedName("Name")
    private String mName;
    @SerializedName("PhotoUrl")
    private String mPhotoUrl;
    @SerializedName("PhotoSmallUrl")
    private String mPhotoSmallUrl;

    @SerializedName("Members")
    private List<UserBase> mMembers;

    public Group() {
    }

    public Group(int id, String name) {
        mId = id;
        mName = name;
    }

    public Group(int id, String name, List<UserBase> members) {
        mId = id;
        mName = name;
        mMembers = members;
    }

    public Group(int id, String name, String photoUrl, String photoSmallUrl, List<UserBase> members) {
        mId = id;
        mName = name;
        mPhotoUrl = photoUrl;
        mPhotoSmallUrl = photoSmallUrl;
        mMembers = members;
    }

    public Group(int groupId) {
        mId = groupId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<UserBase> getMembers() {
        return mMembers;
    }

    @SuppressWarnings("unused")
    public void setMembers(List<UserBase> members) {
        mMembers = members;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public String getPhotoSmallUrl() {
        return mPhotoSmallUrl;
    }

    public void setPhotoSmallUrl(String photoSmallUrl) {
        mPhotoSmallUrl = photoSmallUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        return getId() == group.getId() && getName().equals(group.getName()) &&
                (getPhotoUrl() != null ? getPhotoUrl().equals(group.getPhotoUrl()) :
                        group.getPhotoUrl() == null && (getPhotoSmallUrl() != null ?
                                getPhotoSmallUrl().equals(group.getPhotoSmallUrl()) :
                                group.getPhotoSmallUrl() == null &&
                                        getMembers().equals(group.getMembers())));

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getPhotoUrl() != null ? getPhotoUrl().hashCode() : 0);
        result = 31 * result + (getPhotoSmallUrl() != null ? getPhotoSmallUrl().hashCode() : 0);
        result = 31 * result + getMembers().hashCode();
        return result;
    }
}
