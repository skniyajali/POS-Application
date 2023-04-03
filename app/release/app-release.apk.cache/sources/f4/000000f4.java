package ac;

import androidx.activity.C0264f;
import androidx.activity.C0269o;
import androidx.activity.C0270p;
import androidx.activity.C0271q;
import androidx.compose.p015ui.platform.CompositionLocals;
import androidx.compose.p015ui.platform.TestTag;
import androidx.compose.p015ui.platform.ViewConfiguration;
import androidx.emoji2.text.C0565j;
import androidx.fragment.app.C0582d0;
import androidx.navigation.compose.C0691q;
import ch.Installation;
import ch.ToastSender;
import com.niyaj.popos.C1518R;
import eg.CoroutineScope;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Functions;
import p001a0.R8$$SyntheticClass;
import p002a1.Painter;
import p017b0.C1018r1;
import p017b0.Colors;
import p017b0.Icon;
import p017b0.Text;
import p018b1.C1233n;
import p018b1.ImageVector;
import p018b1.PathBuilder;
import p029c0.KeyboardArrowLeft;
import p029c0.KeyboardArrowRight;
import p054dd.Tuples;
import p071ed.AbstractC3650g0;
import p071ed.C3662x;
import p071ed.IteratorsJVM;
import p076f1.C3780c;
import p093g0.C3883b2;
import p093g0.C3928j;
import p093g0.C3963n0;
import p093g0.C3996u0;
import p093g0.C4004w0;
import p093g0.CompositionLocal;
import p093g0.InterfaceC3895d;
import p093g0.InterfaceC3921i;
import p093g0.InterfaceC3992t0;
import p095g2.Density;
import p095g2.IntOffset;
import p095g2.LayoutDirection;
import p157k1.C4834r;
import p157k1.MeasurePolicy;
import p187m1.C5406z;
import p187m1.ComposeUiNode;
import p198n.C5590b1;
import p198n.C5608f0;
import p198n.C5644o1;
import p198n.C5661t1;
import p198n.C5662u;
import p198n.C5680x0;
import p198n.C5684z0;
import p199n0.C5685a;
import p199n0.ComposableLambda;
import p209nd.C5917h;
import p209nd.Lambda;
import p210o.VisibilityThresholds;
import p228p.C6271u;
import p230p1.C6327b;
import p259r.C6751m;
import p276s.Arrangement;
import p276s.Box;
import p276s.C6862b;
import p276s.C6890h;
import p276s.C6919o1;
import p276s.C6961z0;
import p276s.Column;
import p276s.Row;
import p277s0.Alignment;
import p277s0.C6966b;
import p277s0.InterfaceC6973f;
import p277s0.ZIndexModifier;
import p302u.C7470h;
import p302u.C7507z0;
import p302u.InterfaceC7455b;
import p302u.InterfaceC7488r0;
import p349x0.C8108t0;
import p349x0.C8110v;
import p349x0.RectangleShape;
import p350x1.FontWeight;
import p353x4.C8172b;
import p353x4.InterfaceC8191g;
import p353x4.PagerState;
import p366y.C8390h;
import p394zb.InterfaceC8841m;
import td.C7328i;
import td.C7330k;
import td.C7331l;

/* compiled from: DatePicker.kt */
/* renamed from: ac.c */
/* loaded from: classes.dex */
public final class C0188c {

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$a */
    /* loaded from: classes.dex */
    public static final class C0189a extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

        /* renamed from: k */
        public final /* synthetic */ String f627k;

        /* renamed from: l */
        public final /* synthetic */ DatePickerState f628l;

        /* renamed from: m */
        public final /* synthetic */ Locale f629m;

