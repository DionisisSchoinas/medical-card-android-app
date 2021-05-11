package com.unipi.p17134.medicalcard.Custom;

public class LoadingDialogEvent {
    public boolean loading;

    public LoadingDialogEvent(boolean loading) {
        this.loading = loading;
    }

    public LoadingDialogEvent setLoading(boolean loading) {
        this.loading = loading;
        return this;
    }

    public boolean isLoading() {
        return loading;
    }
}
