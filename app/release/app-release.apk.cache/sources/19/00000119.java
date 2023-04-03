package ad;

import androidx.activity.C0264f;
import p001a0.R8$$SyntheticClass;
import p209nd.C5917h;

/* compiled from: RealmPropertyType.kt */
/* renamed from: ad.b */
/* loaded from: classes.dex */
public final class C0222b implements InterfaceC0223d {

    /* renamed from: a */
    public final RealmStorageType f750a;

    /* renamed from: b */
    public final boolean f751b;

    public C0222b(RealmStorageType realmStorageType, boolean z) {
        C5917h.m3118f(realmStorageType, "storageType");
        this.f750a = realmStorageType;
        this.f751b = z;
    }

    @Override // ad.InterfaceC0223d
    /* renamed from: b */
    public final boolean mo8259b() {
        return this.f751b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0222b)) {
            return false;
        }
        C0222b c0222b = (C0222b) obj;
        if (this.f750a == c0222b.f750a && this.f751b == c0222b.f751b) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int hashCode() {
        int hashCode = this.f750a.hashCode() * 31;
        boolean z = this.f751b;
        int i = z;
        if (z != 0) {
            i = 1;
        }
        return hashCode + i;
    }

    public final String toString() {
        StringBuilder m8428l = R8$$SyntheticClass.m8428l("MapPropertyType(storageType=");
        m8428l.append(this.f750a);
        m8428l.append(", isNullable=");
        return C0264f.m8169f(m8428l, this.f751b, ')');
    }
}