package com.android.tony.vpnattack.core.utils;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueHelper {
    /**
     * 外网返回后待转发到内网的队列
     */
    private static final LinkedBlockingQueue tcpOutToLocalQueue = new LinkedBlockingQueue();
    /**
     * 内网待转发到外网的队列
     */
    private static final LinkedBlockingQueue tcpInToNetQueue = new LinkedBlockingQueue();
}
