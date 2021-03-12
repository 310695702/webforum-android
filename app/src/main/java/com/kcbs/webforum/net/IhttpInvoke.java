package com.kcbs.webforum.net;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface IhttpInvoke {
    void doget(String url,INetCallback callback);

    void dopost(String url, HashMap<Object,Object> params,INetCallback callback);

    void dopostMutipart(String url, File file, INetCallback callback);

    void dopostMutipartFiles(String url, List<File> files, INetCallback callback);

    void dopostMutipartAndObj(String url, HashMap<Object, Object> params, List<String> urls, INetCallback callback);

    void dopostMutipartAndFile(String url, HashMap<Object, Object> params, File file, INetCallback callback);

    void dopostJson(String url, String jsonstr, INetCallback callback);

    void doput(String url, HashMap<Object,Object> params, INetCallback callback);

    void dodelete(String url,HashMap<Object,Object> params,INetCallback callback);
}
