package me.dkzwm.smoothrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import me.dkzwm.smoothrefreshlayout.extra.header.LottieHeader;

/**
 * @author Stormouble
 * @since 2017/6/12.
 */

public class LottieSmoothRefreshLayout extends SmoothRefreshLayout {

    private LottieHeader mLottelHeader;

    public LottieSmoothRefreshLayout(Context context) {
        this(context, null);
    }

    public LottieSmoothRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LottieSmoothRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mMode = MODE_REFRESH;
        mLottelHeader = (LottieHeader) LayoutInflater.from(
                getContext()).inflate(R.layout.lottie_header, this, false);
        setHeaderView(mLottelHeader);
        setEnableKeepRefreshView(true);
    }
}
