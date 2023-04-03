package ac;

import java.time.LocalDate;
import kotlin.Unit;
import kotlin.jvm.functions.Functions;
import p209nd.C5917h;
import p209nd.Lambda;

/* compiled from: DatePicker.kt */
/* renamed from: ac.d */
/* loaded from: classes.dex */
public final class C0199d extends Lambda implements Functions<Unit> {

    /* renamed from: k */
    public final /* synthetic */ DatePickerState f668k;

    /* renamed from: l */
    public final /* synthetic */ LocalDate f669l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0199d(DatePickerState datePickerState, LocalDate localDate) {
        super(0);
        this.f668k = datePickerState;
        this.f669l = localDate;
    }

    @Override // kotlin.jvm.functions.Functions
    public final Unit invoke() {
        DatePickerState datePickerState = this.f668k;
        LocalDate localDate = this.f669l;
        C5917h.m3119e(localDate, "date");
        datePickerState.getClass();
        datePickerState.f666d.setValue(localDate);
        return Unit.INSTANCE;
    }
}