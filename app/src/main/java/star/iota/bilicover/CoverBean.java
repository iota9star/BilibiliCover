package star.iota.bilicover;


import android.os.Parcel;
import android.os.Parcelable;

class CoverBean implements Parcelable {
    private String title;
    private String cover;
    private String info;
    private String category;
    private String date;
    private String upAvatar;
    private String upName;
    private String upInfo;

    CoverBean(String title, String cover, String info, String category, String date, String upAvatar, String upName, String upInfo) {
        this.title = title;
        this.cover = cover;
        this.info = info;
        this.category = category;
        this.date = date;
        this.upAvatar = upAvatar;
        this.upName = upName;
        this.upInfo = upInfo;
    }

    protected CoverBean(Parcel in) {
        title = in.readString();
        cover = in.readString();
        info = in.readString();
        category = in.readString();
        date = in.readString();
        upAvatar = in.readString();
        upName = in.readString();
        upInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(info);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(upAvatar);
        dest.writeString(upName);
        dest.writeString(upInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoverBean> CREATOR = new Creator<CoverBean>() {
        @Override
        public CoverBean createFromParcel(Parcel in) {
            return new CoverBean(in);
        }

        @Override
        public CoverBean[] newArray(int size) {
            return new CoverBean[size];
        }
    };

    String getTitle() {
        return title;
    }

    String getCover() {
        return cover;
    }

    String getInfo() {
        return info;
    }

    String getCategory() {
        return category;
    }

    String getDate() {
        return date;
    }

    String getUpAvatar() {
        return upAvatar;
    }

    String getUpName() {
        return upName;
    }

    String getUpInfo() {
        return upInfo;
    }
}
