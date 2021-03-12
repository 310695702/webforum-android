package com.kcbs.webforum.biz;

import android.os.Handler;
import android.os.Looper;

import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.SavaDataUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpClientbiz {
    private Socket socket;
    private InputStream in;
    private OutputStream os;
    private Handler mhandler = new Handler(Looper.getMainLooper());

    public void onDestory() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnMsgCommingListener {
        void onMsgComing(String msg);

        void onError(Exception ex);
    }

    private OnMsgCommingListener mListener;

    public void setOnMsgCommingListener(OnMsgCommingListener listener) {
        mListener = listener;
    }

    public TcpClientbiz() {
        new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket("47.111.9.152", 8089);
                    in = socket.getInputStream();
                    os = socket.getOutputStream();
                    readServerMsg();
                } catch (final IOException e) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onError(e);
                            }
                        }
                    });
                }
            }
        }.start();

    }

    private void readServerMsg() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
            final String finalLine = line;
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onMsgComing(finalLine);
                    }
                }
            });
        }
    }

    public void sendMsg(final String msg) {
        new Thread() {
            @Override
            public void run() {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(((User) SavaDataUtils.getData(WebApplication.getContext(), "User", 0).get(0)).getUsername() + ":" + msg);
                    bw.newLine();
                    bw.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

}
