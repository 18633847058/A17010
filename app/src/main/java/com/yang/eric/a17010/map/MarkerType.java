package com.yang.eric.a17010.map;

/**
 * Created by Yang on 2017/4/26.
 * 键值,用来区分不同的marker
 * tag,groupId相同 视为同一个marker
 */

public class MarkerType {

    private int tag;
    private int groupId;

    public MarkerType(int tag, int groupId) {
        this.tag = tag;
        this.groupId = groupId;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarkerType that = (MarkerType) o;

        if (tag != that.tag) return false;
        return groupId == that.groupId;

    }

    @Override
    public int hashCode() {
        int result = tag;
        result = 31 * result + groupId;
        return result;
    }
}
