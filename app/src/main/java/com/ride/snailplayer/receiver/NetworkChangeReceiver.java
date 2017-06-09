package com.ride.snailplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ride.snailplayer.framework.event.NetworkFlowEvent;
import com.ride.snailplayer.framework.event.NetworkNoEvent;
import com.ride.snailplayer.framework.event.NetworkWifiEvent;
import com.ride.util.common.util.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Stormouble
 * @since 2017/5/25.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtils.NetworkType type = NetworkUtils.getNetworkStatus();
        if (type == NetworkUtils.NetworkType.NETWORK_NO) {
            EventBus.getDefault().post(new NetworkNoEvent());
        } else if (type == NetworkUtils.NetworkType.NETWORK_WIFI){
            EventBus.getDefault().post(new NetworkWifiEvent());
        } else {
            EventBus.getDefault().post(new NetworkFlowEvent());
        }
    }
}
