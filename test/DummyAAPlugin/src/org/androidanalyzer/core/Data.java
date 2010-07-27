/**
 * 
 */
package org.androidanalyzer.core;

import java.util.ArrayList;

import org.androidanalyzer.Constants;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author k.raev
 */
public class Data implements Parcelable {

  private Bundle bundle;

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeBundle(bundle);
  }

  public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
    public Data createFromParcel(Parcel in) {
      return new Data(in);
    }

    public Data[] newArray(int size) {
      return new Data[size];
    }
  };

  private Data(Parcel in) {
    bundle = in.readBundle();
  }

  public Data() {
    bundle = new Bundle();
  }

  /* Getters and Setters */
  public void setName(String name) {
    if (name != null)
      bundle.putString(Constants.NODE_NAME, (String) name);
  }

  public String getName() {
    return (String) bundle.get(Constants.NODE_NAME);
  }

  public void setValue(Object value) {
    if (value != null)
      if (value instanceof String) {
        bundle.putString(Constants.NODE_VALUE, (String) value);
      } else if (value instanceof Data) {
        Data data = (Data) value;
        ArrayList<Parcelable> list = bundle.getParcelableArrayList(Constants.NODE_VALUE);
        if (list == null) {
          list = new ArrayList<Parcelable>();
        }
        list.add(data);
        bundle.putParcelableArrayList(Constants.NODE_VALUE, list);
      }
  }

  public Object getValue() {
    return bundle.get(Constants.NODE_VALUE);
  }

  public void setComment(String comment) {
    if (comment != null)
      bundle.putString(Constants.NODE_COMMENT, (String) comment);
  }

  public String getComment() {
    return (String) bundle.get(Constants.NODE_COMMENT);
  }

  public String getConfirmationLevel() {
    return (String) bundle.get(Constants.NODE_CONFIRMATION_LEVEL);
  }

  public void setConfirmationLevel(String confLevel) {
    if (confLevel != null)
      bundle.putString(Constants.NODE_CONFIRMATION_LEVEL, (String) confLevel);
  }

  public String getInputSource() {
    return (String) bundle.get(Constants.NODE_INPUT_SOURCE);
  }

  public void setInputSource(String inputSource) {
    if (inputSource != null)
      bundle.putString(Constants.NODE_INPUT_SOURCE, (String) inputSource);
  }

  public Object get(String key) {
    return bundle.get(key);
  }
}
