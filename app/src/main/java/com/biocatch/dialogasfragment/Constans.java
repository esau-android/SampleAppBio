package com.biocatch.dialogasfragment;

import android.os.Parcel;
import android.os.Parcelable;

public class Constans implements Parcelable, Cloneable {
    public static final String ACTION_COCREO = "com.biocatch.action.COCREA";

    protected Constans(Parcel in) {
    }

    public static final Creator<Constans> CREATOR = new Creator<Constans>() {
        @Override
        public Constans createFromParcel(Parcel in) {
            return new Constans(in);
        }

        @Override
        public Constans[] newArray(int size) {
            return new Constans[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
