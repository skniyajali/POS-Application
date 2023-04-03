package ba;

import androidx.activity.C0270p;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import p093g0.InterfaceC3921i;
import p209nd.Lambda;

/* renamed from: ba.a */
/* loaded from: classes.dex */
public final class TextDivider extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

    /* renamed from: k */
    public final /* synthetic */ String f4953k;

    /* renamed from: l */
    public final /* synthetic */ int f4954l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public TextDivider(String str, int i) {
        super(2);
        this.f4953k = str;
        this.f4954l = i;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
        num.intValue();
        String str = this.f4953k;
        C1306b.m6940a(C0270p.m8128J0(this.f4954l | 1), interfaceC3921i, str);
        return Unit.INSTANCE;
    }
}