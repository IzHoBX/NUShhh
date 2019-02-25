package izho.com.nushhh;

public class Singletons {
    static PopupManager popupManager;
    static NusmodsManager nusmodsManager;
    static RemoteConfigManager remoteConfigManager;

    public static PopupManager getPopupManager() {
        if(popupManager == null) {
            popupManager = new PopupManager();
        }
        return popupManager;
    }

    public static NusmodsManager getNusmodsManager() {
        if(nusmodsManager == null) {
            nusmodsManager = new NusmodsManager();
        }
        return nusmodsManager;
    }

    public static RemoteConfigManager getRemoteConfigManager() {
        if(remoteConfigManager == null) {
            remoteConfigManager = new RemoteConfigManager();
        }
        return remoteConfigManager;
    }


}
