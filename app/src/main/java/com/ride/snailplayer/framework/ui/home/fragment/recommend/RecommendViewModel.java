package com.ride.snailplayer.framework.ui.home.fragment.recommend;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;

import com.ride.snailplayer.net.model.RecommendItem;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/15
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class RecommendViewModel extends AndroidViewModel {

    private MutableLiveData<List<RecommendItem>> data = new MutableLiveData<>();
    public RecommendViewModel(Application application) {
        super(application);
    }

}
