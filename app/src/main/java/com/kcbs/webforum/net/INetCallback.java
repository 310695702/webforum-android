package com.kcbs.webforum.net;

import java.lang.reflect.Type;

public interface INetCallback<T> {
    void onSuccess(T data);
    void onFailed(Throwable ex);
    Type getType();
}
