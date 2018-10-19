package com.android.tony.vpnattack;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.android.tony.vpnattack.core.AttackVpnService;

import cn.xuhao.android.lib.activity.BaseTitleBarActivityWithUIStuff;

import static com.android.tony.vpnattack.core.AttackVpnService.ACTION_DISCONNECT;

public class MainActivity extends BaseTitleBarActivityWithUIStuff {
    private static final int VPN_REQUEST_CODE = 0x0F;

    private Button mVpnButton;

    @Override
    protected void findViews() {
        mVpnButton = findViewById(R.id.vpn);

    }

    @Override
    protected void parseBundle(@Nullable Bundle bundle) {

    }

    @Override
    protected void initObjects() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mVpnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AttackVpnService.isRunning()) {
                    stopVPN();
                } else {
                    startVPN();
                }
            }
        });
    }

    private void stopVPN() {
        sendBroadcast(new Intent(ACTION_DISCONNECT));
        mVpnButton.setText("开启VPN中间人");
    }

    private void startVPN() {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    @Override
    protected boolean onActivityResult(int requestCode, int resultCode, Intent data, boolean isFragmentSponsor) {
        super.onActivityResult(requestCode, resultCode, data, isFragmentSponsor);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startService(new Intent(this, AttackVpnService.class));
            mVpnButton.setText("停止中间人");
        }
        return true;
    }

    @Override
    protected int getInnerLayoutResId() {
        return R.layout.activity_local_vpn;
    }
}
