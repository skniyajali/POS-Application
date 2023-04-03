package ac;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Functions;
import p093g0.InterfaceC3921i;
import p209nd.Lambda;

/* compiled from: DatePicker.kt */
/* renamed from: ac.a0 */
/* loaded from: classes.dex */
public final class C0186a0 extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

    /* renamed from: k */
    public final /* synthetic */ int f622k;

    /* renamed from: l */
    public final /* synthetic */ boolean f623l;

    /* renamed from: m */
    public final /* synthetic */ DatePickerColors f624m;

    /* renamed from: n */
    public final /* synthetic */ Functions<Unit> f625n;

    /* renamed from: o */
    public final /* synthetic */ int f626o;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0186a0(int i, boolean z, DatePickerColors datePickerColors, Functions<Unit> functions, int i2) {
        super(2);
        this.f622k = i;
        this.f623l = z;
        this.f624m = datePickerColors;
        this.f625n = functions;
        this.f626o = i2;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
        num.intValue();
        C0188c.m8267h(this.f622k, this.f623l, this.f624m, this.f625n, interfaceC3921i, this.f626o | 1);
        return Unit.INSTANCE;
    }
}