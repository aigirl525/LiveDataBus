package com.example.livedatabus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class LiveDataBus {
    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DATA_BUS = new LiveDataBus();
    }

    public static LiveDataBus getInstance() {
        return SingletonHolder.DATA_BUS;
    }

    public MutableLiveData<Object> getChannel(String target) {
        return getChannel(target, Object.class);
    }

    public <T> MutableLiveData<T> getChannel(String target, Class<T> type) {

        if (!bus.containsKey(target)) {
            bus.put(target, new BusMutableLiveData<Object>());
        }
        return (MutableLiveData<T>) bus.get(target);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T o) {

            if (observer != null) {
                if (isCallOnObserve()) {
                    return;
                }
                observer.onChanged(o);
            }

        }

        private boolean isCallOnObserve() {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                for (StackTraceElement element : stackTraceElements) {
                    if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                            "observeForever".equals(element.getMethodName())) {
                        return true;
                    }
                }
            }
            return false;
        }

    }
    private static class BusMutableLiveData<T> extends MutableLiveData<T> {
        private Map<Observer, Observer> observerObserverMap = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            try {
                hook(observer);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public void observeForever(@NonNull Observer<? super T> observer) {
            if (!observerObserverMap.containsKey(observer)) {
                observerObserverMap.put(observer, new ObserverWrapper(observer));
            }
            super.observeForever(observerObserverMap.get(observer));
        }

        @Override
        public void removeObserver(@NonNull Observer<? super T> observer) {
            Observer realObserver = null;
            if (observerObserverMap.containsKey(observer)){
                realObserver = observerObserverMap.get(observer);
            }else {
                realObserver = observer;
            }
            super.removeObserver(realObserver);
        }

        private void hook(@NonNull Observer<? super T> observer) throws Exception {
            //get wrapper's version
            Class<LiveData> classLiveData = LiveData.class;
            Field fieldObservers = classLiveData.getDeclaredField("mObservers");
            fieldObservers.setAccessible(true);
            Object objectObservers = fieldObservers.get(this);
            Class<?> classObservers = objectObservers.getClass();
            Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
            methodGet.setAccessible(true);
            Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
            Object objectWrapper = null;
            if (objectWrapperEntry instanceof Map.Entry) {
                objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
            }
            if (objectWrapper == null) {
                throw new NullPointerException("Wrapper can not be bull!");
            }
            Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
            Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
            fieldLastVersion.setAccessible(true);
            //get livedata's version
            Field fieldVersion = classLiveData.getDeclaredField("mVersion");
            fieldVersion.setAccessible(true);
            Object objectVersion = fieldVersion.get(this);
            //set wrapper's version
            fieldLastVersion.set(objectWrapper, objectVersion);
        }
    }
}
