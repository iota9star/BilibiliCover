package star.iota.bilicover;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends Activity {
    @BindView(R.id.ken_burns_view_cover)
    KenBurnsView mKenBurnsView;
    @BindView(R.id.circle_image_view_avatar)
    CircleImageView mCircleImageViewAvatar;
    @BindView(R.id.edit_text_search_bar)
    EditText mEditTextSearchBar;
    @BindView(R.id.text_view_category)
    TextView mTextViewCategory;
    @BindView(R.id.text_view_info)
    TextView mTextViewInfo;
    @BindView(R.id.scroll_view_container)
    ScrollView mScrollViewContainer;
    @BindView(R.id.text_view_title)
    TextView mTextViewTitle;
    @BindView(R.id.text_view_up_info)
    TextView mTextViewUpInfo;
    @BindView(R.id.text_view_up_name)
    TextView mTextViewUpName;
    @BindView(R.id.text_view_date)
    TextView mTextViewDate;
    @BindView(R.id.image_view_bilibili)
    ImageView mImageViewBilibili;
    @BindView(R.id.text_view_source)
    TextView mTextViewSource;
    @BindView(R.id.text_view_banner)
    TextView mTextViewBanner;
    private Context mContext;
    private CoverBean mData;

    @OnClick({R.id.button_copy, R.id.button_download, R.id.button_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_copy:
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("image_url", mData.getCover()));
                break;
            case R.id.button_download:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mData.getCover()));
                startActivity(intent);
                break;
            case R.id.button_search:
                startSearch(mEditTextSearchBar.getText().toString());
                break;
        }
    }

    private static final String SCHEMA = "https:";
    private static final String AV = "https://www.bilibili.com/video/av";

    private boolean isRunning;

    private MyHandler mHandler = new MyHandler(this);
    private static final String[] bilibili = {
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/mH8GNg8y40e8zNnE1AOJbAQnyxODZNxZHR1ZCC1dN1I!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/p1hwdSx0fylJjD1uZXeQ*b1qxQ7mG16OQ8.w.9onaRU!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/k*.Z.UlfnhD6cy16LqKnkk1INJMm*au9ZjBUr6PsF7U!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/KRYC8RM164VTKmvGnjeVxiwkCitsrNNbZOicZTMdQm0!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/pLElTAo8Ixly1ZbWBInrIPM3ZkRgHvVmu9ORUPAi0NM!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/tvaUzkrt0KYCCQCBT*i2OSbhUqRVTm*ZrnGIZajnqqM!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/2hGPZXrKtnR79v1icdAZIKIUiDGvqK*vT6zDsJsjfjI!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/Q5LiFVfC7sDv5v7TSY7IHX1UiqoQ.BX64YJflxL6BME!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/Plctkw9btc8MCPSlATu55VDXWW5JHSQLWwk4xqUImU8!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/TFBrsjhDwo3ZpgteysudbkvMR2p54T3kUvIWzmDpm3Y!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/xfKpIQeYNNIRINXwTK96oel23EuObd*ukI5zhmCRGsg!/r/dG4BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/KqfZ*3*0dtrsf8pbOtKIVQWbXKGztOS9xKgLLNS9zrY!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/aEfER8hyouOiLabP6pq34BzAfKDUo.3faQ6wxOe3*dE!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/XZhPW8yk3lbLkqq7yTfjqWkdE2nkgNl1l*.C8AMraIw!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/ftI57wTd45iQyOUF8tLtsRrVh7L*emdit8t6xl5z5xk!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/nwU4wPcwWi*2ESJFkEZU.ifRBSeiBk8dvYRl1dlcBv8!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/MT61FZrvtFZ.7QBI8d8zE6Q6jLRrMv5Rqn5Fka1DAo4!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/rmmZNv3fO3kLdyTLBx2KU7aWRu2h2yyN6NxLskEq6OQ!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/eGVN13Xgh9o60SFP.HVHmyIrlRFqdi5B0mYAdCIFO1Q!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/I.T3vuNDdRAxOdO3kSh6QKSXY4XPbP8Bp4DaeOgl2OY!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/URMtxZkti2L2x7pecQdGFrXMGlBu.b6buaCnlK6NRK8!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/d1YwNA1NQx71WpLiVp.ZABxiHrskuYOwlzRhgXCHqIQ!/r/dOYAAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/CD5fxHAhv1hODZ4Fgj5aUQvwPAlLT1OzXgMzAlpgifs!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/C1qcnD8Lr48z7wgE39Trs9gLdhN3CtMZxM7FNx49YaA!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/MsFall2N6lfmX9TMADonUsah*yVIGkcXwcmbt8rNHkU!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/gwhfdYckGz1g4qZv1QVg6LkZodfWPAqHyFkw7dBjp*k!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/3b0rix8aAuQkiJxVYlmpUjJxUhCpifaA5.8V3PiN7ec!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/ZMgyJL.onKT5zcoXLrPDzbObgxc2FWs4KjnxyGXw8jg!/r/dG0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/AYIHkwdR4d5hDOsAPLbzY2n.Ed*SW2*wKqjI4uriJz8!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/TR41r0PjcBtoo8vQEzd6.zeSsjUP*7cYim9f9l4mEKQ!/r/dGwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/n9.3rjKqsr7hcjbF4qZ*2r4j9pUvhIFosA7YEU3nqI0!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/k8xlioeMDepTBvB*2vu9pixLVoNN0WWhri2nnhklA50!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/6VPwAJ9lM*dQ3ndJPFE6yV19CG8RsNSU0O11WsuROZs!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/ybX1c7rRi8Tjio3F3Nk*jtjedk*Z3yMYqyjKLd1Q0Pw!/r/dAUBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/IGRqGv0LafRzZ1wACbH5HR2Z4WfZKQF2GJWx6tRWZso!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/cKwHspfCt02GbEED4Qitjc14gdU72jQ98OM8vTzTdmk!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/lyJ9*0iDRDQh5xd24vkVEAkrUiQgsHH0eIRZPZTTYd4!/r/dNsAAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/uFu3vAcx7x4vnN49IDB*l6sVAPleDAKLiDp5kecpExY!/r/dGwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/C2he26QIeiMUIYVY8IJIKpqltYT99KDXb3gW0kwkCjA!/r/dGwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/0F.CeHWTvJ87r6dI3OIlULpnUsIgyOulDvsd3iW2*Jk!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/Pbvn.KyOJeIhia4thFusIMu7U.S3CLwptI7Lt1pWJMA!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/J1vwlKXXUK*cJ*NzjaV2ObBgfD5aWIoS8s5pP*WLC3c!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/IDFBYaTaPz3YOWQdpQJ38zQLNo3x4asd*r7vR0dVkr0!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/iMlyQvI88qxTmzR*xP28tRhaEM8mvMdOUBNC7vdzYfo!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/gXSReemzlOs38LfdNKQdCdEvtkqcVYl7.TZgBW2Jq6A!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/NpUsuoIKAfafmAsIzzvvK4jNIEvrOE9f9pPXLl1o7hw!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/lyEg1XuhS4Wb80j85BX1e7DrQKsN4oku92gMRIYOslo!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/AJVNnl8*rJyhKG8vHraS3euDnTvUj3PMySgURFJmkeg!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/59ozgyyXwX1SZardRCCMA0hTZkU4C22HmRWKcwFXfnY!/r/dNsAAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/.yEkdonaTSmDSLmn8XMvhEbsPknXT6BkTXh4h3ur5oY!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/BoNwJ2rU0X1ABrZhcc2ranp8455Pod5wm5txfpCnk*k!/r/dOYAAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/ALFxvC.AwvWMdRZMYgivvidlIbt845PHGBoIYREVS10!/r/dD0BAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/KnCqzfha0wVIf40FylthxPPuXwwLbnBO4v3dLw*Ufs8!/r/dDwBAAAAAAAA",
            "http://r.photo.store.qq.com/psb?/V12tx9ch4cYaRh/AJTdo0ZUgXATxasc9txKHOhZlszrWlZcRrrGphyODL0!/r/dDwBAAAAAAAA"
    };
    private String mSourceUrl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        isRunning = false;
        loader(mImageViewBilibili, bilibili[new Random().nextInt(bilibili.length)]);
    }

    private void startSearch(String keywords) {
        int type = checkKeyWords(keywords);
        switch (type) {
            case SearchType.AV:
                String url = AV + keywords;
                runTask(url);
                break;
            case SearchType.Url:
                runTask(keywords);
                break;
            default:
                showInfo("您输入的AV号或是地址不正确");
        }
    }

    private void runTask(String url) {
        mSourceUrl = url;
        mScrollViewContainer.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                searching(mSourceUrl);
            }
        }).start();
    }

    private int checkKeyWords(String keywords) {
        Pattern av = Pattern.compile("^[0-9]+$");
        Matcher mav = av.matcher(keywords);
        if (mav.find()) {
            return SearchType.AV;
        }
        Pattern url = Pattern.compile("^[http|https].+/av[0-9]+.?");
        Matcher murl = url.matcher(keywords);
        if (murl.find()) {
            return SearchType.Url;
        }
        return -1;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mReference;

        MyHandler(MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mReference.get();
            if (activity != null) {
                ResultBean result = msg.getData().getParcelable("result");
                switch (result.getCode()) {
                    case MsgType.SUCCESS:
                        activity.mData = result.getData();
                        activity.bindView(activity.mData);
                        activity.showInfo(result.getMsg());
                        activity.mScrollViewContainer.setVisibility(View.VISIBLE);
                        activity.mImageViewBilibili.setVisibility(View.GONE);
                        break;
                    case MsgType.ERROR:
                        activity.mImageViewBilibili.setVisibility(View.VISIBLE);
                        activity.loader(activity.mImageViewBilibili, bilibili[new Random().nextInt(bilibili.length)]);
                        activity.showInfo(result.getMsg());
                        break;
                    case MsgType.LOADING:
                        activity.showInfo(result.getMsg());
                        break;
                }
            }
        }
    }

    private void searching(String url) {
        if (isRunning) {
            ResultBean result = new ResultBean(MsgType.LOADING, "正在加载中，别着急哦", null);
            sendMsg(result);
            return;
        }
        isRunning = true;
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3228.1 Safari/537.36")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(60000)
                    .get();
            String title = document.select("#viewbox_report > div.info > div.v-title > h1").attr("title");
            String cover = SCHEMA + document.select("img.cover_image").attr("src");
            document.select("#v_desc img").remove();
            String info = document.select("#v_desc").html().replaceAll("\n", "\n");
            System.out.println(info);
            String category = document.select("#viewbox_report > div.info > div.tminfo > span:nth-child(3) > a").text();
            String date = document.select("#viewbox_report > div.info > div.tminfo > time > i").text();
            String upAvatar = document.select("#r-info-rank > a:nth-child(1) > img").attr("data-fn-src");
            String upName = document.select("#viewbox_report > div.upinfo > div.r-info > div.usname > a.name").attr("title");
            document.select("#viewbox_report > div.upinfo > div.r-info > div.sign.static > a").remove();
            String upInfo = document.select("#viewbox_report > div.upinfo > div.r-info > div.sign").text().replaceAll("&nbsp;", "");
            CoverBean bean = new CoverBean(title, cover, info, category, date, upAvatar, upName, upInfo);
            if (cover.length() > SCHEMA.length() + 10) {
                ResultBean result = new ResultBean(MsgType.SUCCESS, "加载成功", bean);
                sendMsg(result);
            } else {
                ResultBean result = new ResultBean(MsgType.ERROR, "没有获取到数据，请检查您的输入是否正确", null);
                sendMsg(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ResultBean result = new ResultBean(MsgType.ERROR, "加载错误：" + e.getMessage(), null);
            sendMsg(result);
        }
        isRunning = false;
    }

    private void sendMsg(ResultBean result) {
        Message message = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable("result", result);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void bindView(CoverBean bean) {
        loader(mKenBurnsView, bean.getCover());
        loader(mCircleImageViewAvatar, bean.getUpAvatar());
        mTextViewCategory.setText(bean.getCategory());
        mTextViewInfo.setText(Html.fromHtml(bean.getInfo()));
        if (mTextViewInfo.getLineCount() > 1) {
            mTextViewInfo.setText(String.format("\u3000\u3000%s", Html.fromHtml(bean.getInfo())));
        }
        mTextViewInfo.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewTitle.setText(bean.getTitle());
        mTextViewUpInfo.setText(bean.getUpInfo());
        if (mTextViewUpInfo.getLineCount() > 1) {
            mTextViewInfo.setText(String.format("\u3000\u3000%s", bean.getUpInfo()));
        } else {
            mTextViewInfo.setGravity(Gravity.CENTER);
        }
        mTextViewUpName.setText(bean.getUpName());
        mTextViewDate.setText(bean.getDate());
        mTextViewSource.setText(String.format("来源：%s", mSourceUrl));
        mTextViewBanner.setText(String.format("封面：%s", bean.getCover()));
    }

    private void loader(ImageView view, String url) {
        if (url == null) return;
        Picasso.with(mContext)
                .load(url)
                .into(view);
    }

    private void showInfo(String info) {
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
    }
}
