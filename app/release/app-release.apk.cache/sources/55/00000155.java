package android.app;

import android.annotation.Nullable;
import android.graphics.drawable.Icon;
import android.os.Parcelable;

/* loaded from: classes.dex */
public final /* synthetic */ class Person implements Parcelable {
    static {
        throw new NoClassDefFoundError();
    }

    @Nullable
    public native /* synthetic */ Icon getIcon();

    @Nullable
    public native /* synthetic */ String getKey();

    @Nullable
    public native /* synthetic */ CharSequence getName();

    @Nullable
    public native /* synthetic */ String getUri();

    public native /* synthetic */ boolean isBot();

    public native /* synthetic */ boolean isImportant();
}