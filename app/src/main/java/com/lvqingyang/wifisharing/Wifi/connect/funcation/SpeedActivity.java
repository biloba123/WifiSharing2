package com.lvqingyang.wifisharing.Wifi.connect.funcation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

import frame.SpeedManager;
import frame.listener.SpeedListener;
import frame.utils.ConverUtil;
import frame.views.PointerSpeedView;


public class SpeedActivity extends BaseActivity {

    private PointerSpeedView speedometer;
    private android.widget.TextView tvdownloadspeed;
    private android.widget.TextView tvuploadspeed;

    public static void start(Context context) {
        Intent starter = new Intent(context, SpeedActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_speed;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.speed_test, true);
        this.tvuploadspeed = (TextView) findViewById(R.id.tv_upload_speed);
        this.tvdownloadspeed = (TextView) findViewById(R.id.tv_download_speed);
        this.speedometer = (PointerSpeedView) findViewById(R.id.speedometer);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        new SpeedManager.Builder()
//                .setNetDelayListener(new NetDelayListener() {
//                    @Override
//                    public void result(String delay) {
//                        tx_delay.setText(delay);
//                    }
//                })
                .setSpeedListener(new SpeedListener() {
                    @Override
                    public void speeding(long downSpeed, long upSpeed) {
                        String[] downResult = ConverUtil.fomartSpeed(downSpeed);
                        tvdownloadspeed.setText(downResult[0] + downResult[1]);
                        setSpeedView(downSpeed, downResult);

                        String[] upResult = ConverUtil.fomartSpeed(upSpeed);
                        tvuploadspeed.setText(upResult[0] + upResult[1]);
                    }

                    @Override
                    public void finishSpeed(long finalDownSpeed, long finalUpSpeed) {
                        String[] downResult = ConverUtil.fomartSpeed(finalDownSpeed);
                        tvdownloadspeed.setText(downResult[0] + downResult[1]);
                        setSpeedView(finalDownSpeed, downResult);

                        String[] upResult = ConverUtil.fomartSpeed(finalUpSpeed);
                        tvuploadspeed.setText(upResult[0] + upResult[1]);

                        ResultActivity.start(SpeedActivity.this, ResultActivity.RESULT_SPEED, downResult[0] + downResult[1]);
                        finish();
                    }
                })
//                .setPindCmd("59.61.92.196")
//                .setSpeedUrl("https://dl.coolapk.com/down?pn=com.tencent.mm&id=MzQ1NA&h=59acabf6p00p6h&from=click")
//                .setSpeedCount(6)
//                .setSpeedTimeOut(10000)
                .builder()
                .startSpeed();
    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    private void setSpeedView(long speed, String[] result) {
        if (null != result && 2 == result.length) {
            speedometer.setCurrentSpeed(result[0]);
            speedometer.setUnit(result[1]);
            speedometer.speedPercentTo(ConverUtil.getSpeedPercent(speed));
        }
    }
}