        /* renamed from: n */
        public final /* synthetic */ int f630n;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C0189a(String str, DatePickerState datePickerState, Locale locale, int i) {
            super(2);
            this.f627k = str;
            this.f628l = datePickerState;
            this.f629m = locale;
            this.f630n = i;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
            num.intValue();
            C0188c.m8274a(this.f627k, this.f628l, this.f629m, interfaceC3921i, this.f630n | 1);
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$b */
    /* loaded from: classes.dex */
    public static final class C0190b extends Lambda implements Function4<InterfaceC8191g, Integer, InterfaceC3921i, Integer, Unit> {

        /* renamed from: k */
        public final /* synthetic */ DatePickerState f631k;

        /* renamed from: l */
        public final /* synthetic */ PagerState f632l;

        /* renamed from: m */
        public final /* synthetic */ Locale f633m;

        /* renamed from: n */
        public final /* synthetic */ Function1<LocalDate, Boolean> f634n;

        /* renamed from: o */
        public final /* synthetic */ int f635o;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public C0190b(DatePickerState datePickerState, PagerState pagerState, Locale locale, Function1<? super LocalDate, Boolean> function1, int i) {
            super(4);
            this.f631k = datePickerState;
            this.f632l = pagerState;
            this.f633m = locale;
            this.f634n = function1;
            this.f635o = i;
        }

        @Override // kotlin.jvm.functions.Function4
        public final Unit invoke(InterfaceC8191g interfaceC8191g, Integer num, InterfaceC3921i interfaceC3921i, Integer num2) {
            int i;
            int intValue = num.intValue();
            InterfaceC3921i interfaceC3921i2 = interfaceC3921i;
            int intValue2 = num2.intValue();
            C5917h.m3118f(interfaceC8191g, "$this$HorizontalPager");
            if ((intValue2 & 112) == 0) {
                if (interfaceC3921i2.mo5176i(intValue)) {
                    i = 32;
                } else {
                    i = 16;
                }
                intValue2 |= i;
            }
            if ((intValue2 & 721) == 144 && interfaceC3921i2.mo5156s()) {
                interfaceC3921i2.mo5144y();
            } else {
                DatePickerState datePickerState = this.f631k;
                interfaceC3921i2.mo5184e(-492369756);
                Object mo5182f = interfaceC3921i2.mo5182f();
                if (mo5182f == InterfaceC3921i.C3922a.f12318a) {
                    mo5182f = LocalDate.of((intValue / 12) + datePickerState.f664b.f22668j, (intValue % 12) + 1, 1);
                    interfaceC3921i2.mo5228B(mo5182f);
                }
                interfaceC3921i2.mo5220F();
                LocalDate localDate = (LocalDate) mo5182f;
                DatePickerState datePickerState2 = this.f631k;
                PagerState pagerState = this.f632l;
                Locale locale = this.f633m;
                Function1<LocalDate, Boolean> function1 = this.f634n;
                int i2 = this.f635o;
                interfaceC3921i2.mo5184e(-483455358);
                InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
                MeasurePolicy m2256a = Column.m2256a(Arrangement.f21354c, Alignment.C6963a.f21545l, interfaceC3921i2);
                interfaceC3921i2.mo5184e(-1323940314);
                CompositionLocal compositionLocal = CompositionLocals.f1901e;
                Density density = (Density) interfaceC3921i2.mo5154t(compositionLocal);
                CompositionLocal compositionLocal2 = CompositionLocals.f1907k;
                LayoutDirection layoutDirection = (LayoutDirection) interfaceC3921i2.mo5154t(compositionLocal2);
                CompositionLocal compositionLocal3 = CompositionLocals.f1911o;
                ViewConfiguration viewConfiguration = (ViewConfiguration) interfaceC3921i2.mo5154t(compositionLocal3);
                ComposeUiNode.f16753e.getClass();
                C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
                C5685a m4068a = C4834r.m4068a(c6974a);
                if (interfaceC3921i2.mo5150v() instanceof InterfaceC3895d) {
                    interfaceC3921i2.mo5158r();
                    if (interfaceC3921i2.mo5168m()) {
                        interfaceC3921i2.mo5148w(c5407a);
                    } else {
                        interfaceC3921i2.mo5230A();
                    }
                    interfaceC3921i2.mo5152u();
                    ComposeUiNode.C5354a.C5357c c5357c = ComposeUiNode.C5354a.f16758e;
                    Installation.m6566d0(interfaceC3921i2, m2256a, c5357c);
                    ComposeUiNode.C5354a.C5355a c5355a = ComposeUiNode.C5354a.f16757d;
                    Installation.m6566d0(interfaceC3921i2, density, c5355a);
                    ComposeUiNode.C5354a.C5356b c5356b = ComposeUiNode.C5354a.f16759f;
                    Installation.m6566d0(interfaceC3921i2, layoutDirection, c5356b);
                    ComposeUiNode.C5354a.C5359e c5359e = ComposeUiNode.C5354a.f16760g;
                    C0582d0.m7540g(0, m4068a, C0271q.m8049i(interfaceC3921i2, viewConfiguration, c5359e, interfaceC3921i2), interfaceC3921i2, 2058660585, -1163856341);
                    C5917h.m3119e(localDate, "viewDate");
                    C0188c.m8270e(localDate, datePickerState2, pagerState, locale, interfaceC3921i2, 4168);
                    interfaceC3921i2.mo5184e(733328855);
                    MeasurePolicy m2281c = C6890h.m2281c(Alignment.C6963a.f21534a, false, interfaceC3921i2);
                    interfaceC3921i2.mo5184e(-1323940314);
                    Density density2 = (Density) interfaceC3921i2.mo5154t(compositionLocal);
                    LayoutDirection layoutDirection2 = (LayoutDirection) interfaceC3921i2.mo5154t(compositionLocal2);
                    ViewConfiguration viewConfiguration2 = (ViewConfiguration) interfaceC3921i2.mo5154t(compositionLocal3);
                    C5685a m4068a2 = C4834r.m4068a(c6974a);
                    if (interfaceC3921i2.mo5150v() instanceof InterfaceC3895d) {
                        interfaceC3921i2.mo5158r();
                        if (interfaceC3921i2.mo5168m()) {
                            interfaceC3921i2.mo5148w(c5407a);
                        } else {
                            interfaceC3921i2.mo5230A();
                        }
                        C0582d0.m7540g(0, m4068a2, R8$$SyntheticClass.m8432h(interfaceC3921i2, interfaceC3921i2, m2281c, c5357c, interfaceC3921i2, density2, c5355a, interfaceC3921i2, layoutDirection2, c5356b, interfaceC3921i2, viewConfiguration2, c5359e, interfaceC3921i2), interfaceC3921i2, 2058660585, -2137368960);
                        boolean booleanValue = ((Boolean) datePickerState2.f667e.getValue()).booleanValue();
                        InterfaceC6973f m5443D = C3780c.m5443D(new ZIndexModifier(0.7f));
                        C5684z0 m3394g = C5608f0.m3394g(C0210o.f708k);
                        C0211p c0211p = C0211p.f709k;
                        int i3 = IntOffset.f12659c;
                        C5662u.m3383e(booleanValue, m5443D, m3394g, new C5590b1(new C5661t1(null, new C5644o1(C0270p.m8144B0(0.0f, 400.0f, new IntOffset(VisibilityThresholds.m2976a()), 1), new C5680x0(c0211p)), null, null, 13)), null, ComposableLambda.m3358b(interfaceC3921i2, 943286276, new C0212q(localDate, datePickerState2, pagerState)), interfaceC3921i2, 200112, 16);
                        C0188c.m8271d(localDate, datePickerState2, locale, function1, interfaceC3921i2, ((i2 << 3) & 7168) | 584);
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5218G();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5218G();
                        interfaceC3921i2.mo5220F();
                        interfaceC3921i2.mo5220F();
                    } else {
                        Installation.m6582Q();
                        throw null;
                    }
                } else {
                    Installation.m6582Q();
                    throw null;
                }
            }
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$c */
    /* loaded from: classes.dex */
    public static final class C0191c extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

        /* renamed from: k */
        public final /* synthetic */ String f636k;

        /* renamed from: l */
        public final /* synthetic */ DatePickerState f637l;

        /* renamed from: m */
        public final /* synthetic */ Function1<LocalDate, Boolean> f638m;

        /* renamed from: n */
        public final /* synthetic */ Locale f639n;

        /* renamed from: o */
        public final /* synthetic */ int f640o;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public C0191c(String str, DatePickerState datePickerState, Function1<? super LocalDate, Boolean> function1, Locale locale, int i) {
            super(2);
            this.f636k = str;
            this.f637l = datePickerState;
            this.f638m = function1;
            this.f639n = locale;
            this.f640o = i;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
            num.intValue();
            C0188c.m8273b(this.f636k, this.f637l, this.f638m, this.f639n, interfaceC3921i, this.f640o | 1);
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$d */
    /* loaded from: classes.dex */
    public static final class C0192d extends Lambda implements Function1<InterfaceC7488r0, Unit> {

        /* renamed from: k */
        public final /* synthetic */ List<String> f641k;

        /* renamed from: l */
        public final /* synthetic */ DatePickerState f642l;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C0192d(ArrayList arrayList, DatePickerState datePickerState) {
            super(1);
            this.f641k = arrayList;
            this.f642l = datePickerState;
        }

        @Override // kotlin.jvm.functions.Function1
        public final Unit invoke(InterfaceC7488r0 interfaceC7488r0) {
            InterfaceC7488r0 interfaceC7488r02 = interfaceC7488r0;
            C5917h.m3118f(interfaceC7488r02, "$this$LazyVerticalGrid");
            List<String> list = this.f641k;
            DatePickerState datePickerState = this.f642l;
            for (String str : list) {
                InterfaceC7488r0.m1602c(interfaceC7488r02, null, ComposableLambda.m3357c(-1112398117, new C0215t(datePickerState, str), true), 7);
            }
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$e */
    /* loaded from: classes.dex */
    public static final class C0193e extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

        /* renamed from: k */
        public final /* synthetic */ DatePickerState f643k;

        /* renamed from: l */
        public final /* synthetic */ Locale f644l;

        /* renamed from: m */
        public final /* synthetic */ int f645m;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C0193e(DatePickerState datePickerState, Locale locale, int i) {
            super(2);
            this.f643k = datePickerState;
            this.f644l = locale;
            this.f645m = i;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
            num.intValue();
            C0188c.m8272c(this.f643k, this.f644l, interfaceC3921i, this.f645m | 1);
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$f */
    /* loaded from: classes.dex */
    public static final class C0194f extends Lambda implements Function1<LocalDate, Boolean> {

        /* renamed from: k */
        public static final C0194f f646k = new C0194f();

        public C0194f() {
            super(1);
        }

        @Override // kotlin.jvm.functions.Function1
        public final Boolean invoke(LocalDate localDate) {
            C5917h.m3118f(localDate, "it");
            return Boolean.TRUE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$g */
    /* loaded from: classes.dex */
    public static final class C0195g extends Lambda implements Function1<LocalDate, Unit> {

        /* renamed from: k */
        public static final C0195g f647k = new C0195g();

        public C0195g() {
            super(1);
        }

        @Override // kotlin.jvm.functions.Function1
        public final Unit invoke(LocalDate localDate) {
            C5917h.m3118f(localDate, "it");
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$h */
    /* loaded from: classes.dex */
    public static final class C0196h extends Lambda implements Functions<Unit> {

        /* renamed from: k */
        public final /* synthetic */ Function1<LocalDate, Unit> f648k;

        /* renamed from: l */
        public final /* synthetic */ DatePickerState f649l;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public C0196h(Function1<? super LocalDate, Unit> function1, DatePickerState datePickerState) {
            super(0);
            this.f648k = function1;
            this.f649l = datePickerState;
        }

        @Override // kotlin.jvm.functions.Functions
        public final Unit invoke() {
            this.f648k.invoke(this.f649l.m8265a());
            return Unit.INSTANCE;
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$i */
    /* loaded from: classes.dex */
    public static final class C0197i extends Lambda implements Function1<C3996u0, InterfaceC3992t0> {

        /* renamed from: k */
        public final /* synthetic */ Function1<LocalDate, Unit> f650k;

        /* renamed from: l */
        public final /* synthetic */ DatePickerState f651l;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public C0197i(Function1<? super LocalDate, Unit> function1, DatePickerState datePickerState) {
            super(1);
            this.f650k = function1;
            this.f651l = datePickerState;
        }

        @Override // kotlin.jvm.functions.Function1
        public final InterfaceC3992t0 invoke(C3996u0 c3996u0) {
            C5917h.m3118f(c3996u0, "$this$DisposableEffect");
            this.f650k.invoke(this.f651l.m8265a());
            return new C0187b0();
        }
    }

    /* compiled from: DatePicker.kt */
    /* renamed from: ac.c$j */
    /* loaded from: classes.dex */
    public static final class C0198j extends Lambda implements Function2<InterfaceC3921i, Integer, Unit> {

        /* renamed from: k */
        public final /* synthetic */ InterfaceC8841m f652k;

        /* renamed from: l */
        public final /* synthetic */ LocalDate f653l;

        /* renamed from: m */
        public final /* synthetic */ String f654m;

        /* renamed from: n */
        public final /* synthetic */ DatePickerColors f655n;

        /* renamed from: o */
        public final /* synthetic */ C7328i f656o;

        /* renamed from: p */
        public final /* synthetic */ boolean f657p;

        /* renamed from: q */
        public final /* synthetic */ Function1<LocalDate, Boolean> f658q;

        /* renamed from: r */
        public final /* synthetic */ Locale f659r;

        /* renamed from: s */
        public final /* synthetic */ Function1<LocalDate, Unit> f660s;

        /* renamed from: t */
        public final /* synthetic */ int f661t;

        /* renamed from: u */
        public final /* synthetic */ int f662u;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public C0198j(InterfaceC8841m interfaceC8841m, LocalDate localDate, String str, DatePickerColors datePickerColors, C7328i c7328i, boolean z, Function1<? super LocalDate, Boolean> function1, Locale locale, Function1<? super LocalDate, Unit> function12, int i, int i2) {
            super(2);
            this.f652k = interfaceC8841m;
            this.f653l = localDate;
            this.f654m = str;
            this.f655n = datePickerColors;
            this.f656o = c7328i;
            this.f657p = z;
            this.f658q = function1;
            this.f659r = locale;
            this.f660s = function12;
            this.f661t = i;
            this.f662u = i2;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Unit invoke(InterfaceC3921i interfaceC3921i, Integer num) {
            num.intValue();
            C0188c.m8266i(this.f652k, this.f653l, this.f654m, this.f655n, this.f656o, this.f657p, this.f658q, this.f659r, this.f660s, interfaceC3921i, this.f661t | 1, this.f662u);
            return Unit.INSTANCE;
        }
    }

    /* renamed from: a */
    public static final void m8274a(String str, DatePickerState datePickerState, Locale locale, InterfaceC3921i interfaceC3921i, int i) {
        float f;
        float f2;
        int i2;
        C3928j mo5162p = interfaceC3921i.mo5162p(-747740625);
        LocalDate m8265a = datePickerState.m8265a();
        mo5162p.mo5184e(1157296644);
        boolean mo5216H = mo5162p.mo5216H(m8265a);
        Object m5183e0 = mo5162p.m5183e0();
        if (mo5216H || m5183e0 == InterfaceC3921i.C3922a.f12318a) {
            Month month = datePickerState.m8265a().getMonth();
            C5917h.m3119e(month, "state.selected.month");
            C5917h.m3118f(locale, "locale");
            m5183e0 = month.getDisplayName(TextStyle.SHORT, locale);
            C5917h.m3119e(m5183e0, "this.getDisplayName(java….TextStyle.SHORT, locale)");
            mo5162p.m5209K0(m5183e0);
        }
        mo5162p.m5198U(false);
        String str2 = (String) m5183e0;
        LocalDate m8265a2 = datePickerState.m8265a();
        mo5162p.mo5184e(1157296644);
        boolean mo5216H2 = mo5162p.mo5216H(m8265a2);
        Object m5183e02 = mo5162p.m5183e0();
        if (mo5216H2 || m5183e02 == InterfaceC3921i.C3922a.f12318a) {
            DayOfWeek dayOfWeek = datePickerState.m8265a().getDayOfWeek();
            C5917h.m3119e(dayOfWeek, "state.selected.dayOfWeek");
            C5917h.m3118f(locale, "locale");
            m5183e02 = dayOfWeek.getDisplayName(TextStyle.SHORT, locale);
            mo5162p.m5209K0(m5183e02);
        }
        mo5162p.m5198U(false);
        String str3 = (String) m5183e02;
        InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
        InterfaceC6973f m2267h = C6919o1.m2267h(Installation.m6546u(c6974a, datePickerState.f663a.mo8261d(), RectangleShape.f25152a));
        mo5162p.mo5184e(733328855);
        C6966b c6966b = Alignment.C6963a.f21534a;
        MeasurePolicy m2281c = C6890h.m2281c(c6966b, false, mo5162p);
        mo5162p.mo5184e(-1323940314);
        CompositionLocal compositionLocal = CompositionLocals.f1901e;
        Density density = (Density) mo5162p.mo5154t(compositionLocal);
        CompositionLocal compositionLocal2 = CompositionLocals.f1907k;
        LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
        CompositionLocal compositionLocal3 = CompositionLocals.f1911o;
        ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
        ComposeUiNode.f16753e.getClass();
        C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
        C5685a m4068a = C4834r.m4068a(m2267h);
        if (mo5162p.f12358a instanceof InterfaceC3895d) {
            mo5162p.mo5158r();
            if (mo5162p.f12345L) {
                mo5162p.mo5148w(c5407a);
            } else {
                mo5162p.mo5230A();
            }
            mo5162p.f12381x = false;
            ComposeUiNode.C5354a.C5357c c5357c = ComposeUiNode.C5354a.f16758e;
            Installation.m6566d0(mo5162p, m2281c, c5357c);
            ComposeUiNode.C5354a.C5355a c5355a = ComposeUiNode.C5354a.f16757d;
            Installation.m6566d0(mo5162p, density, c5355a);
            ComposeUiNode.C5354a.C5356b c5356b = ComposeUiNode.C5354a.f16759f;
            Installation.m6566d0(mo5162p, layoutDirection, c5356b);
            ComposeUiNode.C5354a.C5359e c5359e = ComposeUiNode.C5354a.f16760g;
            C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, c5359e, mo5162p), mo5162p, 2058660585, -2137368960);
            float f3 = 24;
            InterfaceC6973f m8084m0 = C0270p.m8084m0(c6974a, f3, 0.0f, f3, 0.0f, 10);
            mo5162p.mo5184e(-483455358);
            MeasurePolicy m2256a = Column.m2256a(Arrangement.f21354c, Alignment.C6963a.f21545l, mo5162p);
            mo5162p.mo5184e(-1323940314);
            Density density2 = (Density) mo5162p.mo5154t(compositionLocal);
            LayoutDirection layoutDirection2 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
            ViewConfiguration viewConfiguration2 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
            C5685a m4068a2 = C4834r.m4068a(m8084m0);
            if (mo5162p.f12358a instanceof InterfaceC3895d) {
                mo5162p.mo5158r();
                if (mo5162p.f12345L) {
                    mo5162p.mo5148w(c5407a);
                } else {
                    mo5162p.mo5230A();
                }
                mo5162p.f12381x = false;
                C0271q.m8044n(0, m4068a2, C0269o.m8156c(mo5162p, m2256a, c5357c, mo5162p, density2, c5355a, mo5162p, layoutDirection2, c5356b, mo5162p, viewConfiguration2, c5359e, mo5162p), mo5162p, 2058660585, -1163856341);
                if (C0691q.m7308N(mo5162p)) {
                    f = f3;
                } else {
                    f = 32;
                }
                Text.m7164d(str, C6862b.m2300b(c6974a, f, Float.NaN), datePickerState.f663a.mo8263b(), 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, new p278s1.TextStyle(0L, ToastSender.m6447m0(12), null, 262141), mo5162p, i & 14, 0, 32760);
                InterfaceC6973f m2268g = C6919o1.m2268g(c6974a, 1.0f);
                if (C0691q.m7308N(mo5162p)) {
                    f2 = 0;
                } else {
                    f2 = 64;
                }
                InterfaceC6973f m2299c = C6862b.m2299c(m2268g, f2);
                mo5162p.mo5184e(733328855);
                MeasurePolicy m2281c2 = C6890h.m2281c(c6966b, false, mo5162p);
                mo5162p.mo5184e(-1323940314);
                Density density3 = (Density) mo5162p.mo5154t(compositionLocal);
                LayoutDirection layoutDirection3 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
                ViewConfiguration viewConfiguration3 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
                C5685a m4068a3 = C4834r.m4068a(m2299c);
                if (mo5162p.f12358a instanceof InterfaceC3895d) {
                    mo5162p.mo5158r();
                    if (mo5162p.f12345L) {
                        mo5162p.mo5148w(c5407a);
                    } else {
                        mo5162p.mo5230A();
                    }
                    mo5162p.f12381x = false;
                    m4068a3.invoke(C0269o.m8156c(mo5162p, m2281c2, c5357c, mo5162p, density3, c5355a, mo5162p, layoutDirection3, c5356b, mo5162p, viewConfiguration3, c5359e, mo5162p), mo5162p, 0);
                    mo5162p.mo5184e(2058660585);
                    mo5162p.mo5184e(-2137368960);
                    Text.m7164d(str3 + ", " + str2 + ' ' + datePickerState.m8265a().getDayOfMonth(), new Box(Alignment.C6963a.f21536c, false), datePickerState.f663a.mo8263b(), 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, new p278s1.TextStyle(0L, ToastSender.m6447m0(30), FontWeight.f25323k, 262137), mo5162p, 0, 0, 32760);
                    C0264f.m8166i(mo5162p, false, false, true, false);
                    mo5162p.m5198U(false);
                    if (C0691q.m7308N(mo5162p)) {
                        i2 = 8;
                    } else {
                        i2 = 16;
                    }
                    Installation.m6567d(C6919o1.m2266i(c6974a, i2), mo5162p, 0);
                    mo5162p.m5198U(false);
                    mo5162p.m5198U(false);
                    mo5162p.m5198U(true);
                    mo5162p.m5198U(false);
                    mo5162p.m5198U(false);
                    C0264f.m8166i(mo5162p, false, false, true, false);
                    mo5162p.m5198U(false);
                    C3883b2 m5195X = mo5162p.m5195X();
                    if (m5195X != null) {
                        m5195X.f12208d = new C0189a(str, datePickerState, locale, i);
                        return;
                    }
                    return;
                }
                Installation.m6582Q();
                throw null;
            }
            Installation.m6582Q();
            throw null;
        }
        Installation.m6582Q();
        throw null;
    }

    /* renamed from: b */
    public static final void m8273b(String str, DatePickerState datePickerState, Function1<? super LocalDate, Boolean> function1, Locale locale, InterfaceC3921i interfaceC3921i, int i) {
        C5917h.m3118f(str, "title");
        C5917h.m3118f(datePickerState, "state");
        C5917h.m3118f(function1, "allowedDateValidator");
        C5917h.m3118f(locale, "locale");
        C3928j mo5162p = interfaceC3921i.mo5162p(-965469197);
        PagerState m7276j0 = C0691q.m7276j0((datePickerState.m8265a().getMonthValue() + ((datePickerState.m8265a().getYear() - datePickerState.f664b.f22668j) * 12)) - 1, mo5162p, 0);
        InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
        InterfaceC6973f m2268g = C6919o1.m2268g(c6974a, 1.0f);
        mo5162p.mo5184e(-483455358);
        MeasurePolicy m2256a = Column.m2256a(Arrangement.f21354c, Alignment.C6963a.f21545l, mo5162p);
        mo5162p.mo5184e(-1323940314);
        Density density = (Density) mo5162p.mo5154t(CompositionLocals.f1901e);
        LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(CompositionLocals.f1907k);
        ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(CompositionLocals.f1911o);
        ComposeUiNode.f16753e.getClass();
        C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
        C5685a m4068a = C4834r.m4068a(m2268g);
        if (mo5162p.f12358a instanceof InterfaceC3895d) {
            mo5162p.mo5158r();
            if (mo5162p.f12345L) {
                mo5162p.mo5148w(c5407a);
            } else {
                mo5162p.mo5230A();
            }
            mo5162p.f12381x = false;
            Installation.m6566d0(mo5162p, m2256a, ComposeUiNode.C5354a.f16758e);
            Installation.m6566d0(mo5162p, density, ComposeUiNode.C5354a.f16757d);
            Installation.m6566d0(mo5162p, layoutDirection, ComposeUiNode.C5354a.f16759f);
            C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, ComposeUiNode.C5354a.f16760g, mo5162p), mo5162p, 2058660585, -1163856341);
            m8274a(str, datePickerState, locale, mo5162p, (i & 14) | 576);
            C7328i c7328i = datePickerState.f664b;
            C8172b.m629a(((c7328i.f22669k - c7328i.f22668j) + 1) * 12, C6919o1.m2266i(c6974a, 336), m7276j0, false, 0.0f, null, Alignment.C6963a.f21542i, null, null, false, ComposableLambda.m3358b(mo5162p, -627727400, new C0190b(datePickerState, m7276j0, locale, function1, i)), mo5162p, 1572912, 6, 952);
            C0264f.m8166i(mo5162p, false, false, true, false);
            mo5162p.m5198U(false);
            C3883b2 m5195X = mo5162p.m5195X();
            if (m5195X != null) {
                m5195X.f12208d = new C0191c(str, datePickerState, function1, locale, i);
                return;
            }
            return;
        }
        Installation.m6582Q();
        throw null;
    }

    /* renamed from: c */
    public static final void m8272c(DatePickerState datePickerState, Locale locale, InterfaceC3921i interfaceC3921i, int i) {
        C3928j mo5162p = interfaceC3921i.mo5162p(286240229);
        DayOfWeek firstDayOfWeek = WeekFields.of(locale).getFirstDayOfWeek();
        C7331l c7331l = new C7331l(0L, 6L);
        ArrayList arrayList = new ArrayList(IteratorsJVM.m5698D0(c7331l));
        Iterator<Long> it = c7331l.iterator();
        while (((C7330k) it).f22681l) {
            arrayList.add(firstDayOfWeek.plus(((AbstractC3650g0) it).nextLong()).getDisplayName(TextStyle.NARROW, locale));
        }
        InterfaceC6973f m2267h = C6919o1.m2267h(C6919o1.m2266i(InterfaceC6973f.C6974a.f21559j, 40));
        C6966b.C6968b c6968b = Alignment.C6963a.f21543j;
        Arrangement.C6875g c6875g = Arrangement.f21357f;
        mo5162p.mo5184e(693286680);
        MeasurePolicy m2280a = Row.m2280a(c6875g, c6968b, mo5162p);
        mo5162p.mo5184e(-1323940314);
        Density density = (Density) mo5162p.mo5154t(CompositionLocals.f1901e);
        LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(CompositionLocals.f1907k);
        ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(CompositionLocals.f1911o);
        ComposeUiNode.f16753e.getClass();
        C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
        C5685a m4068a = C4834r.m4068a(m2267h);
        if (mo5162p.f12358a instanceof InterfaceC3895d) {
            mo5162p.mo5158r();
            if (mo5162p.f12345L) {
                mo5162p.mo5148w(c5407a);
            } else {
                mo5162p.mo5230A();
            }
            mo5162p.f12381x = false;
            Installation.m6566d0(mo5162p, m2280a, ComposeUiNode.C5354a.f16758e);
            Installation.m6566d0(mo5162p, density, ComposeUiNode.C5354a.f16757d);
            Installation.m6566d0(mo5162p, layoutDirection, ComposeUiNode.C5354a.f16759f);
            m4068a.invoke(C0582d0.m7542e(mo5162p, viewConfiguration, ComposeUiNode.C5354a.f16760g, mo5162p), mo5162p, 0);
            mo5162p.mo5184e(2058660585);
            mo5162p.mo5184e(-678309503);
            C7470h.m1622a(new InterfaceC7455b.C7456a(7), null, null, null, false, null, null, null, false, new C0192d(arrayList, datePickerState), mo5162p, 0, 510);
            C0264f.m8166i(mo5162p, false, false, true, false);
            mo5162p.m5198U(false);
            C3883b2 m5195X = mo5162p.m5195X();
            if (m5195X != null) {
                m5195X.f12208d = new C0193e(datePickerState, locale, i);
                return;
            }
            return;
        }
        Installation.m6582Q();
        throw null;
    }

    /* renamed from: d */
    public static final void m8271d(LocalDate localDate, DatePickerState datePickerState, Locale locale, Function1 function1, InterfaceC3921i interfaceC3921i, int i) {
        C3928j mo5162p = interfaceC3921i.mo5162p(-405884166);
        InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
        float f = 12;
        InterfaceC6973f m7864a = TestTag.m7864a(C0270p.m8084m0(c6974a, f, 0.0f, f, 0.0f, 10), "dialog_date_calendar");
        mo5162p.mo5184e(-483455358);
        MeasurePolicy m2256a = Column.m2256a(Arrangement.f21354c, Alignment.C6963a.f21545l, mo5162p);
        mo5162p.mo5184e(-1323940314);
        Density density = (Density) mo5162p.mo5154t(CompositionLocals.f1901e);
        LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(CompositionLocals.f1907k);
        ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(CompositionLocals.f1911o);
        ComposeUiNode.f16753e.getClass();
        C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
        C5685a m4068a = C4834r.m4068a(m7864a);
        if (mo5162p.f12358a instanceof InterfaceC3895d) {
            mo5162p.mo5158r();
            if (mo5162p.f12345L) {
                mo5162p.mo5148w(c5407a);
            } else {
                mo5162p.mo5230A();
            }
            mo5162p.f12381x = false;
            Installation.m6566d0(mo5162p, m2256a, ComposeUiNode.C5354a.f16758e);
            Installation.m6566d0(mo5162p, density, ComposeUiNode.C5354a.f16757d);
            Installation.m6566d0(mo5162p, layoutDirection, ComposeUiNode.C5354a.f16759f);
            C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, ComposeUiNode.C5354a.f16760g, mo5162p), mo5162p, 2058660585, -1163856341);
            m8272c(datePickerState, locale, mo5162p, 72);
            mo5162p.mo5184e(-492369756);
            Object m5183e0 = mo5162p.m5183e0();
            InterfaceC3921i.C3922a.C3923a c3923a = InterfaceC3921i.C3922a.f12318a;
            boolean z = true;
            if (m5183e0 == c3923a) {
                Tuples tuples = new Tuples(Integer.valueOf(localDate.withDayOfMonth(1).getDayOfWeek().getValue() - (WeekFields.of(locale).getFirstDayOfWeek().getValue() % 7)), Integer.valueOf(localDate.getMonth().length(localDate.isLeapYear())));
                mo5162p.m5209K0(tuples);
                m5183e0 = tuples;
            }
            mo5162p.m5198U(false);
            Tuples tuples2 = (Tuples) m5183e0;
            mo5162p.mo5184e(-492369756);
            Object m5183e02 = mo5162p.m5183e0();
            if (m5183e02 == c3923a) {
                m5183e02 = C3662x.m5652v1(new C7328i(1, ((Number) tuples2.f10760k).intValue()));
                mo5162p.m5209K0(m5183e02);
            }
            mo5162p.m5198U(false);
            List list = (List) m5183e02;
            LocalDate m8265a = datePickerState.m8265a();
            mo5162p.mo5184e(1157296644);
            boolean mo5216H = mo5162p.mo5216H(m8265a);
            Object m5183e03 = mo5162p.m5183e0();
            if (mo5216H || m5183e03 == c3923a) {
                if (localDate.getYear() != datePickerState.m8265a().getYear() || localDate.getMonth() != datePickerState.m8265a().getMonth()) {
                    z = false;
                }
                m5183e03 = Boolean.valueOf(z);
                mo5162p.m5209K0(m5183e03);
            }
            mo5162p.m5198U(false);
            C7470h.m1622a(new InterfaceC7455b.C7456a(7), C6919o1.m2266i(c6974a, 240), null, null, false, null, null, null, false, new C0202g(tuples2, list, datePickerState, localDate, function1, ((Boolean) m5183e03).booleanValue()), mo5162p, 48, 508);
            C0264f.m8166i(mo5162p, false, false, true, false);
            mo5162p.m5198U(false);
            C3883b2 m5195X = mo5162p.m5195X();
            if (m5195X != null) {
                m5195X.f12208d = new C0203h(localDate, datePickerState, locale, function1, i);
                return;
            }
            return;
        }
        Installation.m6582Q();
        throw null;
    }

    /* renamed from: e */
    public static final void m8270e(LocalDate localDate, DatePickerState datePickerState, PagerState pagerState, Locale locale, InterfaceC3921i interfaceC3921i, int i) {
        C5406z.C5407a c5407a;
        Painter painter;
        boolean z;
        C3928j mo5162p = interfaceC3921i.mo5162p(3561846);
        mo5162p.mo5184e(773894976);
        mo5162p.mo5184e(-492369756);
        Object m5183e0 = mo5162p.m5183e0();
        InterfaceC3921i.C3922a.C3923a c3923a = InterfaceC3921i.C3922a.f12318a;
        if (m5183e0 == c3923a) {
            m5183e0 = C0271q.m8050h(C4004w0.m4994h(mo5162p), mo5162p);
        }
        mo5162p.m5198U(false);
        CoroutineScope coroutineScope = ((C3963n0) m5183e0).f12466j;
        mo5162p.m5198U(false);
        mo5162p.mo5184e(-492369756);
        Object m5183e02 = mo5162p.m5183e0();
        if (m5183e02 == c3923a) {
            Month month = localDate.getMonth();
            C5917h.m3119e(month, "viewDate.month");
            C5917h.m3118f(locale, "locale");
            m5183e02 = month.getDisplayName(TextStyle.FULL_STANDALONE, locale);
            mo5162p.m5209K0(m5183e02);
        }
        mo5162p.m5198U(false);
        String str = (String) m5183e02;
        Painter m2736a = C6327b.m2736a(C1518R.C1519drawable.baseline_arrow_drop_up_24, mo5162p);
        Painter m2736a2 = C6327b.m2736a(C1518R.C1519drawable.baseline_arrow_drop_down_24, mo5162p);
        InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
        float f = 16;
        float f2 = 24;
        C6961z0 c6961z0 = new C6961z0(f2, f, f2, f);
        c6974a.mo2243u0(c6961z0);
        InterfaceC6973f m2267h = C6919o1.m2267h(C6919o1.m2266i(c6961z0, f2));
        mo5162p.mo5184e(733328855);
        MeasurePolicy m2281c = C6890h.m2281c(Alignment.C6963a.f21534a, false, mo5162p);
        mo5162p.mo5184e(-1323940314);
        CompositionLocal compositionLocal = CompositionLocals.f1901e;
        Density density = (Density) mo5162p.mo5154t(compositionLocal);
        CompositionLocal compositionLocal2 = CompositionLocals.f1907k;
        LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
        CompositionLocal compositionLocal3 = CompositionLocals.f1911o;
        ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
        ComposeUiNode.f16753e.getClass();
        C5406z.C5407a c5407a2 = ComposeUiNode.C5354a.f16755b;
        C5685a m4068a = C4834r.m4068a(m2267h);
        if (mo5162p.f12358a instanceof InterfaceC3895d) {
            mo5162p.mo5158r();
            if (mo5162p.f12345L) {
                mo5162p.mo5148w(c5407a2);
            } else {
                mo5162p.mo5230A();
            }
            mo5162p.f12381x = false;
            ComposeUiNode.C5354a.C5357c c5357c = ComposeUiNode.C5354a.f16758e;
            Installation.m6566d0(mo5162p, m2281c, c5357c);
            ComposeUiNode.C5354a.C5355a c5355a = ComposeUiNode.C5354a.f16757d;
            Installation.m6566d0(mo5162p, density, c5355a);
            ComposeUiNode.C5354a.C5356b c5356b = ComposeUiNode.C5354a.f16759f;
            Installation.m6566d0(mo5162p, layoutDirection, c5356b);
            ComposeUiNode.C5354a.C5359e c5359e = ComposeUiNode.C5354a.f16760g;
            C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, c5359e, mo5162p), mo5162p, 2058660585, -2137368960);
            InterfaceC6973f m2270e = C6919o1.m2270e(c6974a);
            C6966b c6966b = Alignment.C6963a.f21536c;
            C5917h.m3118f(m2270e, "<this>");
            InterfaceC6973f m2742d = C6271u.m2742d(m2270e.mo2243u0(new Box(c6966b, false)), false, new C0204i(datePickerState), 7);
            mo5162p.mo5184e(693286680);
            Arrangement.C6877i c6877i = Arrangement.f21352a;
            C6966b.C6968b c6968b = Alignment.C6963a.f21542i;
            MeasurePolicy m2280a = Row.m2280a(c6877i, c6968b, mo5162p);
            mo5162p.mo5184e(-1323940314);
            Density density2 = (Density) mo5162p.mo5154t(compositionLocal);
            LayoutDirection layoutDirection2 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
            ViewConfiguration viewConfiguration2 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
            C5685a m4068a2 = C4834r.m4068a(m2742d);
            if (mo5162p.f12358a instanceof InterfaceC3895d) {
                mo5162p.mo5158r();
                if (mo5162p.f12345L) {
                    mo5162p.mo5148w(c5407a2);
                } else {
                    mo5162p.mo5230A();
                }
                mo5162p.f12381x = false;
                m4068a2.invoke(C0269o.m8156c(mo5162p, m2280a, c5357c, mo5162p, density2, c5355a, mo5162p, layoutDirection2, c5356b, mo5162p, viewConfiguration2, c5359e, mo5162p), mo5162p, 0);
                mo5162p.mo5184e(2058660585);
                mo5162p.mo5184e(-678309503);
                InterfaceC6973f m2300b = C6862b.m2300b(c6974a, f, Float.NaN);
                C6966b c6966b2 = Alignment.C6963a.f21537d;
                Text.m7164d(str + ' ' + localDate.getYear(), C6919o1.m2257r(m2300b, c6966b2, 2), datePickerState.f663a.mo8260e(), 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, new p278s1.TextStyle(0L, ToastSender.m6447m0(14), FontWeight.f25325m, 262137), mo5162p, 48, 0, 32760);
                Installation.m6567d(C6919o1.m2260o(c6974a, 4), mo5162p, 6);
                InterfaceC6973f m2263l = C6919o1.m2263l(c6974a, f2);
                mo5162p.mo5184e(733328855);
                MeasurePolicy m2281c2 = C6890h.m2281c(c6966b2, false, mo5162p);
                mo5162p.mo5184e(-1323940314);
                Density density3 = (Density) mo5162p.mo5154t(compositionLocal);
                LayoutDirection layoutDirection3 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
                ViewConfiguration viewConfiguration3 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
                C5685a m4068a3 = C4834r.m4068a(m2263l);
                if (mo5162p.f12358a instanceof InterfaceC3895d) {
                    mo5162p.mo5158r();
                    if (mo5162p.f12345L) {
                        c5407a = c5407a2;
                        mo5162p.mo5148w(c5407a);
                    } else {
                        c5407a = c5407a2;
                        mo5162p.mo5230A();
                    }
                    mo5162p.f12381x = false;
                    C5406z.C5407a c5407a3 = c5407a;
                    C0271q.m8044n(0, m4068a3, C0269o.m8156c(mo5162p, m2281c2, c5357c, mo5162p, density3, c5355a, mo5162p, layoutDirection3, c5356b, mo5162p, viewConfiguration3, c5359e, mo5162p), mo5162p, 2058660585, -2137368960);
                    if (((Boolean) datePickerState.f667e.getValue()).booleanValue()) {
                        painter = m2736a;
                    } else {
                        painter = m2736a2;
                    }
                    Icon.m7153a(painter, "Year Selector", null, datePickerState.f663a.mo8260e(), mo5162p, 56, 4);
                    C0264f.m8166i(mo5162p, false, false, true, false);
                    C0264f.m8166i(mo5162p, false, false, false, true);
                    mo5162p.m5198U(false);
                    mo5162p.m5198U(false);
                    InterfaceC6973f m2270e2 = C6919o1.m2270e(c6974a);
                    C6966b c6966b3 = Alignment.C6963a.f21538e;
                    C5917h.m3118f(m2270e2, "<this>");
                    InterfaceC6973f mo2243u0 = m2270e2.mo2243u0(new Box(c6966b3, false));
                    mo5162p.mo5184e(693286680);
                    MeasurePolicy m2280a2 = Row.m2280a(c6877i, c6968b, mo5162p);
                    mo5162p.mo5184e(-1323940314);
                    Density density4 = (Density) mo5162p.mo5154t(compositionLocal);
                    LayoutDirection layoutDirection4 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
                    ViewConfiguration viewConfiguration4 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
                    C5685a m4068a4 = C4834r.m4068a(mo2243u0);
                    if (mo5162p.f12358a instanceof InterfaceC3895d) {
                        mo5162p.mo5158r();
                        if (mo5162p.f12345L) {
                            mo5162p.mo5148w(c5407a3);
                        } else {
                            mo5162p.mo5230A();
                        }
                        mo5162p.f12381x = false;
                        C0271q.m8044n(0, m4068a4, C0269o.m8156c(mo5162p, m2280a2, c5357c, mo5162p, density4, c5355a, mo5162p, layoutDirection4, c5356b, mo5162p, viewConfiguration4, c5359e, mo5162p), mo5162p, 2058660585, -678309503);
                        ImageVector imageVector = KeyboardArrowLeft.f5146a;
                        if (imageVector != null) {
                            z = false;
                        } else {
                            ImageVector.C1173a c1173a = new ImageVector.C1173a("Filled.KeyboardArrowLeft", 24.0f, 24.0f, 24.0f, 24.0f, 0L, 0, false, 224);
                            int i2 = C1233n.f4783a;
                            C8108t0 c8108t0 = new C8108t0(C8110v.f25208b);
                            PathBuilder m8052f = C0271q.m8052f(15.41f, 16.59f, 10.83f, 12.0f);
                            m8052f.m6982h(4.58f, -4.59f);
                            m8052f.m6983g(14.0f, 6.0f);
                            m8052f.m6982h(-6.0f, 6.0f);
                            m8052f.m6982h(6.0f, 6.0f);
                            m8052f.m6982h(1.41f, -1.41f);
                            m8052f.m6988b();
                            z = false;
                            ImageVector.C1173a.m6993c(c1173a, m8052f.f4616a, 0, c8108t0);
                            imageVector = c1173a.m6992d();
                            KeyboardArrowLeft.f5146a = imageVector;
                        }
                        Icon.m7152b(imageVector, "Previous Month", C6271u.m2742d(C6919o1.m2263l(TestTag.m7864a(c6974a, "dialog_date_prev_month"), f2), z, new C0206k(coroutineScope, pagerState), 7), datePickerState.f663a.mo8260e(), mo5162p, 48, 0);
                        Installation.m6567d(C6919o1.m2260o(c6974a, f2), mo5162p, 6);
                        Icon.m7152b(KeyboardArrowRight.m6861a(), "Next Month", C6271u.m2742d(C6919o1.m2263l(TestTag.m7864a(c6974a, "dialog_date_next_month"), f2), false, new C0208m(coroutineScope, pagerState), 7), datePickerState.f663a.mo8260e(), mo5162p, 48, 0);
                        C0264f.m8166i(mo5162p, false, false, true, false);
                        C0264f.m8166i(mo5162p, false, false, false, true);
                        mo5162p.m5198U(false);
                        mo5162p.m5198U(false);
                        C3883b2 m5195X = mo5162p.m5195X();
                        if (m5195X != null) {
                            m5195X.f12208d = new C0209n(localDate, datePickerState, pagerState, locale, i);
                            return;
                        }
                        return;
                    }
                    Installation.m6582Q();
                    throw null;
                }
                Installation.m6582Q();
                throw null;
            }
            Installation.m6582Q();
            throw null;
        }
        Installation.m6582Q();
        throw null;
    }

    /* renamed from: f */
    public static final void m8269f(int i, boolean z, DatePickerColors datePickerColors, boolean z2, Functions functions, InterfaceC3921i interfaceC3921i, int i2) {
        int i3;
        float m5385g0;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        C3928j mo5162p = interfaceC3921i.mo5162p(-1594512812);
        if ((i2 & 14) == 0) {
            if (mo5162p.mo5176i(i)) {
                i8 = 4;
            } else {
                i8 = 2;
            }
            i3 = i8 | i2;
        } else {
            i3 = i2;
        }
        if ((i2 & 112) == 0) {
            if (mo5162p.mo5188c(z)) {
                i7 = 32;
            } else {
                i7 = 16;
            }
            i3 |= i7;
        }
        if ((i2 & 896) == 0) {
            if (mo5162p.mo5216H(datePickerColors)) {
                i6 = 256;
            } else {
                i6 = 128;
            }
            i3 |= i6;
        }
        if ((i2 & 7168) == 0) {
            if (mo5162p.mo5188c(z2)) {
                i5 = 2048;
            } else {
                i5 = 1024;
            }
            i3 |= i5;
        }
        if ((57344 & i2) == 0) {
            if (mo5162p.mo5216H(functions)) {
                i4 = 16384;
            } else {
                i4 = 8192;
            }
            i3 |= i4;
        }
        if ((i3 & 46811) == 9362 && mo5162p.mo5156s()) {
            mo5162p.mo5144y();
        } else {
            InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
            InterfaceC6973f m2263l = C6919o1.m2263l(TestTag.m7864a(c6974a, "dialog_date_selection_" + i), 40);
            C6751m c6751m = new C6751m();
            Boolean valueOf = Boolean.valueOf(z2);
            mo5162p.mo5184e(511388516);
            boolean mo5216H = mo5162p.mo5216H(valueOf) | mo5162p.mo5216H(functions);
            Object m5183e0 = mo5162p.m5183e0();
            if (mo5216H || m5183e0 == InterfaceC3921i.C3922a.f12318a) {
                m5183e0 = new C0213r(z2, functions);
                mo5162p.m5209K0(m5183e0);
            }
            mo5162p.m5198U(false);
            InterfaceC6973f m2743c = C6271u.m2743c(m2263l, c6751m, null, false, null, (Functions) m5183e0, 28);
            C6966b c6966b = Alignment.C6963a.f21537d;
            mo5162p.mo5184e(733328855);
            MeasurePolicy m2281c = C6890h.m2281c(c6966b, false, mo5162p);
            mo5162p.mo5184e(-1323940314);
            Density density = (Density) mo5162p.mo5154t(CompositionLocals.f1901e);
            LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(CompositionLocals.f1907k);
            ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(CompositionLocals.f1911o);
            ComposeUiNode.f16753e.getClass();
            C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
            C5685a m4068a = C4834r.m4068a(m2743c);
            if (mo5162p.f12358a instanceof InterfaceC3895d) {
                mo5162p.mo5158r();
                if (mo5162p.f12345L) {
                    mo5162p.mo5148w(c5407a);
                } else {
                    mo5162p.mo5230A();
                }
                mo5162p.f12381x = false;
                Installation.m6566d0(mo5162p, m2281c, ComposeUiNode.C5354a.f16758e);
                Installation.m6566d0(mo5162p, density, ComposeUiNode.C5354a.f16757d);
                Installation.m6566d0(mo5162p, layoutDirection, ComposeUiNode.C5354a.f16759f);
                C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, ComposeUiNode.C5354a.f16760g, mo5162p), mo5162p, 2058660585, -2137368960);
                String valueOf2 = String.valueOf(i);
                InterfaceC6973f m2257r = C6919o1.m2257r(Installation.m6545v(C3780c.m5445C(C6919o1.m2263l(c6974a, 32), C8390h.f25899a), ((C8110v) datePickerColors.mo8264a(z, mo5162p).getValue()).f25219a), c6966b, 2);
                if (z2) {
                    mo5162p.mo5184e(71532136);
                    m5385g0 = C3780c.m5379j0(mo5162p, 8);
                } else {
                    mo5162p.mo5184e(71532159);
                    m5385g0 = C3780c.m5385g0(mo5162p, 8);
                }
                mo5162p.m5198U(false);
                Text.m7164d(valueOf2, C0565j.m7586p(m2257r, m5385g0), 0L, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, new p278s1.TextStyle(((C8110v) datePickerColors.mo8262c(z, mo5162p).getValue()).f25219a, ToastSender.m6447m0(12), null, 262140), mo5162p, 0, 0, 32764);
                C0264f.m8166i(mo5162p, false, false, true, false);
                mo5162p.m5198U(false);
            } else {
                Installation.m6582Q();
                throw null;
            }
        }
        C3883b2 m5195X = mo5162p.m5195X();
        if (m5195X != null) {
            m5195X.f12208d = new C0214s(i, z, datePickerColors, z2, functions, i2);
        }
    }

    /* renamed from: g */
    public static final void m8268g(LocalDate localDate, DatePickerState datePickerState, PagerState pagerState, InterfaceC3921i interfaceC3921i, int i) {
        C3928j mo5162p = interfaceC3921i.mo5162p(-934779207);
        C7507z0 m6573Z = Installation.m6573Z(localDate.getYear() - datePickerState.f664b.f22668j, mo5162p, 2);
        mo5162p.mo5184e(773894976);
        mo5162p.mo5184e(-492369756);
        Object m5183e0 = mo5162p.m5183e0();
        if (m5183e0 == InterfaceC3921i.C3922a.f12318a) {
            m5183e0 = C0271q.m8050h(C4004w0.m4994h(mo5162p), mo5162p);
        }
        mo5162p.m5198U(false);
        CoroutineScope coroutineScope = ((C3963n0) m5183e0).f12466j;
        mo5162p.m5198U(false);
        C7470h.m1622a(new InterfaceC7455b.C7456a(3), Installation.m6546u(InterfaceC6973f.C6974a.f21559j, datePickerState.f665c, RectangleShape.f25152a), m6573Z, null, false, null, null, null, false, new C0220y(datePickerState, localDate, coroutineScope, pagerState), mo5162p, 0, 504);
        C3883b2 m5195X = mo5162p.m5195X();
        if (m5195X != null) {
            m5195X.f12208d = new C0221z(localDate, datePickerState, pagerState, i);
        }
    }

    /* renamed from: h */
    public static final void m8267h(int i, boolean z, DatePickerColors datePickerColors, Functions functions, InterfaceC3921i interfaceC3921i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        C3928j mo5162p = interfaceC3921i.mo5162p(-1688565145);
        if ((i2 & 14) == 0) {
            if (mo5162p.mo5176i(i)) {
                i7 = 4;
            } else {
                i7 = 2;
            }
            i3 = i7 | i2;
        } else {
            i3 = i2;
        }
        if ((i2 & 112) == 0) {
            if (mo5162p.mo5188c(z)) {
                i6 = 32;
            } else {
                i6 = 16;
            }
            i3 |= i6;
        }
        if ((i2 & 896) == 0) {
            if (mo5162p.mo5216H(datePickerColors)) {
                i5 = 256;
            } else {
                i5 = 128;
            }
            i3 |= i5;
        }
        if ((i2 & 7168) == 0) {
            if (mo5162p.mo5216H(functions)) {
                i4 = 2048;
            } else {
                i4 = 1024;
            }
            i3 |= i4;
        }
        if ((i3 & 5851) == 1170 && mo5162p.mo5156s()) {
            mo5162p.mo5144y();
        } else {
            InterfaceC6973f.C6974a c6974a = InterfaceC6973f.C6974a.f21559j;
            InterfaceC6973f m2262m = C6919o1.m2262m(c6974a, 88, 52);
            C6966b c6966b = Alignment.C6963a.f21537d;
            mo5162p.mo5184e(733328855);
            MeasurePolicy m2281c = C6890h.m2281c(c6966b, false, mo5162p);
            mo5162p.mo5184e(-1323940314);
            CompositionLocal compositionLocal = CompositionLocals.f1901e;
            Density density = (Density) mo5162p.mo5154t(compositionLocal);
            CompositionLocal compositionLocal2 = CompositionLocals.f1907k;
            LayoutDirection layoutDirection = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
            CompositionLocal compositionLocal3 = CompositionLocals.f1911o;
            ViewConfiguration viewConfiguration = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
            ComposeUiNode.f16753e.getClass();
            C5406z.C5407a c5407a = ComposeUiNode.C5354a.f16755b;
            C5685a m4068a = C4834r.m4068a(m2262m);
            if (mo5162p.f12358a instanceof InterfaceC3895d) {
                mo5162p.mo5158r();
                if (mo5162p.f12345L) {
                    mo5162p.mo5148w(c5407a);
                } else {
                    mo5162p.mo5230A();
                }
                mo5162p.f12381x = false;
                ComposeUiNode.C5354a.C5357c c5357c = ComposeUiNode.C5354a.f16758e;
                Installation.m6566d0(mo5162p, m2281c, c5357c);
                ComposeUiNode.C5354a.C5355a c5355a = ComposeUiNode.C5354a.f16757d;
                Installation.m6566d0(mo5162p, density, c5355a);
                ComposeUiNode.C5354a.C5356b c5356b = ComposeUiNode.C5354a.f16759f;
                Installation.m6566d0(mo5162p, layoutDirection, c5356b);
                ComposeUiNode.C5354a.C5359e c5359e = ComposeUiNode.C5354a.f16760g;
                C0271q.m8044n(0, m4068a, C0582d0.m7542e(mo5162p, viewConfiguration, c5359e, mo5162p), mo5162p, 2058660585, -2137368960);
                InterfaceC6973f m2743c = C6271u.m2743c(Installation.m6545v(C3780c.m5445C(C6919o1.m2262m(c6974a, 72, 36), C8390h.m424a(16)), ((C8110v) datePickerColors.mo8264a(z, mo5162p).getValue()).f25219a), new C6751m(), null, false, null, functions, 28);
                mo5162p.mo5184e(733328855);
                MeasurePolicy m2281c2 = C6890h.m2281c(c6966b, false, mo5162p);
                mo5162p.mo5184e(-1323940314);
                Density density2 = (Density) mo5162p.mo5154t(compositionLocal);
                LayoutDirection layoutDirection2 = (LayoutDirection) mo5162p.mo5154t(compositionLocal2);
                ViewConfiguration viewConfiguration2 = (ViewConfiguration) mo5162p.mo5154t(compositionLocal3);
                C5685a m4068a2 = C4834r.m4068a(m2743c);
                if (mo5162p.f12358a instanceof InterfaceC3895d) {
                    mo5162p.mo5158r();
                    if (mo5162p.f12345L) {
                        mo5162p.mo5148w(c5407a);
                    } else {
                        mo5162p.mo5230A();
                    }
                    mo5162p.f12381x = false;
                    C0271q.m8044n(0, m4068a2, C0269o.m8156c(mo5162p, m2281c2, c5357c, mo5162p, density2, c5355a, mo5162p, layoutDirection2, c5356b, mo5162p, viewConfiguration2, c5359e, mo5162p), mo5162p, 2058660585, -2137368960);
                    Text.m7164d(String.valueOf(i), null, 0L, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, new p278s1.TextStyle(((C8110v) datePickerColors.mo8262c(z, mo5162p).getValue()).f25219a, ToastSender.m6447m0(18), null, 262140), mo5162p, 0, 0, 32766);
                    C0264f.m8166i(mo5162p, false, false, true, false);
                    C0264f.m8166i(mo5162p, false, false, false, true);
                    mo5162p.m5198U(false);
                    mo5162p.m5198U(false);
                } else {
                    Installation.m6582Q();
                    throw null;
                }
            } else {
                Installation.m6582Q();
                throw null;
            }
        }
        C3883b2 m5195X = mo5162p.m5195X();
        if (m5195X != null) {
            m5195X.f12208d = new C0186a0(i, z, datePickerColors, functions, i2);
        }
    }

    /* renamed from: i */
    public static final void m8266i(InterfaceC8841m interfaceC8841m, LocalDate localDate, String str, DatePickerColors datePickerColors, C7328i c7328i, boolean z, Function1<? super LocalDate, Boolean> function1, Locale locale, Function1<? super LocalDate, Unit> function12, InterfaceC3921i interfaceC3921i, int i, int i2) {
        LocalDate localDate2;
        int i3;
        String str2;
        DatePickerColors datePickerColors2;
        C7328i c7328i2;
        boolean z2;
        Function1<? super LocalDate, Boolean> function13;
        Locale locale2;
        Function1<? super LocalDate, Unit> function14;
        C5917h.m3118f(interfaceC8841m, "<this>");
        C3928j mo5162p = interfaceC3921i.mo5162p(958483393);
        if ((i2 & 1) != 0) {
            localDate2 = LocalDate.now();
            C5917h.m3119e(localDate2, "now()");
            i3 = i & (-113);
        } else {
            localDate2 = localDate;
            i3 = i;
        }
        if ((i2 & 2) != 0) {
            str2 = "SELECT DATE";
        } else {
            str2 = str;
        }
        if ((i2 & 4) != 0) {
            mo5162p.mo5184e(-561826643);
            CompositionLocal compositionLocal = C1018r1.f3906a;
            datePickerColors2 = new C0200d0(((Colors) mo5162p.mo5154t(compositionLocal)).m7069h(), ((Colors) mo5162p.mo5154t(compositionLocal)).m7072e(), ((Colors) mo5162p.mo5154t(compositionLocal)).m7074c(), ((Colors) mo5162p.mo5154t(compositionLocal)).m7069h(), C8110v.f25216j, ((Colors) mo5162p.mo5154t(compositionLocal)).m7072e(), ((Colors) mo5162p.mo5154t(compositionLocal)).m7074c());
            mo5162p.m5198U(false);
            i3 &= -7169;
        } else {
            datePickerColors2 = datePickerColors;
        }
        if ((i2 & 8) != 0) {
            c7328i2 = new C7328i(1900, 2100);
            i3 &= -57345;
        } else {
            c7328i2 = c7328i;
        }
        if ((i2 & 16) != 0) {
            z2 = true;
        } else {
            z2 = z;
        }
        if ((i2 & 32) != 0) {
            function13 = C0194f.f646k;
        } else {
            function13 = function1;
        }
        if ((i2 & 64) != 0) {
            locale2 = Locale.getDefault();
            C5917h.m3119e(locale2, "getDefault()");
            i3 &= -29360129;
        } else {
            locale2 = locale;
        }
        if ((i2 & 128) != 0) {
            function14 = C0195g.f647k;
        } else {
            function14 = function12;
        }
        mo5162p.mo5184e(-492369756);
        Object m5183e0 = mo5162p.m5183e0();
        if (m5183e0 == InterfaceC3921i.C3922a.f12318a) {
            C8110v c8110v = (C8110v) interfaceC8841m.mo50d().f27270b.getValue();
            C5917h.m3121c(c8110v);
            m5183e0 = new DatePickerState(localDate2, datePickerColors2, c7328i2, c8110v.f25219a);
            mo5162p.m5209K0(m5183e0);
        }
        mo5162p.m5198U(false);
        DatePickerState datePickerState = (DatePickerState) m5183e0;
        String str3 = str2;
        Function1<? super LocalDate, Boolean> function15 = function13;
        Locale locale3 = locale2;
        m8273b(str3, datePickerState, function15, locale3, mo5162p, ((i3 >> 12) & 896) | ((i3 >> 6) & 14) | 4160);
        if (z2) {
            mo5162p.mo5184e(-412815523);
            interfaceC8841m.mo51c(new C0196h(function14, datePickerState), mo5162p, 64);
            mo5162p.m5198U(false);
        } else {
            mo5162p.mo5184e(-412815444);
            C4004w0.m5000b(datePickerState.m8265a(), new C0197i(function14, datePickerState), mo5162p);
            mo5162p.m5198U(false);
        }
        C3883b2 m5195X = mo5162p.m5195X();
        if (m5195X != null) {
            m5195X.f12208d = new C0198j(interfaceC8841m, localDate2, str2, datePickerColors2, c7328i2, z2, function13, locale2, function14, i, i2);
        }
    }
}