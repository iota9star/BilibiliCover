package star.iota.bilicover;


import android.os.Parcel;
import android.os.Parcelable;

class ResultBean implements Parcelable {
    private int code;
    private String msg;
    private CoverBean data;

    ResultBean(int code, String msg, CoverBean data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    protected ResultBean(Parcel in) {
        code = in.readInt();
        msg = in.readString();
        data = in.readParcelable(CoverBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(msg);
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
        @Override
        public ResultBean createFromParcel(Parcel in) {
            return new ResultBean(in);
        }

        @Override
        public ResultBean[] newArray(int size) {
            return new ResultBean[size];
        }
    };

    int getCode() {
        return code;
    }

    String getMsg() {
        return msg;
    }

    public CoverBean getData() {
        return data;
    }

    public void setData(CoverBean data) {
        this.data = data;
    }
}
